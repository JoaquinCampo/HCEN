package grupo12.practico.repositories.NotificationToken;

import java.util.List;

import grupo12.practico.models.NotificationToken;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.time.LocalDateTime;

@Stateless
@Local(NotificationTokenRepositoryLocal.class)
@Remote(NotificationTokenRepositoryRemote.class)
public class NotificationTokenRepositoryBean implements NotificationTokenRepositoryRemote {

    @PersistenceContext(unitName = "practicoPersistenceUnit")
    private EntityManager em;

    @Override
    public NotificationToken add(NotificationToken token) {
        em.persist(token);
        return token;
    }

    @Override
    public List<NotificationToken> findByUserId(String userId) {
        TypedQuery<NotificationToken> q = em.createQuery(
                "SELECT t FROM NotificationToken t WHERE t.user.id = :userId",
                NotificationToken.class);
        q.setParameter("userId", userId);
        return q.getResultList();
    }

    @Override
    public NotificationToken findByToken(String token) {
        TypedQuery<NotificationToken> q = em.createQuery(
                "SELECT t FROM NotificationToken t WHERE t.token = :token",
                NotificationToken.class);
        q.setParameter("token", token);
        return q.getResultList().stream().findFirst().orElse(null);
    }

    @Override
    public void delete(NotificationToken token) {
        NotificationToken managed = em.merge(token);
        em.remove(managed);
    }

    @Override
    public NotificationToken updateLastUsedAt(String tokenId) {
        NotificationToken token = findByToken(tokenId);
        token.setLastUsedAt(LocalDateTime.now());
        return em.merge(token);

    }
}
