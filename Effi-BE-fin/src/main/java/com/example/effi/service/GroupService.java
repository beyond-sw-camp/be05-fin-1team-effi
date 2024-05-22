package com.example.effi.service;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.effi.domain.DTO.EmployeeDTO;
import com.example.effi.domain.DTO.GroupDTO;
import com.example.effi.domain.DTO.GroupRequestDTO;
import com.example.effi.domain.DTO.GroupResponseDTO;
import com.example.effi.domain.DTO.UpdateGroupNameRequest;
import com.example.effi.domain.Entitiy.Category;
import com.example.effi.domain.Entitiy.Employee;
import com.example.effi.domain.Entitiy.Group;
import com.example.effi.domain.Entitiy.GroupEmp;
import com.example.effi.repository.CategoryRepository;
import com.example.effi.repository.EmployeeRepository;
import com.example.effi.repository.GroupEmpRepository;
import com.example.effi.repository.GroupRepository;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
        // 현재 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long creatorEmpNo = Long.valueOf(authentication.getName()); // empNo가 인증 토큰의 이름에 저장된 경우

        // 카테고리 테이블에서 category_id가 3인 카테고리를 가져옵니다.
        Category category = categoryRepository.findByCategoryId(3)
                .orElseThrow(() -> new IllegalArgumentException("category_id가 3인 카테고리를 찾을 수 없습니다."));

        Group group = Group.builder()
                .groupName(groupRequestDTO.getGroupName())
                .category(category) // 가져온 카테고리를 설정합니다.
                .deleteYn(false)
                .build();
        Group savedGroup = groupRepository.save(group);

        // 그룹 생성자를 그룹장으로 추가
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

        // 직원들을 그룹에 추가
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

   // 그룹 이름 변경
    @Transactional
    public GroupResponseDTO updateGroupName(Long groupId, String newGroupName) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 그룹 ID: " + groupId));

        // 현재 날짜를 가져와서 날짜 객체로 변환
        Date updatedAt = Date.valueOf(LocalDate.now());

        // 그룹의 이름과 업데이트된 날짜를 설정하여 엔티티를 업데이트
        group.setGroupName(newGroupName);
        group.setUpdatedAt(updatedAt);

        // 업데이트된 그룹을 저장하고 응답을 생성하여 반환
        Group savedGroup = groupRepository.save(group);
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

        // GroupEmp 엔티티에서 해당 구성원의 deleteYn을 1로 업데이트
        groupEmpRepository.updateDeleteYnByGroupIdAndEmployeeId(groupId, empId);

        return GroupResponseDTO.builder()
                .code("200")
                .message("그룹 탈퇴 성공")
                .groupName(group.getGroupName())
                .build();
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
}
