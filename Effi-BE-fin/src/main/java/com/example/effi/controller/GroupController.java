package com.example.effi.controller;

import com.example.effi.domain.DTO.*;
import com.example.effi.service.EmployeeService;
import com.example.effi.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;
    private final EmployeeService employeeService;

    // 그룹 생성
    @PostMapping
    public ResponseEntity<?> createGroup(@RequestBody GroupRequestDTO groupRequestDTO) {
        if (groupRequestDTO.getEmployeeIds() == null || groupRequestDTO.getEmployeeIds().isEmpty()) {
            return ResponseEntity.badRequest().body("Employee list cannot be null or empty");
        }
        return groupService.createGroup(groupRequestDTO);
    }

    // 그룹에 구성원 추가
    @PostMapping("/{groupId}/employees")
    public ResponseEntity<GlobalResponse> addEmployeesToGroup(@PathVariable Long groupId,
            @RequestBody List<Long> employeeIds) {
        return groupService.addEmployeesToGroup(groupId, employeeIds);
    }

    // 직원 검색
    @GetMapping("/search")
    public ResponseEntity<List<EmployeeDTO>> searchEmployees(@RequestParam String name) {
        List<EmployeeDTO> employees = groupService.searchEmployeesByName(name);
        return ResponseEntity.ok(employees != null ? employees : Collections.emptyList());
    }

    // 그룹 탈퇴
    @DeleteMapping("/{groupId}/employees")
    public ResponseEntity<GlobalResponse> leaveGroup(@PathVariable Long groupId) {
        return groupService.withdrawGroup(groupId);
    }

    // 그룹 이름 변경
    @PutMapping("/{groupId}")
    public ResponseEntity<GlobalResponse> updateGroupName(@PathVariable Long groupId,
            @RequestBody UpdateGroupNameRequest request) {
        return groupService.updateGroupName(groupId, request.getNewGroupName());
    }

    // 그룹 삭제
    @DeleteMapping("/{groupId}")
    public ResponseEntity<GlobalResponse> deleteGroup(@PathVariable Long groupId) {
        return groupService.deleteGroup(groupId);
    }

    // 모든 그룹 조회
    @GetMapping("/all")
    public ResponseEntity<List<GroupDTO>> findAllGroups() {
        return ResponseEntity.ok(groupService.findAllGroups());
    }

    // 그룹 id로 그룹 구성원 찾기
    @GetMapping("/find/{groupId}")
    public ResponseEntity<List<EmployeeDTO>> findGroupById(@PathVariable Long groupId) {
        try {
            List<Long> empNolst = groupService.findEmployeeIdsByGroupId(groupId);
            List<EmployeeDTO> emplist = new ArrayList<>();
            for (Long empId : empNolst) {
                emplist.add(employeeService.findById(empId));
            }
            return ResponseEntity.ok(emplist);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 내가 속한 그룹 찾기
    @GetMapping("/find/myGroup")
    public ResponseEntity<List<GroupNameDTO>> findMyGroups() {
        List<GroupNameDTO> myGroup = groupService.findMyGroup();
        return ResponseEntity.ok(myGroup);
    }

    // 그룹 이름으로 그룹 찾아서 그룹 정보 반환
    @GetMapping("/find/name/{groupName}")
    public ResponseEntity<GroupDTO> findGroupByName(@PathVariable String groupName) {
        return ResponseEntity.ok(groupService.findGroupByName(groupName));
    }
}
