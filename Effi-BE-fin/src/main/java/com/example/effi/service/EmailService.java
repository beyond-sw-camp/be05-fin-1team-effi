package com.example.effi.service;

import com.example.effi.config.RedisUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final RedisUtil redisUtil;
    private static String MessageBody;

    // 인증 코드 대신 상황에 맞는 문구 추기

    // 그룹 추가 -> 그룹 구성원들에게 (groupId, empId-email)
    public static void addGroup(){
        StringBuilder key = new StringBuilder();

        key.append("그룹에 추가 되었습니다.");

        MessageBody = key.toString();
    }

    // 그룹 편집 -> 그룹 구성원들에게 (groupId, empId-email)
    public static void updateGroup(){
        StringBuilder key = new StringBuilder();

        key.append("그룹이 편집 되었습니다.");

        MessageBody = key.toString();
    }

    // 회사, 팀, 그룹의 일정이 추가 시 속한 사원들에게 메일 전송 기능 구현
    // 전체 전송 (empId-email)
    public static void allEmployees(){
        StringBuilder key = new StringBuilder();

        key.append("전체 발송 메일입니다.");

        MessageBody = key.toString();
    }

    // 부서 전송 (deptId, empId-email)
    public static void deptEmplyes(){
        StringBuilder key = new StringBuilder();

        key.append("부서 발송 메일입니다.");

        MessageBody = key.toString();
    }
    // 그룹 전송 (groupId, empId-email)
    public static void groupEmplyes(){
        StringBuilder key = new StringBuilder();

        key.append("그룹 발송 메일입니다.");

        MessageBody = key.toString();
    }

    // 일정에 대한 미리 알림 메일 전송 기능 구현
    // 일정 하나 (empId-email, scheduleId, notifitionYn)
    public static void scheduleNotify(){
        StringBuilder key = new StringBuilder();

        key.append("일정 알림 메일입니다.");

        MessageBody = key.toString();
    }


    /**
     * 인증코드 생성 및 이메일 전송
     * @author Jihwan
     * @param email 인증메일을 보낼 이메일 주소
     * @return 8자리 인증코드
     */
    public void setEmail(String email) {
//        createAuthCode(); -> 해당하는 함수로 변경
        scheduleNotify();
        String fromEmail = "teseuteu593@gmail.com";//?
        String toEmail = email;
        String title = MessageBody;
        String body = "";
        body += "<div style='margin:100px;'>";
        body += "<h1> 안녕하세요 이피 입니다. </h1>";
        body += "<br>";
        body += "<strong>";
        body += MessageBody + "</strong><div><br/> ";
        body += "</div>";
        sendMail(fromEmail, toEmail, title, body);
    }

    /**
     * 설정해둔 세부사항 정보를 가지고 인증메일을 전송
     * @author Jihwan
     * @param fromEmail 보내는이 이메일 주소
     * @param toEmail 받는이 이메일 주소
     * @param title 메일 제목
     * @param content 메일 내용
     */
    @Transactional
    public void sendMail(String fromEmail, String toEmail, String title, String content) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(title);
            helper.setText(content, true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        redisUtil.setDataExpire(MessageBody, toEmail, 60 * 5L);
    }

    /**
     * 사용자가 입력한 인증코드가 전송된 인증코드와 일치하는지 확인
     * @author Jihwan
     * @param email 사용자가 인증번호를 받은 이메일
     * @param authNumber 사용자가 해당 이메일로 받은 인증코드
     * @return 사용자가 입력한 인증코드와 전송됐었던 인증코드가 일치하는지 여부
     */
    @Transactional(readOnly = true)
    public boolean checkAuthNumber(String email, String authNumber) {
        if (redisUtil.getData(authNumber) == null) {
            return false;
        } else
            return redisUtil.getData(authNumber).equals(email);
    }

}