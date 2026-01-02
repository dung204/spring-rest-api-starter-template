package com.example.modules.email.listeners;

import com.example.modules.email.dtos.SendEmailEventDTO;
import com.example.modules.email.services.EmailService;
import com.example.modules.redis.listeners.RedisStreamListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailStreamListener extends RedisStreamListener<SendEmailEventDTO> {

  private final EmailService emailService;

  public EmailStreamListener(
    StringRedisTemplate redisTemplate,
    ObjectMapper objectMapper,
    EmailService emailService
  ) {
    super(redisTemplate, objectMapper);
    this.emailService = emailService;
  }

  @Override
  public String getStreamKey() {
    return "stream:email_sending";
  }

  @Override
  public String getConsumerGroup() {
    return "group:email_workers";
  }

  @Override
  public Class<SendEmailEventDTO> getTargetType() {
    return SendEmailEventDTO.class;
  }

  @Override
  protected void process(String messageId, SendEmailEventDTO dto) {
    log.info("Processing email event for: {}", dto.getTo());

    emailService.sendHtmlEmail(
      dto.getTo(),
      dto.getSubject(),
      dto.getTemplateName(),
      dto.getVariables()
    );
  }
}
