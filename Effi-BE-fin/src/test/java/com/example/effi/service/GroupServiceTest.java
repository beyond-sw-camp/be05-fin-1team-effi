package com.example.effi.service;

import com.example.effi.domain.DTO.*;
import com.example.effi.domain.Entity.Category;
import com.example.effi.domain.Entity.Dept;
import com.example.effi.domain.Entity.Employee;
import com.example.effi.domain.Entity.Group;
import com.example.effi.domain.Entity.GroupEmp;
import com.example.effi.repository.CategoryRepository;
import com.example.effi.repository.EmployeeRepository;
import com.example.effi.repository.GroupEmpRepository;
import com.example.effi.repository.GroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class GroupServiceTest {

    @InjectMocks
    private GroupService groupService;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private GroupEmpRepository groupEmpRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("1"); // mock employee number
        when(employeeService.findEmpIdByEmpNo(1L)).thenReturn(1L); // mock employee ID
    }

    @DisplayName("그룹 생성 서비스 테스트 - 성공")
    @Test
    void createGroup_success() {
        GroupRequestDTO requestDTO = GroupRequestDTO.builder()
                .groupName("Example Group").employeeIds(Arrays.asList(2L, 3L))
                .build();

        Category category = Category.builder().categoryName("그룹").build();
        Group group = Group.builder()
                .groupName("Example Group").deleteYn(false).createdAt(Date.valueOf(LocalDate.now()))
                .build();
        Employee creator = Employee.builder().empNo(1L).build();
        Employee employee2 = Employee.builder().empNo(2L).build();
        Employee employee3 = Employee.builder().empNo(3L).build();
        Group savedGroup = Group.builder().groupId(1L).groupName("Example Group").build();

        when(authentication.getName()).thenReturn("1");
        when(categoryRepository.findByCategoryId(3)).thenReturn(Optional.of(category));
        when(groupRepository.save(any(Group.class))).thenReturn(savedGroup);
        when(employeeRepository.findByEmpNo(1L)).thenReturn(creator);
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(employee2));
        when(employeeRepository.findById(3L)).thenReturn(Optional.of(employee3));
        when(groupRepository.findById(savedGroup.getGroupId())).thenReturn(Optional.of(savedGroup));

        ResponseEntity<GlobalResponse> responseEntity = groupService.createGroup(requestDTO);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("그룹 생성 성공", responseEntity.getBody().getMessage());

        GroupResponseDTO responseData = (GroupResponseDTO) responseEntity.getBody().getData();
        assertEquals("Example Group", responseData.getGroupName());
        assertEquals(Arrays.asList(2L, 3L), responseData.getEmployeeIds());

        verify(groupRepository, times(1)).save(any(Group.class));
        verify(groupEmpRepository, times(3)).save(any(GroupEmp.class));  // 그룹 리더 + 2명의 구성원
    }

    @DisplayName("그룹 생성 서비스 테스트 - 중복된 사원 번호가 포함된 경우 - 실패")
    @Test
    void createGroup_duplicateEmployeeIds() {
        // Given
        GroupRequestDTO requestDTO = GroupRequestDTO.builder()
                .groupName("Example Group")
                .employeeIds(Arrays.asList(2L, 2L))
                .build();

        // Mock behavior
        when(authentication.getName()).thenReturn("1");

        // When
        ResponseEntity<GlobalResponse> responseEntity = groupService.createGroup(requestDTO);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("중복된 사원 번호가 포함되어 있습니다.", responseEntity.getBody().getMessage());

        verify(groupRepository, never()).save(any(Group.class));
        verify(groupEmpRepository, never()).save(any());
    }

    @DisplayName("구성원 추가 서비스 테스트 - 성공")
    @Test
    void addEmployeesToGroup_success() {
        // Given
        Long groupId = 1L;
        List<Long> employeeIds = Arrays.asList(2L, 3L);
        Category category = Category.builder().categoryName("그룹").build();
        Group group = Group.builder().groupId(groupId).build();
        Employee employee1 = Employee.builder().empNo(2L).build();
        Employee employee2 = Employee.builder().empNo(3L).build();

        // Mock behavior
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(employee1));
        when(employeeRepository.findById(3L)).thenReturn(Optional.of(employee2));

        // When
        ResponseEntity<GlobalResponse> responseEntity = groupService.addEmployeesToGroup(groupId, employeeIds);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("구성원 추가 성공", responseEntity.getBody().getMessage());

        verify(groupEmpRepository, times(2)).save(any(GroupEmp.class));
    }

    @DisplayName("구성원 추가 서비스 테스트 - 중복된 사원 번호가 포함된 경우 - 실패")
    @Test
    void addEmployeesToGroup_duplicateEmployeeIds() {
        // Given
        Long groupId = 1L;
        List<Long> employeeIds = Arrays.asList(2L, 2L);
        Category category = Category.builder().categoryName("그룹").build();
        Group group = Group.builder().groupId(groupId).build();

        // Mock behavior
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));

        // When
        ResponseEntity<GlobalResponse> responseEntity = groupService.addEmployeesToGroup(groupId, employeeIds);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("중복된 사원 번호가 포함되어 있습니다.", responseEntity.getBody().getMessage());

        verify(groupEmpRepository, never()).save(any(GroupEmp.class));
    }

    @Test
    @DisplayName("구성원 추가 서비스 테스트 - 그룹이 존재하지 않는 경우 - 실패")
    void addEmployeesToGroup_groupNotFound() {
        // Given
        Long groupId = 1L;
        List<Long> employeeIds = Arrays.asList(2L, 3L);

        // Mock behavior
        when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            groupService.addEmployeesToGroup(groupId, employeeIds);
        });

        assertEquals("유효하지 않은 그룹 ID: " + groupId, exception.getMessage());

        verify(groupEmpRepository, never()).save(any(GroupEmp.class));
    }


    @DisplayName("그룹 이름 변경 테스트 - 성공")
    @Test
    void updateGroupName_success() {
        // Given
        Long groupId = 1L;
        String newGroupName = "Updated Group Name";
        Category category = Category.builder().categoryName("그룹").build();
        Group group = Group.builder().groupId(groupId).groupName("Old Group Name").build();
        Group updatedGroup = Group.builder().groupId(groupId).groupName(newGroupName).build();

        // Mock behavior
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(groupRepository.save(any(Group.class))).thenReturn(updatedGroup);

        // When
        ResponseEntity<GlobalResponse> responseEntity = groupService.updateGroupName(groupId, newGroupName);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("그룹 이름 변경 성공", responseEntity.getBody().getMessage());

        GroupResponseDTO responseData = (GroupResponseDTO) responseEntity.getBody().getData();
        assertEquals(newGroupName, responseData.getGroupName());

        verify(groupRepository, times(1)).save(any(Group.class));
    }

    @Test
    @DisplayName("그룹 이름 변경 테스트 - 그룹 이름이 중복되는 경우 - 실패")
    void updateGroupName_duplicateGroupName() {
        // Given
        Long groupId = 1L;
        String newGroupName = "Updated Group Name";
        Category category = Category.builder().categoryName("그룹").build();
        Group group = Group.builder().groupId(groupId).groupName("Old Group Name").deleteYn(false)
                .build();
        Group groupWithSameName = Group.builder().groupId(2L).groupName(newGroupName).deleteYn(false)
                .build();

        // Mock behavior
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(groupRepository.findByGroupName(newGroupName)).thenReturn(Optional.of(groupWithSameName));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            groupService.updateGroupName(groupId, newGroupName);
        });

        assertEquals("이미 존재하는 그룹 이름입니다.", exception.getMessage());

        verify(groupRepository, never()).save(any(Group.class));
    }

    @DisplayName("그룹 이름 변경 테스트 - 그룹이 존재하지 않는 경우 - 실패")
    @Test
    void updateGroupName_groupNotFound() {
        // Given
        Long groupId = 1L;
        String newGroupName = "Updated Group Name";

        // Mock behavior
        when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            groupService.updateGroupName(groupId, newGroupName);
        });

        assertEquals("유효하지 않은 그룹 ID: " + groupId, exception.getMessage());

        verify(groupRepository, never()).save(any(Group.class));
    }

    @DisplayName("그룹 탈퇴 서비스 테스트 - 성공")
    @Test
    void withdrawGroup_success() {
        // Given
        Long groupId = 1L;
        Long empId = 2L;
        Group group = Group.builder().groupId(groupId).groupName("Example Group").build();

        // Mock behavior
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(authentication.getName()).thenReturn("1");
        when(employeeService.findEmpIdByEmpNo(1L)).thenReturn(empId);
        doNothing().when(groupEmpRepository).updateDeleteYnByGroupIdAndEmployeeId(groupId, empId);
        when(groupEmpRepository.countActiveMembersInGroup(groupId)).thenReturn(1L);

        // When
        ResponseEntity<GlobalResponse> responseEntity = groupService.withdrawGroup(groupId);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("그룹 탈퇴 성공", responseEntity.getBody().getMessage());

        verify(groupEmpRepository, times(1)).updateDeleteYnByGroupIdAndEmployeeId(groupId, empId);
    }

    @Test
    @DisplayName("그룹 탈퇴 서비스 테스트 - 그룹이 존재하지 않는 경우 - 실패")
    void withdrawGroup_groupNotFound() {
        // Given
        Long groupId = 1L;

        // Mock behavior
        when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            groupService.withdrawGroup(groupId);
        });

        assertEquals("유효하지 않은 그룹 ID: " + groupId, exception.getMessage());

        verify(groupEmpRepository, never()).updateDeleteYnByGroupIdAndEmployeeId(anyLong(), anyLong());
    }

    @DisplayName("그룹 탈퇴 서비스 테스트 - 모든 구성원이 그룹을 떠난 경우 - 성공")
    @Test
    void withdrawGroup_andDeleteGroupIfNoMembers() {
        // Given
        Long groupId = 1L;
        Long empId = 2L;
        Group group = Group.builder().groupId(groupId).groupName("Example Group").build();

        // Mock behavior
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(authentication.getName()).thenReturn("1");
        when(employeeService.findEmpIdByEmpNo(1L)).thenReturn(empId);
        doNothing().when(groupEmpRepository).updateDeleteYnByGroupIdAndEmployeeId(groupId, empId);
        when(groupEmpRepository.countActiveMembersInGroup(groupId)).thenReturn(0L);
        when(groupRepository.save(any(Group.class))).thenReturn(group);

        // When
        ResponseEntity<GlobalResponse> responseEntity = groupService.withdrawGroup(groupId);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("모든 구성원이 그룹을 떠났기 때문에 그룹이 삭제되었습니다.", responseEntity.getBody().getMessage());

        GroupResponseDTO responseData = (GroupResponseDTO) responseEntity.getBody().getData();
        assertEquals("Example Group", responseData.getGroupName());

        verify(groupEmpRepository, times(1)).updateDeleteYnByGroupIdAndEmployeeId(groupId, empId);
        verify(groupRepository, times(1)).save(any(Group.class));
    }


    @DisplayName("그룹 삭제 서비스 테스트 - 성공")
    @Test
    void deleteGroup_success() {
        // Given
        Long groupId = 1L;
        Category category = Category.builder().categoryName("그룹").build();
        Group group = Group.builder().groupId(groupId).build();

        // Mock behavior
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        doNothing().when(groupEmpRepository).deleteAllByGroupId(groupId);

        // When
        ResponseEntity<GlobalResponse> responseEntity = groupService.deleteGroup(groupId);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("그룹 삭제 성공", responseEntity.getBody().getMessage());

        verify(groupEmpRepository, times(1)).deleteAllByGroupId(groupId);
        verify(groupRepository, times(1)).save(any(Group.class));
    }

    @Test
    @DisplayName("그룹 삭제 서비스 테스트 - 그룹이 존재하지 않는 경우 - 실패")
    void deleteGroup_groupNotFound() {
        // Given
        Long groupId = 1L;

        // Mock behavior
        when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            groupService.deleteGroup(groupId);
        });

        assertEquals("유효하지 않은 그룹 ID: " + groupId, exception.getMessage());

        verify(groupEmpRepository, never()).deleteAllByGroupId(groupId);
        verify(groupRepository, never()).delete(any(Group.class));
    }

    @Test
    @DisplayName("그룹과 구성원 삭제 - 성공")
    void deleteGroupAndMembersTest(){
        Long groupId = 1L;
        Category category = Category.builder().categoryName("그룹").build();
        Group group = Group.builder().groupId(groupId).build();

        // Mock behavior
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        doNothing().when(groupEmpRepository).deleteAllByGroupId(groupId);

        // When
        groupService.deleteGroupAndMembers(groupId);

        verify(groupEmpRepository, times(1)).deleteAllByGroupId(groupId);
    }

    @Test
    @DisplayName("그룹과 구성원 삭제 - 실패")
    void deleteGroupAndMembersTestFail(){
        Long groupId = -1L;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            groupService.deleteGroupAndMembers(groupId);
        });

        assertEquals("유효하지 않은 그룹 ID: " + groupId, exception.getMessage());
    }

    @DisplayName("직원 이름으로 검색 서비스 테스트 - 성공")
    @Test
    void searchEmployeesByName_success() {
        // Given
        String name = "John";
        Dept dept = Dept.builder().deptName("Example Dept").build();
        Employee employee = Employee.builder()
                .id(1L)
                .empNo(123L)
                .company("Example Company")
                .name("John Doe")
                .email("john@example.com")
                .phoneNum("123-456-7890")
                .extensionNum("123")
                .rank("Manager")
                .password("password")
                .dept(dept)
                .build();

        // Mock behavior
        when(employeeRepository.findByNameContaining(name)).thenReturn(Arrays.asList(employee));

        // When
        List<EmployeeDTO> employees = groupService.searchEmployeesByName(name);

        // Then
        assertEquals(1, employees.size());
        assertEquals("John Doe", employees.get(0).getName());

        verify(employeeRepository, times(1)).findByNameContaining(name);
    }

    @Test
    @DisplayName("직원 이름으로 검색 서비스 테스트 - 검색 결과가 없는 경우 - 실패")
    void searchEmployeesByName_fail() {
        // Given
        String name = "John";

        // Mock behavior
        when(employeeRepository.findByNameContaining(name)).thenReturn(Arrays.asList());

        // When
        List<EmployeeDTO> employees = groupService.searchEmployeesByName(name);

        // Then
        assertEquals(0, employees.size());

        verify(employeeRepository, times(1)).findByNameContaining(name);
    }

    @DisplayName("직원 검색 서비스 테스트 - 그룹id로 직원 조회 - 성공")
    @Test
    void findEmployeeIdsByGroupId_success() {
        // Given
        Long groupId = 1L;
        Employee employee1 = Employee.builder().id(1L).build();
        Employee employee2 = Employee.builder().id(2L).build();
        Group group = Group.builder().groupId(groupId).build();
        GroupEmp groupEmp1 = GroupEmp.builder().group(group).employee(employee1).build();
        GroupEmp groupEmp2 = GroupEmp.builder().group(group).employee(employee2).build();

        // Mock behavior
        when(groupEmpRepository.findAllByGroup_GroupId(groupId)).thenReturn(Arrays.asList(groupEmp1, groupEmp2));

        // When
        List<Long> employeeIds = groupService.findEmployeeIdsByGroupId(groupId);

        // Then
        assertEquals(2, employeeIds.size());
        assertEquals(Arrays.asList(1L, 2L), employeeIds);

        verify(groupEmpRepository, times(1)).findAllByGroup_GroupId(groupId);
    }

    @Test
    @DisplayName("직원 검색 서비스 테스트 - 그룹id로 직원 조회 - 실패")
    void findEmployeeIdsByGroupId_fail() {
        // Given
        Long groupId = 1L;

        // Mock behavior
        when(groupEmpRepository.findAllByGroup_GroupId(groupId)).thenReturn(Arrays.asList());

        // When
        List<Long> employeeIds = groupService.findEmployeeIdsByGroupId(groupId);

        // Then
        assertEquals(0, employeeIds.size());

        verify(groupEmpRepository, times(1)).findAllByGroup_GroupId(groupId);
    }

    @DisplayName("그룹 조회 서비스 테스트 - 모든 그룹 조회 성공")
    @Test
    void findAllGroups_success() {
        // Given
        Category category = Category.builder().categoryName("그룹").build();
        Group group1 = Group.builder().groupId(1L).groupName("Example Group 1").build();
        Group group2 = Group.builder().groupId(2L).groupName("Example Group 2").build();

        // Mock behavior
        when(groupRepository.findAll()).thenReturn(Arrays.asList(group1, group2));

        // When
        List<GroupDTO> groups = groupService.findAllGroups();

        // Then
        assertEquals(2, groups.size());
        assertEquals("Example Group 1", groups.get(0).getGroupName());
        assertEquals("Example Group 2", groups.get(1).getGroupName());

        verify(groupRepository, times(1)).findAll();
    }

    @DisplayName("id로 그룹 조회 서비스 테스트 - 성공")
    @Test
    void findGroupById_success() {
        // Given
        Long groupId = 1L;
        Category category = Category.builder().categoryName("그룹").build();
        Group group = Group.builder().groupId(groupId).groupName("Example Group").build();

        // Mock behavior
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));

        // When
        GroupDTO groupDTO = groupService.findGroupById(groupId);

        // Then
        assertEquals(groupId, groupDTO.getGroupId());
        assertEquals("Example Group", groupDTO.getGroupName());

        verify(groupRepository, times(1)).findById(groupId);
    }

    @Test
    @DisplayName("id로 그룹 조회 서비스 테스트 - 그룹이 존재하지 않는 경우 - 실패")
    void findGroupById_fail() {
        // Given
        Long groupId = 1L;

        // Mock behavior
        when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            groupService.findGroupById(groupId);
        });

        assertEquals("유효하지 않은 그룹 ID: " + groupId, exception.getMessage());

        verify(groupRepository, times(1)).findById(groupId);
    }

    @Test
    @DisplayName("groupId로 그룹 리더 찾기 - 성공")
    void findGroupLeader_success() {
        // Given
        Long groupId = 1L;
        Long empNo = 1L;
        Group group = Group.builder().groupId(groupId).groupName("Example Group").build();
        Employee employee = Employee.builder().empNo(empNo).build();
        GroupEmp groupEmp = GroupEmp.builder().group(group).employee(employee).groupEmpRank("Leader").build();

        // Mock behavior
        when(authentication.getName()).thenReturn("1");
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(employeeRepository.findByEmpNo(empNo)).thenReturn(employee);
        when(groupEmpRepository.findByGroup_GroupIdAndEmployee_Id(groupId, empNo)).thenReturn(groupEmp);

        // When
        Boolean isLeader = groupService.findGroupLeader(groupId);

        // Then
        assertTrue(isLeader);
        verify(groupRepository, times(1)).findById(groupId);
        verify(employeeRepository, times(1)).findByEmpNo(empNo);
        verify(groupEmpRepository, times(1)).findByGroup_GroupIdAndEmployee_Id(groupId, empNo);
    }

    @Test
    @DisplayName("groupId로 그룹 리더 찾기 - 실패: 그룹 리더가 아닌 경우")
    void findGroupLeader_fail_notLeader() {
        // Given
        Long groupId = 1L;
        Long empNo = 1L;
        Group group = Group.builder().groupId(groupId).groupName("Example Group").build();
        Employee employee = Employee.builder().empNo(empNo).build();
        GroupEmp groupEmp = GroupEmp.builder().group(group).employee(employee).groupEmpRank("Member").build();

        // Mock behavior
        when(authentication.getName()).thenReturn("1");
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(employeeRepository.findByEmpNo(empNo)).thenReturn(employee);
        when(groupEmpRepository.findByGroup_GroupIdAndEmployee_Id(groupId, empNo)).thenReturn(groupEmp);

        // When
        Boolean isLeader = groupService.findGroupLeader(groupId);

        // Then
        assertFalse(isLeader);
        verify(groupRepository, times(1)).findById(groupId);
        verify(employeeRepository, times(1)).findByEmpNo(empNo);
        verify(groupEmpRepository, times(1)).findByGroup_GroupIdAndEmployee_Id(groupId, empNo);
    }

    @Test
    @DisplayName("내 그룹 찾기 - 성공")
    void findMyGroup_success() {
        Long empNo = 1L;
        Long empId = 1L;
        Long groupId1 = 1L;
        Long groupId2 = 2L;

        Group group1 = Group.builder().groupId(groupId1).groupName("Group 1").build();
        Group group2 = Group.builder().groupId(groupId2).groupName("Group 2").build();

        GroupEmp groupEmp1 = GroupEmp.builder().group(group1).employee(Employee.builder().id(empId).build()).deleteYn(false).build();
        GroupEmp groupEmp2 = GroupEmp.builder().group(group2).employee(Employee.builder().id(empId).build()).deleteYn(false).build();

        List<GroupEmp> groupEmpList = List.of(groupEmp1, groupEmp2);

        when(authentication.getName()).thenReturn("1");
        when(employeeService.findEmpIdByEmpNo(empNo)).thenReturn(empId);
        when(groupEmpRepository.findAllByEmployee_Id(empId)).thenReturn(groupEmpList);
        when(groupRepository.findById(groupId1)).thenReturn(Optional.of(group1));
        when(groupRepository.findById(groupId2)).thenReturn(Optional.of(group2));

        List<GroupNameDTO> result = groupService.findMyGroup();

        assertEquals(2, result.size());
        assertEquals(groupId1, result.get(0).getGroupId());
        assertEquals("Group 1", result.get(0).getGroupName());
        assertEquals(groupId2, result.get(1).getGroupId());
        assertEquals("Group 2", result.get(1).getGroupName());

        verify(groupEmpRepository, times(1)).findAllByEmployee_Id(empId);
        verify(groupRepository, times(1)).findById(groupId1);
        verify(groupRepository, times(1)).findById(groupId2);
    }

    @Test
    @DisplayName("내 그룹 찾기 - 실패: 그룹이 존재하지 않는 경우")
    void findMyGroup_fail() {
        // Given
        Long empNo = 1L;
        Long empId = 1L;

        // Mock behavior
        when(authentication.getName()).thenReturn("1");
        when(employeeService.findEmpIdByEmpNo(empNo)).thenReturn(empId);
        when(groupEmpRepository.findAllByEmployee_Id(empId)).thenReturn(new ArrayList<>());

        // When
        List<GroupNameDTO> result = groupService.findMyGroup();

        // Then
        assertTrue(result.isEmpty());
        verify(groupEmpRepository, times(1)).findAllByEmployee_Id(empId);
        verify(groupRepository, never()).findById(anyLong());
    }
}
