package grupo12.practico.repositories.NotificationToken;

import java.util.List;

import grupo12.practico.models.NotificationToken;
import jakarta.ejb.Remote;

@Remote
public interface NotificationTokenRepositoryRemote extends NotificationTokenRepositoryLocal {
    @Override
    NotificationToken add(NotificationToken token);

    @Override
    List<NotificationToken> findByUserId(String userId);

    @Override
    void delete(NotificationToken token);
}
