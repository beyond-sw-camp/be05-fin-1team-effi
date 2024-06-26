package com.example.effi.service;

import com.example.effi.domain.DTO.RoutineRequestDTO;
import com.example.effi.domain.DTO.RoutineResponseDTO;
import com.example.effi.domain.DTO.ScheduleResponseDTO;
import com.example.effi.domain.Entity.Routine;
import com.example.effi.domain.Entity.Schedule;
import com.example.effi.repository.RoutineRepository;
import com.example.effi.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class RoutineService {
    private final RoutineRepository routineRepository;
    private final ScheduleRepository scheduleRepository;

    //루틴 조회 (routineId)
    public RoutineResponseDTO findRoutineById(Long Id){
        Routine routine = routineRepository.findById(Id).orElse(null);
        if (routine == null) {
            throw new IllegalArgumentException("루틴 ID가 유효하지 않습니다.");
        }
        return new RoutineResponseDTO(routine);
    }

    // schduleId 찾기
    public List<ScheduleResponseDTO> findAllByRoutineId(Long routineId){
        List<Schedule> lst = scheduleRepository.findAllByRoutine_RoutineId(routineId);
        if (lst.size() == 0)
            throw new IllegalArgumentException("루틴 ID가 유효하지 않습니다.");
        List<ScheduleResponseDTO> dtos = new ArrayList<>();
        for(Schedule schedule : lst){
            dtos.add(new ScheduleResponseDTO(schedule));
        }
        return dtos;
    }

    //루틴 추가 -> routine Id 리턴
    public Long addRoutine(RoutineRequestDTO routineRequestDTO){
        if (routineRequestDTO.getRoutineCycle() == null || routineRequestDTO.getRoutineCycle().isEmpty())
            throw new IllegalArgumentException("루틴 정보가 유효하지 않습니다.");
        return routineRepository.save(routineRequestDTO.toEntity()).getRoutineId();
    }

    //루틴 수정
    public RoutineResponseDTO updateRoutine(Long routineId, RoutineRequestDTO routineRequestDTO){
        Routine routine = routineRepository.findById(routineId).orElse(null);
        if (routine == null) {
            throw new IllegalArgumentException("루틴 ID가 유효하지 않습니다.");
        }
        routine.update(routineRequestDTO.getRoutineStart(), routineRequestDTO.getRoutineEnd(),
                routineRequestDTO.getRoutineCycle());
        return new RoutineResponseDTO(routine);
    }

    //루틴 삭제 (deleteYn = true)
    public Long deleteRoutine(Long Id){
        Routine routine = routineRepository.findById(Id).orElse(null);
        if (routine == null) {
            throw new IllegalArgumentException("루틴 ID가 유효하지 않습니다.");
        }
        routine.delete();
        return routineRepository.save(routine).getRoutineId();
    }
}
