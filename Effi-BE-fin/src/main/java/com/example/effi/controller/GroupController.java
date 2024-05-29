package com.example.effi.controller;

import com.example.effi.domain.DTO.EmployeeDTO;
import com.example.effi.domain.DTO.GlobalResponse;
import com.example.effi.domain.DTO.GroupRequestDTO;
import com.example.effi.domain.DTO.UpdateGroupNameRequest;
import com.example.effi.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;

    // 그룹 생성
    @PostMapping
    public ResponseEntity<GlobalResponse> createGroup(@RequestBody GroupRequestDTO groupRequestDTO) {
        return groupService.createGroup(groupRequestDTO);
    }

    // 그룹에 구성원 추가
    @PostMapping("/{groupId}/employees")
    public ResponseEntity<GlobalResponse> addEmployeesToGroup(@PathVariable Long groupId, @RequestBody List<Long> employeeIds) {
        return groupService.addEmployeesToGroup(groupId, employeeIds);
    }

    // 직원 검색
    @GetMapping("/search")
    public ResponseEntity<List<EmployeeDTO>> searchEmployees(@RequestParam String name) {
        List<EmployeeDTO> employees = groupService.searchEmployeesByName(name);
        return ResponseEntity.ok(employees);
    }

    // 그룹 탈퇴
    @DeleteMapping("/{groupId}/employees/{empId}")
    public ResponseEntity<GlobalResponse> leaveGroup(@PathVariable Long groupId, @PathVariable Long empId) {
        return groupService.withdrawGroup(groupId, empId);
    }

    // 그룹 이름 변경
    @PutMapping("/{groupId}")
    public ResponseEntity<GlobalResponse> updateGroupName(@PathVariable Long groupId, @RequestBody UpdateGroupNameRequest request) {
        return groupService.updateGroupName(groupId, request.getNewGroupName());
    }

    // 그룹 삭제
    @DeleteMapping("/{groupId}")
    public ResponseEntity<GlobalResponse> deleteGroup(@PathVariable Long groupId) {
        return groupService.deleteGroup(groupId);
    }
}
