package com.example.effi.repository;

import com.example.effi.domain.DTO.MyPageTimezoneDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.effi.domain.Entity.Employee;

import java.util.List;

public interface MyPageRepository extends JpaRepository<Employee, Long> {

    @Query("select t.timezoneName " +
            "from Timezone t " +
            "left join TimezoneEmp te on t.timezoneId = te.timezone.timezoneId " +
            "left join Employee e on e.id = te.employee.id " +
            "where e.empNo = :empNo " +
            "and te.defaultTimezone = true")
    String findDefaultTimezoneName(@Param("empNo") Long empNo);


    @Query("select new com.example.effi.domain.DTO.MyPageTimezoneDTO(t.timezoneId, t.timezoneName) " +
            "from Timezone t " +
            "group by t.timezoneName")
    List<MyPageTimezoneDTO> findAllTimezonesGroupedByName();


}
