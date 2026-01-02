package com.example.modules.email.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

  private final JavaMailSender mailSender;
  private final SpringTemplateEngine templateEngine;

  @Value("${spring.mail.username}")
  private String emailFrom;

  public void sendHtmlEmail(
    String to,
    String subject,
    String templateName,
    Map<String, Object> variables
  ) {
    try {
      Context context = new Context();
      context.setVariables(variables);

      String htmlBody = templateEngine.process(templateName, context);

      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(
        message,
        true,
        StandardCharsets.UTF_8.name()
      );

      helper.setFrom(emailFrom);
      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(htmlBody, true);

      mailSender.send(message);
      log.info("Email sent successfully to: {}", to);
    } catch (MessagingException e) {
      log.error("Failed to send email", e);
      throw new RuntimeException("Email sending failed");
    }
  }
}
