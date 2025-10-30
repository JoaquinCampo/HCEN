package grupo12.practico.repositories.NotificationUnsubscription;

import grupo12.practico.models.NotificationUnsubscription;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

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
