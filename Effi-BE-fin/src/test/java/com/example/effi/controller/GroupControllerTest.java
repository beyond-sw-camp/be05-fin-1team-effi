package com.example.effi.controller;

import com.example.effi.domain.DTO.EmployeeDTO;
import com.example.effi.domain.DTO.GlobalResponse;
import com.example.effi.domain.DTO.GroupRequestDTO;
import com.example.effi.domain.DTO.GroupResponseDTO;
import com.example.effi.domain.DTO.UpdateGroupNameRequest;
import com.example.effi.service.GroupService;
import com.example.effi.config.TokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
    private TokenProvider tokenProvider;

    @Autowired
    private WebApplicationContext context;

    private String jwtToken;

    @BeforeEach
    void setUp() {
        jwtToken = "Bearer test.jwt.token";
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @DisplayName("그룹 생성 성공 테스트")
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

        when(groupService.createGroup(any(GroupRequestDTO.class))).thenReturn(ResponseEntity.ok(globalResponse));

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
    @DisplayName("구성원 추가 성공 테스트")
    @WithMockUser
    void addEmployeesToGroup_success() throws Exception {
        GlobalResponse globalResponse = GlobalResponse.builder()
                .message("구성원 추가 성공")
                .status(200)
                .build();

        when(groupService.addEmployeesToGroup(anyLong(), any(List.class))).thenReturn(ResponseEntity.ok(globalResponse));

        mockMvc.perform(post("/api/groups/1/employees")
                        .header(HttpHeaders.AUTHORIZATION, jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[2, 3]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("구성원 추가 성공"))
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    @DisplayName("직원 이름으로 검색 성공 테스트")
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
    @DisplayName("그룹 탈퇴 성공 테스트")
    @WithMockUser
    void leaveGroup_success() throws Exception {
        GlobalResponse globalResponse = GlobalResponse.builder()
                .message("그룹 탈퇴 성공")
                .status(200)
                .build();

        when(groupService.withdrawGroup(anyLong(), anyLong())).thenReturn(ResponseEntity.ok(globalResponse));

        mockMvc.perform(delete("/api/groups/1/employees/2")
                        .header(HttpHeaders.AUTHORIZATION, jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("그룹 탈퇴 성공"))
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    @DisplayName("그룹 이름 변경 성공 테스트")
    @WithMockUser
    void updateGroupName_success() throws Exception {
        GlobalResponse globalResponse = GlobalResponse.builder()
                .message("그룹 이름 변경 성공")
                .status(200)
                .build();

        when(groupService.updateGroupName(anyLong(), any(String.class))).thenReturn(ResponseEntity.ok(globalResponse));

        mockMvc.perform(put("/api/groups/1")
                        .header(HttpHeaders.AUTHORIZATION, jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"newGroupName\": \"Updated Group Name\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("그룹 이름 변경 성공"))
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    @DisplayName("그룹 삭제 성공 테스트")
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
}
