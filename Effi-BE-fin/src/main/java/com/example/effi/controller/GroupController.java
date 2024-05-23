package com.example.effi.controller;

import com.example.effi.domain.DTO.EmployeeDTO;
import com.example.effi.domain.DTO.GroupDTO;
import com.example.effi.domain.DTO.GroupRequestDTO;
import com.example.effi.domain.DTO.GroupResponseDTO;
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
    public ResponseEntity<GroupResponseDTO> createGroup(@RequestBody GroupRequestDTO groupRequestDTO) {
        GroupResponseDTO responseDTO = groupService.createGroup(groupRequestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    // 그룹에 구성원 추가
    @PostMapping("/{groupId}/employees")
    public ResponseEntity<Void> addEmployeesToGroup(@PathVariable Long groupId, @RequestBody List<Long> employeeIds) {
        groupService.addEmployeesToGroup(groupId, employeeIds);
        return ResponseEntity.ok().build();
    }

    // 직원 검색
    @GetMapping("/search")
    public ResponseEntity<List<EmployeeDTO>> searchEmployees(@RequestParam String name) {
        List<EmployeeDTO> employees = groupService.searchEmployeesByName(name);
        return ResponseEntity.ok(employees);
    }


    // 그룹 탈퇴
    @DeleteMapping("/{groupId}/employees/{empId}")
    public ResponseEntity<GroupResponseDTO> leaveGroup(@PathVariable Long groupId, @PathVariable Long empId) {
        GroupResponseDTO response = groupService.withdrawGroup(groupId, empId);
        return ResponseEntity.ok(response);
    }

    // 그룹 이름 변경
    @PutMapping("/{groupId}")
    public ResponseEntity<GroupResponseDTO> updateGroupName(@PathVariable Long groupId, @RequestBody UpdateGroupNameRequest request) {
        GroupResponseDTO response = groupService.updateGroupName(groupId, request.getNewGroupName());
        return ResponseEntity.ok(response);
    }

    // 그룹 삭제
    @DeleteMapping("/{groupId}")
    public ResponseEntity<GroupResponseDTO> deleteGroup(@PathVariable Long groupId) {
        GroupResponseDTO response = groupService.deleteGroup(groupId);
        return ResponseEntity.ok(response);
    }

}
