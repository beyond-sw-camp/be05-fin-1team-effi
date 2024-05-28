package com.example.effi.service;

import com.example.effi.domain.DTO.SearchResponseDTO;
import com.example.effi.domain.Entitiy.Participant;
import com.example.effi.domain.Entitiy.Schedule;
import com.example.effi.domain.Entitiy.TagSchedule;
import com.example.effi.repository.CategoryRepository;
import com.example.effi.repository.ParticipantRepository;
import com.example.effi.repository.SearchRepository;
import com.example.effi.repository.TagScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SearchService {

    private final SearchRepository searchRepository;
    private final ParticipantRepository participantRepository;
    private final TagScheduleRepository tagScheduleRepository;
    private final CategoryRepository categoryRepository;

    // title 검색으로 내 일정 검색하기
    // containing ignore case -> 대소문자 구분 x / %title$
    public List<SearchResponseDTO> searchSchedulesByTitle(String title) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String empId = authentication.getName();

        // 제목 기준으로 일정 가져오기
        List<Schedule> allSchedules = searchRepository.findAllByTitleContainingIgnoreCase(title);
        // 로그인한 사원이 참여자인 일정 가져오기
        List<Participant> participants = participantRepository.findAllByEmployee_Id(Long.valueOf(empId));
        List<SearchResponseDTO> schedules = new ArrayList<>();

        for (Participant participant : participants) {
            Schedule schedule = participant.getSchedule();
            // 삭제되지 않은 일정
            if (!schedule.getDeleteYn() && allSchedules.contains(schedule)) {
                schedules.add(new SearchResponseDTO(schedule));
            }
        }
        return schedules;
    }

    // tag 검색으로 내 일정 검색하기
    // like -> 정확하기 검색 필요
    public List<SearchResponseDTO> searchSchedulesByTag(String tagName){
        // 현재 로그인된 사용자의 empId를 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long empId = Long.parseLong(authentication.getName());

        // 태그 이름으로 모든 일정 검색
        List<Schedule> taggedSchedules = tagScheduleRepository.findSchedulesByTagName(tagName);
        // 로그인한 사원이 참여자인 일정 가져오기
        List<Participant> participants = participantRepository.findAllByEmployee_Id(empId);
        List<SearchResponseDTO> schedules = new ArrayList<>();

        for (Participant participant : participants) {
            Schedule schedule = participant.getSchedule();
            // 삭제되지 않은 일정
            if (!schedule.getDeleteYn() && taggedSchedules.contains(schedule)) {
                schedules.add(new SearchResponseDTO(schedule));
            }
        }
        return schedules;
    }

    // category 검색으로 내 일정 검색하기
    // like -> 정확하기 검색 필요
    public List<SearchResponseDTO> searchSchedulesByCategory(String categoryName){
        // 현재 로그인된 사용자의 empId를 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long empId = Long.parseLong(authentication.getName());

        // 카테고리 이름으로 모든 일정 검색
        List<Schedule> categorySchedules = categoryRepository.findSchedulesByCategoryName(categoryName);
        // 내가 참여한 일정은 참여자로 들어가있다고 가정
        // 로그인한 사원이 참여자인 일정 가져오기
        List<Participant> participants = participantRepository.findAllByEmployee_Id(empId);
        List<SearchResponseDTO> schedules = new ArrayList<>();

        for (Participant participant : participants) {
            Schedule schedule = participant.getSchedule();
            // 삭제되지 않은 일정
            if (!schedule.getDeleteYn() && categorySchedules.contains(schedule)) {
                schedules.add(new SearchResponseDTO(schedule));
            }
        }
        return schedules;
    }

}
