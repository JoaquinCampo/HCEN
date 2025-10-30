package grupo12.practico.repositories.NotificationToken;

import java.util.List;

import grupo12.practico.models.NotificationToken;
import jakarta.ejb.Local;

@Local
public interface NotificationTokenRepositoryLocal {
    NotificationToken add(NotificationToken token);

    List<NotificationToken> findByUserId(String userId);

    NotificationToken findByToken(String token);

    NotificationToken updateLastUsedAt(String token);

    void delete(NotificationToken token);
}
