package com.example.effi.service;

import com.example.effi.domain.DTO.SearchResponseDTO;
import com.example.effi.domain.Entity.Participant;
import com.example.effi.domain.Entity.Schedule;
import com.example.effi.repository.CategoryRepository;
import com.example.effi.repository.ParticipantRepository;
import com.example.effi.repository.SearchRepository;
import com.example.effi.repository.TagScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SearchServiceTest {

    @Mock
    private SearchRepository searchRepository;

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private TagScheduleRepository tagScheduleRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private SearchService searchService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("1"); // Assuming empId as String "1"
    }

    @Test
    void testSearchSchedulesByTitle() {
        Schedule schedule = mock(Schedule.class);
        Participant participant = mock(Participant.class);
        when(participant.getSchedule()).thenReturn(schedule);
        when(participantRepository.findAllByEmployee_Id(anyLong())).thenReturn(Collections.singletonList(participant));
        when(searchRepository.findAllByTitleContainingIgnoreCase(anyString())).thenReturn(Collections.singletonList(schedule));
        when(tagScheduleRepository.findAllBySchedule_ScheduleId(anyLong())).thenReturn(Collections.emptyList());

        List<SearchResponseDTO> results = searchService.searchSchedulesByTitle("title");

        assertThat(results).isNotEmpty();
    }

    @Test
    void testSearchSchedulesByTitleFailure() {
        when(participantRepository.findAllByEmployee_Id(anyLong())).thenReturn(Collections.emptyList());

        List<SearchResponseDTO> results = searchService.searchSchedulesByTitle("title");

        assertThat(results).isEmpty();
    }

    @Test
    void testSearchSchedulesByTag() {
        Schedule schedule = mock(Schedule.class);
        Participant participant = mock(Participant.class);
        when(participant.getSchedule()).thenReturn(schedule);
        when(participantRepository.findAllByEmployee_Id(anyLong())).thenReturn(Collections.singletonList(participant));
        when(tagScheduleRepository.findAllByTag_TagNameContainingIgnoreCase(anyString())).thenReturn(Collections.singletonList(schedule));
        when(tagScheduleRepository.findAllBySchedule_ScheduleId(anyLong())).thenReturn(Collections.emptyList());

        List<SearchResponseDTO> results = searchService.searchSchedulesByTag("tag");

        assertThat(results).isNotEmpty();
    }

    @Test
    void testSearchSchedulesByTagFailure() {
        when(participantRepository.findAllByEmployee_Id(anyLong())).thenReturn(Collections.emptyList());

        List<SearchResponseDTO> results = searchService.searchSchedulesByTag("tag");

        assertThat(results).isEmpty();
    }

    @Test
    void testSearchSchedulesByCategory() {
        Schedule schedule = mock(Schedule.class);
        Participant participant = mock(Participant.class);
        when(participant.getSchedule()).thenReturn(schedule);
        when(participantRepository.findAllByEmployee_Id(anyLong())).thenReturn(Collections.singletonList(participant));
        when(categoryRepository.findSchedulesByCategoryName(anyString())).thenReturn(Collections.singletonList(schedule));
        when(tagScheduleRepository.findAllBySchedule_ScheduleId(anyLong())).thenReturn(Collections.emptyList());

        List<SearchResponseDTO> results = searchService.searchSchedulesByCategory("category");

        assertThat(results).isNotEmpty();
    }

    @Test
    void testSearchSchedulesByCategoryFailure() {
        when(participantRepository.findAllByEmployee_Id(anyLong())).thenReturn(Collections.emptyList());

        List<SearchResponseDTO> results = searchService.searchSchedulesByCategory("category");

        assertThat(results).isEmpty();
    }
}
