package grupo12.practico.repositories.NotificationUnsubscription;

import grupo12.practico.models.NotificationUnsubscription;
import grupo12.practico.models.NotificationType;
import jakarta.ejb.Local;

@Local
public interface NotificationUnsubscriptionRepositoryLocal {
    NotificationUnsubscription add(NotificationUnsubscription entity);

    NotificationUnsubscription remove(String userId);

    @Deprecated
    boolean existsByUserId(String userId);

    NotificationUnsubscription findByUserId(String userId);

    NotificationUnsubscription updateSubscription(String userId, NotificationType type, boolean subscribed);
}
