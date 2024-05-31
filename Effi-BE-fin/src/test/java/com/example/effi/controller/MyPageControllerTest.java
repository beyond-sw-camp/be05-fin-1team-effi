package com.example.effi.controller;

import com.example.effi.config.TokenProvider;
import com.example.effi.domain.Dto.Mypage.MyPageResponseDTO;
import com.example.effi.domain.Dto.Mypage.MyPageUpdateDTO;
import com.example.effi.repository.GroupEmpRepository;
import com.example.effi.service.EmailService;
import com.example.effi.service.GroupService;
import com.example.effi.service.MyPageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
class MyPageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MyPageService myPageService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void mypageView_ShouldReturnMyPageResponseDTO() throws Exception {
        // 테스트용 MyPageResponseDTO 객체 생성
        MyPageResponseDTO responseDTO = MyPageResponseDTO.builder()
                .empNo(1L)
                .name("John Doe")
                .company("Example Corp")
                .email("john.doe@example.com")
                .rank("Manager")
                .phoneNum("123-456-7890")
                .extensionNum("1234")
                .deptName("Sales")
                .timezoneName("UTC")
                .msg("회원 정보 조회")
                .build();

        // myPageService.getEmployee() 메서드가 responseDTO를 반환하도록 모킹
        when(myPageService.getEmployee()).thenReturn(responseDTO);

        // /api/mypage/me 엔드포인트에 GET 요청을 보내고, 응답을 검증
        mockMvc.perform(get("/api/mypage/me")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"empNo\":1,\"name\":\"John Doe\",\"company\":\"Example Corp\",\"email\":\"john.doe@example.com\",\"rank\":\"Manager\",\"phoneNum\":\"123-456-7890\",\"extensionNum\":\"1234\",\"deptName\":\"Sales\",\"timezoneName\":\"UTC\",\"msg\":\"회원 정보 조회\"}"))
                .andDo(print());
    }

    @Test
    void updateTimezone_ShouldReturnSuccessMessage() throws Exception {
        // 테스트용 MyPageUpdateDTO 객체 생성
        MyPageUpdateDTO updateDTO = MyPageUpdateDTO.builder()
                .empId(1L)
                .timezoneId(2L)
                .build();

        // /api/mypage/update 엔드포인트에 PUT 요청을 보내고, 응답을 검증
        mockMvc.perform(put("/api/mypage/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"empId\":1,\"timezoneId\":2}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Timezone updated successfully"))
                .andDo(print());
    }



}

