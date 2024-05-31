//package com.example.effi.service;
//
//import jakarta.mail.internet.MimeMessage;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestInstance;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.mail.javamail.MimeMessagePreparator;
//
//import static org.mockito.Mockito.*;
//
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//public class EmailServiceTest {
//
//    @Mock
//    private JavaMailSender javaMailSender;
//
//    @InjectMocks
//    private EmailService emailService;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @DisplayName("sendMail 메서드 테스트")
//    @Test
//    public void sendMailTest() {
//        // Given
//        String fromEmail = "test@example.com";
//        String toEmail = "to@example.com";
//        String title = "Test Email";
//        String content = "This is a test email content.";
//
//        MimeMessage mimeMessage = mock(MimeMessage.class);
//        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");
//        // When
//        emailService.sendMail(fromEmail, toEmail, title, content);
//
//        // Then
//        verify(javaMailSender, times(1)).send((MimeMessagePreparator) any());
//
//    }
//}
