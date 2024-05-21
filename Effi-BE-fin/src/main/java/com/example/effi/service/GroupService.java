package com.example.effi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.effi.domain.DTO.EmployeeDTO;
import com.example.effi.domain.DTO.GroupRequestDTO;
import com.example.effi.domain.DTO.GroupResponseDTO;
import com.example.effi.domain.Entitiy.Employee;
import com.example.effi.domain.Entitiy.Group;
import com.example.effi.domain.Entitiy.GroupEmp;
import com.example.effi.repository.EmployeeRepository;
import com.example.effi.repository.GroupEmpRepository;
import com.example.effi.repository.GroupRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final EmployeeRepository employeeRepository;
    private final GroupEmpRepository groupEmpRepository;

    @Transactional
    public GroupResponseDTO createGroup(GroupRequestDTO groupRequestDTO) {
        Group group = Group.builder()
                .groupName(groupRequestDTO.getGroupName())
                .deleteYn(false)
                .build();
        Group savedGroup = groupRepository.save(group);

        // 직원들을 그룹에 추가
        addEmployeesToGroup(savedGroup.getGroupId(), groupRequestDTO.getEmployeeIds());

        return GroupResponseDTO.builder()
                .groupId(savedGroup.getGroupId())
                .groupName(savedGroup.getGroupName())
                .msg("그룹 생성 성공")
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

    public List<EmployeeDTO> searchEmployeesByName(String name) {
        return employeeRepository.findByNameContaining(name).stream()
                .map((Employee employee) -> EmployeeDTO.builder()
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