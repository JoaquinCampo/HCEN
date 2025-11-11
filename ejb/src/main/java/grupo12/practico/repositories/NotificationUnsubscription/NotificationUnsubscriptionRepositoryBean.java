package grupo12.practico.repositories.NotificationUnsubscription;

import grupo12.practico.models.NotificationUnsubscription;
import grupo12.practico.models.NotificationType;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;

@Stateless
@Local(NotificationUnsubscriptionRepositoryLocal.class)
@Remote(NotificationUnsubscriptionRepositoryRemote.class)
public class NotificationUnsubscriptionRepositoryBean implements NotificationUnsubscriptionRepositoryRemote {

    @PersistenceContext(unitName = "practicoPersistenceUnit")
    private EntityManager em;

    @Override
    public NotificationUnsubscription add(NotificationUnsubscription entity) {
        em.persist(entity);
        return entity;
    }

    @Override
    public NotificationUnsubscription remove(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return null;
        }
        TypedQuery<NotificationUnsubscription> query = em.createQuery(
                "SELECT u FROM NotificationUnsubscription u WHERE u.user.id = :userId",
                NotificationUnsubscription.class);
        query.setParameter("userId", userId.trim());
        List<NotificationUnsubscription> results = query.getResultList();
        if (!results.isEmpty()) {
            NotificationUnsubscription entity = results.get(0);
            em.remove(entity);
            return entity;
        }
        return null;
    }

    @Override
    public boolean existsByUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return false;
        }
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(u) FROM NotificationUnsubscription u WHERE u.user.id = :userId",
                Long.class);
        query.setParameter("userId", userId.trim());
        Long result = query.getSingleResult();
        return result != null && result > 0;
    }

    @Override
    public NotificationUnsubscription findByUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return null;
        }
        TypedQuery<NotificationUnsubscription> query = em.createQuery(
                "SELECT u FROM NotificationUnsubscription u WHERE u.user.id = :userId",
                NotificationUnsubscription.class);
        query.setParameter("userId", userId.trim());
        List<NotificationUnsubscription> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public NotificationUnsubscription updateSubscription(String userId, NotificationType type, boolean subscribed) {
        if (userId == null || userId.trim().isEmpty() || type == null) {
            return null;
        }

        NotificationUnsubscription entity = findByUserId(userId);
        if (entity == null) {
            return null;
        }

        switch (type) {
            case ACCESS_REQUEST:
                entity.setSubscribedToAccessRequest(subscribed);
                break;
            case CLINICAL_HISTORY_ACCESS:
                entity.setSubscribedToClinicalHistoryAccess(subscribed);
                break;
        }

        return em.merge(entity);
    }
}
