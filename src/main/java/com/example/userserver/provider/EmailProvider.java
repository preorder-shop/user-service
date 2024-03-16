package com.example.userserver.provider;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailProvider {

    private final JavaMailSender javaMailSender;

    private final String SUBJECT = "예약구매 웹사이트 이메일 인증번호";

    public boolean sendCertificationMail(String email, String certificationNumber) {

        try {

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);

            String htmlContent = getCertificationMessage(certificationNumber);

            messageHelper.setTo(email);
            messageHelper.setSubject(SUBJECT);
            messageHelper.setText(htmlContent,true);

            javaMailSender.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }

    private String getCertificationMessage( String certificationNumber){
        String message = "";
        message +="<h2 style='text-align:center;'>아래 인증코드를 사이트에 입력해주세요. (유효시간 10분) </h2>";
        message+="<h3 style='text-align:center;'>인증코드 : <strong style='font-size:32px; letter-spacing:8px;'>"+certificationNumber+"</strong></h3>";
        return message;
    }
}
