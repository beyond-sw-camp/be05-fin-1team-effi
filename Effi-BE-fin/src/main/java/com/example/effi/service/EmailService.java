package com.example.effi.service;

import com.example.effi.config.RedisUtil;
import com.example.effi.domain.DTO.ScheduleResponseDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Async
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final RedisUtil redisUtil;
    private static String MessageBody;
    private final GroupService groupService;
    private final ScheduleService scheduleService;

    // 인증 코드 대신 상황에 맞는 문구 추기

    // 그룹 추가 -> 그룹 구성원들에게 (groupId, empId-email)
    @Transactional
    public void addGroupMail(String email, Long groupId){
        MessageBody = groupService.findGroupById(groupId).getGroupName();

        String fromEmail = "teseuteu593@gmail.com";//?
        String toEmail = email;
        String title = "그룹 추가 알림 메일";
        String body = "";
        body += "<h1> 안녕하세요 이피 입니다. </h1>";
        body += "<br>";
        body += "<strong>";
        body += MessageBody +"에 추가 되었습니다."  + "</strong><div><br/> ";
        body += "</div>";
        sendMail(fromEmail, toEmail, title, body);
    }

    // 그룹 편집 -> 그룹 구성원들에게 (groupId, empId-email)
    @Transactional
    public void updateGroupMail(String email, Long groupId){
        MessageBody = groupService.findGroupById(groupId).getGroupName();

        String fromEmail = "teseuteu593@gmail.com";//?
        String toEmail = email;
        String title = "그룹 편집 알림 메일";
        String body = "";
        body += "<h1> 안녕하세요 이피 입니다. </h1>";
        body += "<br>";
        body += "<strong>";
        body += MessageBody +"에서 탈퇴 되었습니다."  + "</strong><div><br/> ";
        body += "</div>";
        sendMail(fromEmail, toEmail, title, body);
    }

    // 회사, 팀, 그룹의 일정이 추가 시 속한 사원들에게 메일 전송 기능 구현
    // 전체 전송 (empId-email)
    @Transactional
    public void allEmployeesMail(String email, Long scheduleId){
        MessageBody = scheduleService.getSchedule(scheduleId).getTitle();

        String fromEmail = "teseuteu593@gmail.com";//?
        String toEmail = email;
        String title = "전체 발송 메일";
        String body = "";
        body += "<h1> 안녕하세요 이피 입니다. </h1>";
        body += "<br>";
        body += "<strong>";
        body += MessageBody  + "</strong><div><br/> ";
        body += "회사 일정이 추가 되었습니다.<br/>";
        body += "</div>";
        sendMail(fromEmail, toEmail, title, body);
    }

    // 부서 전송 (deptId, empId-email)
    @Transactional
    public void deptEmplyeesMail(String email, Long scheduleId){
        MessageBody = scheduleService.getSchedule(scheduleId).getTitle();

        String fromEmail = "teseuteu593@gmail.com";//?
        String toEmail = email;
        String title = "부서 발송 메일";
        String body = "";
        body += "<h1> 안녕하세요 이피 입니다. </h1>";
        body += "<br>";
        body += "<strong>";
        body += MessageBody  + "</strong><div><br/> ";
        body += "부서 일정이 추가 되었습니다.<br/>";
        body += "</div>";
        sendMail(fromEmail, toEmail, title, body);
    }

    // 그룹 전송 (groupId, empId-email)
    @Transactional
    public void groupEmplyesMail(String email, Long scheduleId){
        MessageBody = scheduleService.getSchedule(scheduleId).getTitle();

        String fromEmail = "teseuteu593@gmail.com";//?
        String toEmail = email;
        String title = "그룹 발송 메일";
        String body = "";
        body += "<h1> 안녕하세요 이피 입니다. </h1>";
        body += "<br>";
        body += "<strong>";
        body += MessageBody  + "</strong><div><br/> ";
        body += "그룹 일정이 추가 되었습니다.<br/>";
        body += "</div>";
        sendMail(fromEmail, toEmail, title, body);
    }

    // 일정에 대한 미리 알림 메일 전송 기능 구현
    // 일정 하나 (empId-email, scheduleId, notifitionYn)
    @Transactional
    public void scheduleNotifyMail(String email, Long scheduleId) {
        MessageBody = scheduleService.getSchedule(scheduleId).getTitle();
        LocalDateTime startTime = scheduleService.getSchedule(scheduleId).getStartTime()
                .toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedStartTime = startTime.format(formatter);

        String fromEmail = "teseuteu593@gmail.com";//?
        String toEmail = email;
        String title = "일정 알림 메일";
        String body = "";
        body += "<h1> 안녕하세요 이피 입니다. </h1>";
        body += "<br>";
        body += "<strong>";
        body += MessageBody + "</strong><div><br/> ";
        body += "일정이 시작 1시간 전입니다.<br/>";
        body += "일정 시작 시간: " + formattedStartTime + "<br/>";
        body += "</div>";
        sendMail(fromEmail, toEmail, title, body);
    }

    @Transactional
    public void sendMail(String fromEmail, String toEmail, String title, String body) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(title);
            helper.setText(body, true);
            javaMailSender.send(message);
            System.out.println("메일 전송완료");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        redisUtil.setDataExpire(MessageBody, toEmail, 60 * 5L);
    }

}