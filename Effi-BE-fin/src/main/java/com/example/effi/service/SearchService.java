package com.example.effi.service;

import com.example.effi.domain.DTO.ScheduleResponseDTO;
import com.example.effi.domain.DTO.SearchResponseDTO;
import com.example.effi.domain.Entitiy.Participant;
import com.example.effi.domain.Entitiy.Schedule;
import com.example.effi.repository.ParticipantRepository;
import com.example.effi.repository.SearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {

    private SearchRepository searchRepository;
    private ParticipantRepository participantRepository;

    // title 검색으로 내 일정 검색하기
    // containing ignore case -> 대소문자 구분 x / %title$
    @Transactional
    public List<SearchResponseDTO> searchSchedulesByTitle(String title) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String empId = authentication.getName();

        List<Schedule> allSchedules = searchRepository.findAllByTitleContainingIgnoreCase(title);
        List<Participant> participants = participantRepository.findAllByEmployee_Id(Long.valueOf(empId));
        List<SearchResponseDTO> schedules = new ArrayList<>();

        for (Participant participant : participants) {
            Schedule schedule = participant.getSchedule();
            if (!schedule.getDeleteYn() && allSchedules.contains(schedule)) {
                schedules.add(new SearchResponseDTO(schedule));
            }
        }
        return schedules;
    }

    // tag 검색으로 내 일정 검색하기
//    public List<SearchResponseDTO> searchSchedulesByTag(String tag){
//
//    }

    // category 검색으로 내 일정 검색하기
//    public List<SearchResponseDTO> searchSchedulesByCategory(String category){
//
//    }





}
