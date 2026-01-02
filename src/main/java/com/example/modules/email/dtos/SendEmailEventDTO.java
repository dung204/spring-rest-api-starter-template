package com.example.modules.email.dtos;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendEmailEventDTO {

  private String to;
  private String subject;
  private String templateName;
  private Map<String, Object> variables;
}
