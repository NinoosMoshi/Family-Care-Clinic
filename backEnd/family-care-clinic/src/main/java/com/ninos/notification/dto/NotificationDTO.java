package com.ninos.notification.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ninos.enums.NotificationType;
import com.ninos.users.dto.UserDTO;
import com.ninos.users.entity.User;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationDTO {

    private Long id;
    private String subject;

    @NotBlank(message = "Recipient is required")
    private String recipient;

    private String message;
    private NotificationType type;

    private LocalDateTime createdAt;

    private String templateName;
    private Map<String, Object> templateVariables;


}
