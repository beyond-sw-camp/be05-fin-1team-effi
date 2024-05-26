// package com.example.effi.repository;


// import com.example.effi.domain.Entitiy.TimezoneEmp;

// import java.util.List;
// import java.util.Optional;

// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;
// import org.springframework.stereotype.Repository;


// @Repository
// public interface TimezoneEmpRepository extends JpaRepository<TimezoneEmp, Long> {
//     @Query("SELECT te FROM TimezoneEmp te WHERE te.employee.id = :empId AND te.defaultTimezone = true")
//     TimezoneEmp findByEmpIdAndDefaultTimezone(@Param("empId") Long empId);

//      /**
//      * 특정 직원의 타임존 목록을 반환합니다.
//      * @param empId 직원 ID
//      * @return 타임존 목록
//      */
//     List<TimezoneEmp> findByEmployeeId(Long empId);

//     /**
//      * 특정 직원과 타임존 ID로 타임존을 반환합니다.
//      * @param empId 직원 ID
//      * @param timezoneId 타임존 ID
//      * @return 타임존
//      */
//     Optional<TimezoneEmp> findByEmployeeIdAndTimezoneTimezoneId(Long empId, Long timezoneId);

//     /**
//      * 특정 직원의 기본 타임존을 반환합니다.
//      * @param empId 직원 ID
//      * @param defaultTimezone 기본 타임존 여부
//      * @return 기본 타임존
//      */
//     Optional<TimezoneEmp> findByEmployeeIdAndDefaultTimezone(Long empId, Boolean defaultTimezone);
// }

package com.example.effi.repository;

import com.example.effi.domain.Entitiy.TimezoneEmp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TimezoneEmpRepository extends JpaRepository<TimezoneEmp, Long> {
    @Query("SELECT te FROM TimezoneEmp te WHERE te.employee.id = :empId AND te.defaultTimezone = true")
    TimezoneEmp findByEmpIdAndDefaultTimezone(@Param("empId") Long empId);

    Optional<TimezoneEmp> findByEmployeeIdAndTimezone_TimezoneId(Long empId, Long timezoneId); // 변경된 메서드 이름
    Optional<TimezoneEmp> findByEmployeeIdAndDefaultTimezone(Long empId, boolean isDefault);
    Optional<TimezoneEmp> findByEmployeeIdAndTimezoneTimezoneId(Long empId, Long timezoneId);
    List<TimezoneEmp> findByEmployeeId(Long empId);
}
