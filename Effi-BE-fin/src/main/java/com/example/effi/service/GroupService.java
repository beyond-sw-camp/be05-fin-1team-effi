package com.example.effi.service;
import com.example.effi.domain.DTO.EmployeeDTO;
import com.example.effi.domain.DTO.GroupDTO;
import com.example.effi.domain.DTO.GroupRequestDTO;
import com.example.effi.domain.DTO.GroupResponseDTO;
import com.example.effi.domain.Entitiy.Category;
import com.example.effi.domain.Entitiy.Employee;
import com.example.effi.domain.Entitiy.Group;
import com.example.effi.domain.Entitiy.GroupEmp;
import com.example.effi.repository.CategoryRepository;
import com.example.effi.repository.EmployeeRepository;
import com.example.effi.repository.GroupEmpRepository;
import com.example.effi.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final EmployeeRepository employeeRepository;
    private final GroupEmpRepository groupEmpRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public GroupResponseDTO createGroup(GroupRequestDTO groupRequestDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long creatorEmpNo = Long.valueOf(authentication.getName());

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
            throw new IllegalArgumentException("유효하지 않은 생성자 직원 번호: " + creatorEmpNo);
        }

        GroupEmp groupLeader = GroupEmp.builder()
                .group(savedGroup)
                .employee(creator)
                .groupEmpRank("Leader")
                .deleteYn(false)
                .build();
        groupEmpRepository.save(groupLeader);

        addEmployeesToGroup(savedGroup.getGroupId(), groupRequestDTO.getEmployeeIds());

        return GroupResponseDTO.builder()
                .code("200")
                .message("그룹 생성 성공")
                .groupName(savedGroup.getGroupName())
                .build();
    }

    @Transactional
    public void addEmployeesToGroup(Long groupId, List<Long> employeeIds) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 그룹 ID: " + groupId));

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
    }

    @Transactional
    public GroupResponseDTO updateGroupName(Long groupId, String newGroupName) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 그룹 ID: " + groupId));

        Group updatedGroup = group.updateGroupName(newGroupName);
        Group savedGroup = groupRepository.save(updatedGroup);

        return GroupResponseDTO.builder()
                .code("200")
                .message("그룹 이름 변경 성공")
                .groupName(savedGroup.getGroupName())
                .build();
    }

    @Transactional
    public GroupResponseDTO withdrawGroup(Long groupId, Long empId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 그룹 ID: " + groupId));

        groupEmpRepository.updateDeleteYnByGroupIdAndEmployeeId(groupId, empId);

        // 그룹에 남아있는 구성원이 있는지 확인
        Long activeMemberCount = groupEmpRepository.countActiveMembersInGroup(groupId);
        if (activeMemberCount == 0) {
            Group deletedGroup = group.markAsDeleted();
            groupRepository.save(deletedGroup);
            return GroupResponseDTO.builder()
                    .code("200")
                    .message("모든 구성원이 그룹을 떠났기 때문에 그룹이 삭제되었습니다.")
                    .groupName(deletedGroup.getGroupName())
                    .build();
        }

        return GroupResponseDTO.builder()
                .code("200")
                .message("그룹 탈퇴 성공")
                .groupName(group.getGroupName())
                .build();
    }

    @Transactional
    public GroupResponseDTO deleteGroup(Long groupId) {
        deleteGroupAndMembers(groupId);
        return GroupResponseDTO.builder()
                .code("200")
                .message("그룹 삭제 성공")
                .groupName(null)
                .build();
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
    public List<Long> findEmployeeIdsByGroupId(Long groupId) {
        List<GroupEmp> lst = groupEmpRepository.findAllByGroup_GroupId(groupId);
        List<Long> employeeIds = new ArrayList<>();
        for (GroupEmp groupEmp : lst) {
            employeeIds.add(groupEmp.getEmployee().getId());
        }
        return employeeIds;
    }

    // groupId로 group 찾기
    public GroupDTO findGroupById(Long groupId) {
        Group grp = groupRepository.findById(groupId).get();
        return new GroupDTO(grp);
    }
}