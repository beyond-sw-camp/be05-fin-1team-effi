package com.example.effi.service;

import com.example.effi.domain.DTO.EmployeeDTO;
import com.example.effi.domain.DTO.GlobalResponse;
import com.example.effi.domain.DTO.GroupDTO;
import com.example.effi.domain.DTO.GroupRequestDTO;
import com.example.effi.domain.DTO.GroupResponseDTO;
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

        Category category = categoryRepository.findByCategoryId(3)
                .orElseThrow(() -> new IllegalArgumentException("category_id가 3인 카테고리를 찾을 수 없습니다."));

        Group group = Group.builder()
                .groupName(groupRequestDTO.getGroupName())
                .category(category)
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

    @Transactional
    public ResponseEntity<GlobalResponse> updateGroupName(Long groupId, String newGroupName) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 그룹 ID: " + groupId));

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
    public ResponseEntity<GlobalResponse> withdrawGroup(Long groupId, Long empId) {
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
        deleteGroupAndMembers(groupId);
        return ResponseEntity.ok().body(GlobalResponse.builder()
                .message("그룹 삭제 성공")
                .status(HttpStatus.OK.value())
                .data(GroupResponseDTO.builder()
                        .groupName(null)
                        .build())
                .build());
    }

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
        Group grp = groupRepository.findById(groupId).get();
        return new GroupDTO(grp);
    }

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
}
