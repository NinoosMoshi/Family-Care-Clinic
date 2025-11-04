package com.ninos.notification.service;

import com.ninos.notification.dto.NotificationDTO;
import com.ninos.users.entity.User;

public interface NotificationService {

    void sendEmail(NotificationDTO notificationDTO, User user);

}
