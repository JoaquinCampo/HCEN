package grupo12.practico.services.NotificationToken;

import java.util.List;

import grupo12.practico.dtos.NotificationToken.NotificationTokenDTO;
import jakarta.ejb.Local;

@Local
public interface NotificationTokenServiceLocal {
    NotificationTokenDTO add(NotificationTokenDTO token);

    List<NotificationTokenDTO> findByUserId(String userId);

    void delete(NotificationTokenDTO token);

    void unsubscribe(String userId);
}
