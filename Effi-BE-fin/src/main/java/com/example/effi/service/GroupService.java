package com.example.effi.service;

import com.example.effi.domain.DTO.*;
import com.example.effi.domain.Entity.Category;
import com.example.effi.domain.Entity.Employee;
import com.example.effi.domain.Entity.Group;
import com.example.effi.domain.Entity.GroupEmp;
import com.example.effi.repository.CategoryRepository;
import com.example.effi.repository.EmployeeRepository;
import com.example.effi.repository.GroupEmpRepository;
import com.example.effi.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final EmployeeRepository employeeRepository;
    private final GroupEmpRepository groupEmpRepository;
    private final CategoryRepository categoryRepository;
    private final EmployeeService employeeService;

    @Transactional
    public ResponseEntity<GlobalResponse> createGroup(GroupRequestDTO groupRequestDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long creatorEmpNo = Long.valueOf(authentication.getName());

        // 중복된 사원 번호가 있는지 확인
        Set<Long> employeeIdSet = new HashSet<>(groupRequestDTO.getEmployeeIds());
        if (employeeIdSet.size() != groupRequestDTO.getEmployeeIds().size()) {
            return ResponseEntity.badRequest().body(GlobalResponse.builder()
                    .message("중복된 사원 번호가 포함되어 있습니다.")
                    .status(HttpStatus.BAD_REQUEST.value())
                    .build());
        }

        Group group = Group.builder()
                .groupName(groupRequestDTO.getGroupName())
                .deleteYn(false)
                .createdAt(Date.valueOf(LocalDate.now()))
                .build();
        Group savedGroup = groupRepository.save(group);

        Employee creator = employeeRepository.findByEmpNo(creatorEmpNo);
        if (creator == null) {
            return ResponseEntity.badRequest().body(GlobalResponse.builder()
                    .message("유효하지 않은 생성자 직원 번호: " + creatorEmpNo)
                    .status(HttpStatus.BAD_REQUEST.value())
                    .build());
        }

        GroupEmp groupLeader = GroupEmp.builder()
                .group(savedGroup)
                .employee(creator)
                .groupEmpRank("Leader")
                .deleteYn(false)
                .build();
        groupEmpRepository.save(groupLeader);

        addEmployeesToGroup(savedGroup.getGroupId(), groupRequestDTO.getEmployeeIds());

        GroupResponseDTO responseDTO = GroupResponseDTO.builder()
                .groupId(savedGroup.getGroupId())
                .groupName(savedGroup.getGroupName())
                .employeeIds(groupRequestDTO.getEmployeeIds())
                .build();

        return ResponseEntity.ok().body(GlobalResponse.builder()
                .message("그룹 생성 성공")
                .status(HttpStatus.OK.value())
                .data(responseDTO)
                .build());
    }

    @Transactional
    public ResponseEntity<GlobalResponse> addEmployeesToGroup(Long groupId, List<Long> employeeIds) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 그룹 ID: " + groupId));

        // 메서드 호출 내에서 중복된 사원 번호가 있는지 확인
        Set<Long> employeeIdSet = new HashSet<>(employeeIds);
        if (employeeIdSet.size() != employeeIds.size()) {
            return ResponseEntity.badRequest().body(GlobalResponse.builder()
                    .message("중복된 사원 번호가 포함되어 있습니다.")
                    .status(HttpStatus.BAD_REQUEST.value())
                    .build());
        }

        for (Long empId : employeeIds) {
            Employee employee = employeeRepository.findById(empId)
                    .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 직원 ID: " + empId));

            GroupEmp groupEmp = GroupEmp.builder()
                    .group(group)
                    .employee(employee)
                    .groupEmpRank("Member")
                    .deleteYn(false)
                    .build();

            groupEmpRepository.save(groupEmp);
        }

        return ResponseEntity.ok().body(GlobalResponse.builder()
                .message("구성원 추가 성공")
                .status(HttpStatus.OK.value())
                .build());
    }

    // 그룹 이름 변경
    @Transactional
    public ResponseEntity<GlobalResponse> updateGroupName(Long groupId, String newGroupName) {
      Group group = groupRepository.findById(groupId)
          .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 그룹 ID: " + groupId));

      if (groupRepository.findByGroupName(newGroupName).isPresent()) {
        throw new IllegalArgumentException("이미 존재하는 그룹 이름입니다.");
      }

      Group updatedGroup = group.updateGroupName(newGroupName);
      Group savedGroup = groupRepository.save(updatedGroup);

      return ResponseEntity.ok().body(GlobalResponse.builder()
          .message("그룹 이름 변경 성공")
          .status(HttpStatus.OK.value())
          .data(GroupResponseDTO.builder()
              .groupName(savedGroup.getGroupName())
              .build())
          .build());
    }

    @Transactional
    public ResponseEntity<GlobalResponse> withdrawGroup(Long groupId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long creatorEmpNo = Long.valueOf(authentication.getName());
        Long empId = employeeService.findEmpIdByEmpNo(creatorEmpNo);

        Group group = groupRepository.findById(groupId)
          .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 그룹 ID: " + groupId));

      groupEmpRepository.updateDeleteYnByGroupIdAndEmployeeId(groupId, empId);

      // 그룹에 남아있는 구성원이 있는지 확인
      Long activeMemberCount = groupEmpRepository.countActiveMembersInGroup(groupId);
      if (activeMemberCount == 0) {
        Group deletedGroup = group.markAsDeleted();
        groupRepository.save(deletedGroup);
        return ResponseEntity.ok().body(GlobalResponse.builder()
            .message("모든 구성원이 그룹을 떠났기 때문에 그룹이 삭제되었습니다.")
            .status(HttpStatus.OK.value())
            .data(GroupResponseDTO.builder()
                .groupName(deletedGroup.getGroupName())
                .build())
            .build());
      }

        return ResponseEntity.ok().body(GlobalResponse.builder()
                .message("그룹 탈퇴 성공")
                .status(HttpStatus.OK.value())
                .data(GroupResponseDTO.builder()
                        .groupName(group.getGroupName())
                        .build())
                .build());
    }

    @Transactional
    public ResponseEntity<GlobalResponse> deleteGroup(Long groupId) {
      // 그룹이 존재하지 않을 경우 예외를 던짐
      Group group = groupRepository.findById(groupId)
          .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 그룹 ID: " + groupId));

      // 그룹이 존재할 경우 그룹과 구성원 삭제
      deleteGroupAndMembers(groupId);

      return ResponseEntity.ok().body(GlobalResponse.builder()
          .message("그룹 삭제 성공")
          .status(HttpStatus.OK.value())
          .data(GroupResponseDTO.builder()
              .groupName(null)
              .build())
          .build());
    }

    // 그룹과 구성원 삭제
    @Transactional
    public void deleteGroupAndMembers(Long groupId) {
        // 그룹 구성원을 모두 삭제
        groupEmpRepository.deleteAllByGroupId(groupId);

        // 그룹을 삭제
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 그룹 ID: " + groupId));
        Group deletedGroup = group.markAsDeleted();
        groupRepository.save(deletedGroup);
    }

    // 그룹에 속한 구성원 목록 조회
    public List<EmployeeDTO> searchEmployeesByName(String name) {
        return employeeRepository.findByNameContaining(name).stream()
                .map(employee -> EmployeeDTO.builder()
                        .id(employee.getId())
                        .empNo(employee.getEmpNo())
                        .company(employee.getCompany())
                        .name(employee.getName())
                        .email(employee.getEmail())
                        .phoneNum(employee.getPhoneNum())
                        .extensionNum(employee.getExtensionNum())
                        .rank(employee.getRank())
                        .password(employee.getPassword())
                        .deptId(employee.getDept().getDeptId())
                        .build())
                .collect(Collectors.toList());
    }

    // groupId로 속한 empId 리턴
    @Transactional
    public List<Long> findEmployeeIdsByGroupId(Long groupId) {
        List<GroupEmp> lst = groupEmpRepository.findAllByGroup_GroupId(groupId);
        List<Long> employeeIds = new ArrayList<Long>();
        for (GroupEmp groupEmp : lst) {
            employeeIds.add(groupEmp.getEmployee().getId());
        }
        return employeeIds;
    }

    // groupId로 group 찾기
    @Transactional
    public GroupDTO findGroupById(Long groupId) {
      Group group = groupRepository.findById(groupId)
          .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 그룹 ID: " + groupId));
      return new GroupDTO(group);
    }

    // 모든 그룹 조회
    @Transactional
    public List<GroupDTO> findAllGroups() {
        List<Group> lst = groupRepository.findAll();
        List<GroupDTO> groupDTOs = new ArrayList<GroupDTO>();
        for (Group group : lst) {
            groupDTOs.add(new GroupDTO(group));
        }
        return groupDTOs;
    }

    // groupId로 groupLeader 찾기
    @Transactional
    public Boolean findGroupLeader(Long groupId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long creatorEmpNo = Long.valueOf(authentication.getName());
        Long empId = Long.valueOf(authentication.getName());

        GroupEmp groupEmp = groupEmpRepository.findByGroup_GroupIdAndEmployee_Id(groupRepository.findById(groupId).get().getGroupId(),
                employeeRepository.findByEmpNo(creatorEmpNo).getEmpNo());
        if (groupEmp != null) {
            if(groupEmp.getGroupEmpRank().equals("Leader"))
                return true;
        }
        return false;
    }

    // 내 그룹 찾기\
    @Transactional
    public List<GroupNameDTO> findMyGroup(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long creatorEmpNo = Long.valueOf(authentication.getName());
        Long empId = employeeService.findEmpIdByEmpNo(creatorEmpNo);

        List<GroupEmp> allByEmployeeId = groupEmpRepository.findAllByEmployee_Id(empId);
        List<GroupNameDTO> groupNameDTOS = new ArrayList<>();
        for (GroupEmp groupEmp : allByEmployeeId) {
            if (groupEmp.getDeleteYn() == false) {
                GroupDTO groupById = findGroupById(groupEmp.getGroup().getGroupId());
                groupNameDTOS.add(new GroupNameDTO(groupById.getGroupId(), groupById.getGroupName()));
            }
        }
        return groupNameDTOS;
    }

    // 그룹 이름으로 그룹 찾아서 그룹 정보 반환
    @Transactional
    public GroupDTO findGroupByName(String groupName) {
        Group group = groupRepository.findByGroupName(groupName)
            .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 그룹 이름: " + groupName));
        return new GroupDTO(group);
    }

}
