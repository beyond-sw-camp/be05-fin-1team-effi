package com.example.effi.controller;

import com.example.effi.domain.DTO.*;
import com.example.effi.service.EmployeeService;
import com.example.effi.service.GroupService;
import com.example.effi.config.TokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GroupController.class)
class GroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GroupService groupService;

    @MockBean
    private EmployeeService employeeService;

    @MockBean
    private TokenProvider tokenProvider;

    @Autowired
    private WebApplicationContext context;

    private String jwtToken;

    @BeforeEach
    void setUp() {
        jwtToken = "Bearer test.jwt.token";
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @DisplayName("그룹 생성 테스트 - 성공")
    @WithMockUser
    void createGroup_success() throws Exception {
        GroupRequestDTO requestDTO = GroupRequestDTO.builder()
                .groupName("Example Group")
                .employeeIds(Arrays.asList(2L, 3L))
                .build();

        GroupResponseDTO responseDTO = GroupResponseDTO.builder()
                .groupName("Example Group")
                .employeeIds(Arrays.asList(2L, 3L))
                .build();

        GlobalResponse globalResponse = GlobalResponse.builder()
                .message("그룹 생성 성공")
                .status(200)
                .data(responseDTO)
                .build();

        when(groupService.createGroup(any(GroupRequestDTO.class)))
                .thenReturn(ResponseEntity.ok(globalResponse));

        mockMvc.perform(post("/api/groups")
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"groupName\": \"Example Group\", \"employeeIds\": [2, 3] }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("그룹 생성 성공"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.groupName").value("Example Group"))
                .andExpect(jsonPath("$.data.employeeIds").isArray());
    }

    @Test
    @DisplayName("그룹 생성 테스트 - 실패")
    @WithMockUser
    void createGroup_fail() throws Exception {
        GroupRequestDTO requestDTO = GroupRequestDTO.builder()
                .groupName("Example Group")
                .employeeIds(Arrays.asList(2L, 3L))
                .build();

        GlobalResponse globalResponse = GlobalResponse.builder()
                .message("그룹 생성 실패")
                .status(400)
                .build();

        when(groupService.createGroup(any(GroupRequestDTO.class)))
                .thenReturn(ResponseEntity.badRequest().body(globalResponse));

        mockMvc.perform(post("/api/groups")
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"groupName\": \"Example Group\", \"employeeIds\": [2, 3] }"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("그룹 생성 실패"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("구성원 추가 테스트 - 성공")
    @WithMockUser
    void addEmployeesToGroup_success() throws Exception {
        GlobalResponse globalResponse = GlobalResponse.builder()
                .message("구성원 추가 성공")
                .status(200)
                .build();

        when(groupService.addEmployeesToGroup(anyLong(), any(List.class)))
                .thenReturn(ResponseEntity.ok(globalResponse));

        mockMvc.perform(post("/api/groups/1/employees")
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("[2, 3]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("구성원 추가 성공"))
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    @DisplayName("구성원 추가 테스트 - 실패")
    @WithMockUser
    void addEmployeesToGroup_fail() throws Exception {
        GlobalResponse globalResponse = GlobalResponse.builder()
                .message("구성원 추가 실패")
                .status(400)
                .build();

        when(groupService.addEmployeesToGroup(anyLong(), any(List.class)))
                .thenReturn(ResponseEntity.badRequest().body(globalResponse));

        mockMvc.perform(post("/api/groups/1/employees")
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("[2, 3]"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("구성원 추가 실패"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("직원 이름으로 검색 테스트 - 성공")
    @WithMockUser
    void searchEmployeesByName_success() throws Exception {
        EmployeeDTO employee = EmployeeDTO.builder()
                .id(1L)
                .empNo(123L)
                .company("Example Company")
                .name("John Doe")
                .email("john@example.com")
                .phoneNum("123-456-7890")
                .extensionNum("123")
                .rank("Manager")
                .build();

        List<EmployeeDTO> employees = Arrays.asList(employee);

        when(groupService.searchEmployeesByName("John")).thenReturn(employees);

        mockMvc.perform(get("/api/groups/search")
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .param("name", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("John Doe"));
    }

    @Test
    @DisplayName("직원 이름으로 검색 테스트 - 직원 이름이 존재하지 않아 검색 결과가 비어 있는 경우 - ")
    @WithMockUser
    void searchEmployeesByName_noResults() throws Exception {
        when(groupService.searchEmployeesByName("John")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/groups/search")
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .param("name", "John"))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    @DisplayName("직원 이름으로 검색 테스트 - 서비스가 null을 반환하는 경우 실패")
    @WithMockUser
    void searchEmployeesByName_nullResult() throws Exception {
        when(groupService.searchEmployeesByName("John")).thenReturn(null);

        mockMvc.perform(get("/api/groups/search")
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .param("name", "John"))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    @DisplayName("그룹 탈퇴 테스트 - 성공")
    @WithMockUser
    void leaveGroup_success() throws Exception {
        GlobalResponse globalResponse = GlobalResponse.builder()
                .message("그룹 탈퇴 성공")
                .status(200)
                .build();

        when(groupService.withdrawGroup(anyLong())).thenReturn(ResponseEntity.ok(globalResponse));

        mockMvc.perform(delete("/api/groups/1/employees")
                        .header(HttpHeaders.AUTHORIZATION, jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("그룹 탈퇴 성공"))
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    @DisplayName("그룹 탈퇴 테스트 - 실패")
    @WithMockUser
    void leaveGroup_fail() throws Exception {
        GlobalResponse globalResponse = GlobalResponse.builder()
                .message("그룹 탈퇴 실패")
                .status(400)
                .build();

        when(groupService.withdrawGroup(anyLong()))
                .thenReturn(ResponseEntity.badRequest().body(globalResponse));

        mockMvc.perform(delete("/api/groups/1/employees")
                        .header(HttpHeaders.AUTHORIZATION, jwtToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("그룹 탈퇴 실패"))
                .andExpect(jsonPath("$.status").value(400));
    }


    @Test
    @DisplayName("그룹 이름 변경 테스트 - 성공")
    @WithMockUser
    void updateGroupName_success() throws Exception {
        GlobalResponse globalResponse = GlobalResponse.builder()
                .message("그룹 이름 변경 성공")
                .status(200)
                .build();

        when(groupService.updateGroupName(anyLong(), any(String.class)))
                .thenReturn(ResponseEntity.ok(globalResponse));

        mockMvc.perform(put("/api/groups/1")
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"newGroupName\": \"Updated Group Name\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("그룹 이름 변경 성공"))
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    @DisplayName("그룹 이름 변경 테스트 - 실패")
    @WithMockUser
    void updateGroupName_fail() throws Exception {
        GlobalResponse globalResponse = GlobalResponse.builder()
                .message("그룹 이름 변경 실패")
                .status(400)
                .build();

        when(groupService.updateGroupName(anyLong(), any(String.class)))
                .thenReturn(ResponseEntity.badRequest().body(globalResponse));

        mockMvc.perform(put("/api/groups/1")
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"newGroupName\": \"Updated Group Name\" }"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("그룹 이름 변경 실패"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("그룹 삭제 테스트 - 성공")
    @WithMockUser
    void deleteGroup_success() throws Exception {
        GlobalResponse globalResponse = GlobalResponse.builder()
                .message("그룹 삭제 성공")
                .status(200)
                .build();

        when(groupService.deleteGroup(anyLong())).thenReturn(ResponseEntity.ok(globalResponse));

        mockMvc.perform(delete("/api/groups/1")
                .header(HttpHeaders.AUTHORIZATION, jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("그룹 삭제 성공"))
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    @DisplayName("그룹 삭제 테스트 - 실패")
    @WithMockUser
    void deleteGroup_fail() throws Exception {
        GlobalResponse globalResponse = GlobalResponse.builder()
                .message("그룹 삭제 실패")
                .status(400)
                .build();

        when(groupService.deleteGroup(anyLong())).thenReturn(ResponseEntity.badRequest().body(globalResponse));

        mockMvc.perform(delete("/api/groups/1")
                .header(HttpHeaders.AUTHORIZATION, jwtToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("그룹 삭제 실패"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("모든 그룹 조회 - 성공")
    @WithMockUser
    void findAllGroups_success() throws Exception {
        GroupDTO group = new GroupDTO(1L, "Example Group", false);

        List<GroupDTO> groups = Arrays.asList(group);

        when(groupService.findAllGroups()).thenReturn(groups);

        mockMvc.perform(get("/api/groups/all")
                .header(HttpHeaders.AUTHORIZATION, jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].groupName").value("Example Group"));
    }

    @Test
    @DisplayName("그룹 id로 그룹 구성원 찾기 - 성공")
    @WithMockUser
    void findGroupById_success() throws Exception {
        EmployeeDTO employee = EmployeeDTO.builder()
                .id(1L)
                .empNo(123L)
                .company("Example Company")
                .name("John Doe")
                .email("john@example.com")
                .phoneNum("123-456-7890")
                .extensionNum("123")
                .rank("Manager")
                .build();

        List<Long> empIds = Arrays.asList(1L);
        List<EmployeeDTO> employees = Arrays.asList(employee);

        when(groupService.findEmployeeIdsByGroupId(anyLong())).thenReturn(empIds);
        when(employeeService.findById(anyLong())).thenReturn(employee);

        mockMvc.perform(get("/api/groups/find/{groupId}", 1L)
                .header(HttpHeaders.AUTHORIZATION, jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("John Doe"));
    }

    @Test
    @DisplayName("그룹 id로 그룹 구성원 찾기 - 실패")
    @WithMockUser
    void findGroupById_failure() throws Exception {
        when(groupService.findEmployeeIdsByGroupId(anyLong())).thenThrow(new RuntimeException("Group not found"));

        mockMvc.perform(get("/api/groups/find/{groupId}", 1L)
                .header(HttpHeaders.AUTHORIZATION, jwtToken))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("내가 속한 그룹 찾기 - 성공")
    @WithMockUser
    void findMyGroups_success() throws Exception {
        GroupNameDTO group = new GroupNameDTO(1L, "Example Group");
        List<GroupNameDTO> myGroups = Arrays.asList(group);

        when(groupService.findMyGroup()).thenReturn(myGroups);

        mockMvc.perform(get("/api/groups/find/myGroup")
                .header(HttpHeaders.AUTHORIZATION, jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].groupName").value("Example Group"));
    }

    @Test
    @DisplayName("그룹 이름으로 그룹 찾아서 그룹 정보 반환 - 성공")
    @WithMockUser
    void findGroupByName_success() throws Exception {
        GroupDTO group = new GroupDTO(1L, "Example Group", false);

        when(groupService.findGroupByName(anyString())).thenReturn(group);

        mockMvc.perform(get("/api/groups/find/name/{groupName}", "Example Group")
                .header(HttpHeaders.AUTHORIZATION, jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.groupName").value("Example Group"));
    }

    @Test
    @DisplayName("그룹 이름으로 그룹 찾아서 그룹 정보 반환 - 실패")
    @WithMockUser
    void findGroupByName_failure() throws Exception {
        when(groupService.findGroupByName(anyString())).thenThrow(new RuntimeException("Group not found"));

        mockMvc.perform(get("/api/groups/find/name/{groupName}", "Example Group")
                .header(HttpHeaders.AUTHORIZATION, jwtToken))
                .andExpect(status().isInternalServerError());
    }

}
