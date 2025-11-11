package grupo12.practico.services.NotificationToken;

import java.util.List;

import grupo12.practico.dtos.NotificationToken.NotificationSubscriptionDTO;
import grupo12.practico.dtos.NotificationToken.NotificationTokenDTO;
import grupo12.practico.models.NotificationType;
import jakarta.ejb.Local;

@Local
public interface NotificationTokenServiceLocal {
    NotificationTokenDTO add(NotificationTokenDTO token);

    List<NotificationTokenDTO> findByUserCi(String userCi);

    void delete(NotificationTokenDTO token);

    void unsubscribe(String userCi, NotificationType notificationType);

    void subscribe(String userCi, NotificationType notificationType);

    boolean isUserSubscribedToNotificationType(String userCi, NotificationType notificationType);

    NotificationSubscriptionDTO getSubscriptionPreferences(String userCi);
}
