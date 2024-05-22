package com.example.effi.service;

import com.example.effi.domain.DTO.RoutineRequestDTO;
import com.example.effi.domain.DTO.RoutineResponseDTO;
import com.example.effi.domain.Entitiy.Routine;
import com.example.effi.repository.RoutineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class RoutineService {
    private final RoutineRepository routineRepository;

    //루틴 조회 (routineId)
    public RoutineResponseDTO findRoutineById(Long Id){
        Routine routine = routineRepository.findById(Id).orElse(null);
        if(routine == null)
            return null;
        return new RoutineResponseDTO(routine);
    }

    //루틴 추가 -> routine Id 리턴
    public Long addRoutine(RoutineRequestDTO routineRequestDTO){
        return routineRepository.save(routineRequestDTO.toEntity()).getRoutineId();
    }

    //루틴 수정
    public RoutineResponseDTO updateRoutine(RoutineRequestDTO routineRequestDTO){
        Long routineId = routineRequestDTO.getRoutineId();
        Routine routine = routineRepository.findById(routineId).orElse(null);
        if(routine == null)
            return null;
        routine.update(routineRequestDTO.getRoutineStart(), routineRequestDTO.getRoutineEnd(),
                routineRequestDTO.getRoutineCycle());
        return new RoutineResponseDTO(routine);
    }

    //루틴 삭제 (deleteYn = true)
    public Long deleteRoutine(Long Id){
        Routine routine = routineRepository.findById(Id).orElse(null);
        routine.delete();
        return routineRepository.save(routine).getRoutineId();
    }
}
