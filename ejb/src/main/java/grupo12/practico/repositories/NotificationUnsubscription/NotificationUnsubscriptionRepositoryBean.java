package grupo12.practico.repositories.NotificationUnsubscription;

import grupo12.practico.models.NotificationUnsubscription;
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
}
