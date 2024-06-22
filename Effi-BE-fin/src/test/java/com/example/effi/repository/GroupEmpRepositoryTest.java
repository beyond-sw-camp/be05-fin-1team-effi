package com.example.effi.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.example.effi.domain.Entity.Category;
import com.example.effi.domain.Entity.Employee;
import com.example.effi.domain.Entity.Group;
import com.example.effi.domain.Entity.GroupEmp;

import jakarta.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class GroupEmpRepositoryTest {

    @Autowired
    private GroupEmpRepository groupEmpRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EntityManager entityManager;

    private Group testGroup;
    private Employee testEmployee;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        testGroup = Group.builder()
                .groupName("Test Group")
                .deleteYn(false)
                .createdAt(java.sql.Date.valueOf(java.time.LocalDate.now()))
                .build();
        entityManager.persist(testGroup);

        testCategory = Category.builder()
                .categoryId(1L)
                .categoryName("Test Category")
                .group(testGroup)
                .build();
        entityManager.persist(testCategory);

        testEmployee = Employee.builder()
                .empNo(1L)
                .name("John Doe")
                .company("Example Corp")
                .email("john.doe@example.com")
                .rank("Manager")
                .phoneNum("123-456-7890")
                .extensionNum("1234")
                .password("password")
                .build();
        entityManager.persist(testEmployee);

        GroupEmp groupEmp = GroupEmp.builder()
                .group(testGroup)
                .employee(testEmployee)
                .groupEmpRank("Member")
                .deleteYn(false)
                .build();
        entityManager.persist(groupEmp);

        entityManager.flush();
    }

    @Test
    @DisplayName("그룹 ID와 직원 ID로 그룹 구성원 찾기 - 성공 ")
    @Rollback(false)
    @Transactional
    public void testFindByGroup_GroupIdAndEmployee_Id() {
        GroupEmp found = groupEmpRepository.findByGroup_GroupIdAndEmployee_Id(testGroup.getGroupId(),
                testEmployee.getId());
        assertThat(found).isNotNull();
    }

    @Test
    @DisplayName("그룹 ID와 직원 ID로 그룹 구성원 찾기 - 실패 ")
    @Rollback(false)
    @Transactional
    public void testFindByGroup_GroupIdAndEmployee_Id_Fail() {
        GroupEmp found = groupEmpRepository.findByGroup_GroupIdAndEmployee_Id(testGroup.getGroupId(), 0L);
        assertThat(found).isNull();
    }

    @Test
    @DisplayName("그룹 ID와 직원 ID로 deleteYn 업데이트하기 - 성공")
    @Rollback(false)
    @Transactional
    public void testUpdateDeleteYnByGroupIdAndEmployeeId() {
        groupEmpRepository.updateDeleteYnByGroupIdAndEmployeeId(testGroup.getGroupId(), testEmployee.getId());
        entityManager.flush(); // 상태를 데이터베이스에 반영
        entityManager.clear(); // 엔티티 매니저를 초기화하여 최신 상태를 반영

        GroupEmp updated = groupEmpRepository.findByGroup_GroupIdAndEmployee_Id(testGroup.getGroupId(),
                testEmployee.getId());
        assertThat(updated.getDeleteYn()).isTrue();
    }

    @Test
    @DisplayName("그룹 ID와 직원 ID로 deleteYn 업데이트하기 - 실패")
    @Rollback(false)
    @Transactional
    public void testUpdateDeleteYnByGroupIdAndEmployeeId_Fail() {
        groupEmpRepository.updateDeleteYnByGroupIdAndEmployeeId(testGroup.getGroupId(), 0L);
        entityManager.flush(); // 상태를 데이터베이스에 반영
        entityManager.clear(); // 엔티티 매니저를 초기화하여 최신 상태를 반영

        GroupEmp updated = groupEmpRepository.findByGroup_GroupIdAndEmployee_Id(testGroup.getGroupId(), 0L);
        assertThat(updated).isNull();
    }

    @Test
    @DisplayName("활성 구성원 수 확인 - 성공")
    @Rollback(false)
    @Transactional
    public void testCountActiveMembersInGroup() {
        Long count = groupEmpRepository.countActiveMembersInGroup(testGroup.getGroupId());
        assertThat(count).isEqualTo(1L);
    }

    @Test
    @DisplayName("활성 구성원 수 확인 - 실패")
    @Rollback(false)
    @Transactional
    public void testCountActiveMembersInGroup_Fail() {
        Long count = groupEmpRepository.countActiveMembersInGroup(0L);
        assertThat(count).isEqualTo(0L);
    }

    @Test
    @DisplayName("그룹 ID로 모든 구성원 삭제 - 성공")
    @Rollback(false)
    @Transactional
    public void testDeleteAllByGroupId() {
        groupEmpRepository.deleteAllByGroupId(testGroup.getGroupId());
        entityManager.flush(); // 상태를 데이터베이스에 반영
        entityManager.clear(); // 엔티티 매니저를 초기화하여 최신 상태를 반영

        assertThat(groupEmpRepository.findAllByGroup_GroupId(testGroup.getGroupId())).isEmpty();
    }

    @Test
    @DisplayName("그룹 ID로 모든 구성원 삭제 - 실패")
    @Rollback(false)
    @Transactional
    public void testDeleteAllByGroupId_Fail() {
        groupEmpRepository.deleteAllByGroupId(0L);
        entityManager.flush(); // 상태를 데이터베이스에 반영
        entityManager.clear(); // 엔티티 매니저를 초기화하여 최신 상태를 반영

        assertThat(groupEmpRepository.findAllByGroup_GroupId(0L)).isEmpty();
    }
}
