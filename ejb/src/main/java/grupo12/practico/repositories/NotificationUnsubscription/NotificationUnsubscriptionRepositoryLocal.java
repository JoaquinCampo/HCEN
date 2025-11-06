package grupo12.practico.repositories.NotificationUnsubscription;

import grupo12.practico.models.NotificationUnsubscription;
import jakarta.ejb.Local;

@Local
public interface NotificationUnsubscriptionRepositoryLocal {
    NotificationUnsubscription add(NotificationUnsubscription entity);

    NotificationUnsubscription remove(String userId);

    boolean existsByUserId(String userId);
}
