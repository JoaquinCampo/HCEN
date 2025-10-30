package grupo12.practico.services.NotificationToken;

import java.util.List;
import java.util.stream.Collectors;

import grupo12.practico.dtos.NotificationToken.NotificationTokenDTO;
import grupo12.practico.models.NotificationToken;
import grupo12.practico.models.NotificationUnsubscription;
import grupo12.practico.models.User;
import grupo12.practico.repositories.NotificationToken.NotificationTokenRepositoryLocal;
import grupo12.practico.repositories.NotificationUnsubscription.NotificationUnsubscriptionRepositoryLocal;
import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.validation.ValidationException;

import java.time.LocalDateTime;

@Stateless
@Local(NotificationTokenServiceLocal.class)
@Remote(NotificationTokenServiceRemote.class)
public class NotificationTokenServiceBean implements NotificationTokenServiceRemote {

    @EJB
    private NotificationTokenRepositoryLocal notificationTokenRepository;

    @EJB
    private NotificationUnsubscriptionRepositoryLocal notificationUnsubscriptionRepository;

    @Override
    public NotificationTokenDTO add(NotificationTokenDTO dto) {
        validate(dto);
        if (notificationUnsubscriptionRepository.existsByUserId(dto.getUserId())) {
            throw new ValidationException("User has unsubscribed from notifications");
        }
        NotificationToken entity = new NotificationToken();
        NotificationTokenDTO out = new NotificationTokenDTO();
        NotificationToken saved = null;
        User user = new UserProxy(dto.getUserId());
        entity.setUser(user);
        entity.setToken(dto.getToken());
        if (notificationTokenRepository.findByToken(dto.getToken()) != null) {
            saved = notificationTokenRepository.updateLastUsedAt(dto.getToken());
        } else {
            entity.setLastUsedAt(LocalDateTime.now());
            saved = notificationTokenRepository.add(entity);
        }
        out.setId(saved.getId());
        out.setUserId(dto.getUserId());
        out.setToken(saved.getToken());
        return out;
    }

    @Override
    public List<NotificationTokenDTO> findByUserId(String userId) {
        return notificationTokenRepository.findByUserId(userId).stream().map(t -> {
            NotificationTokenDTO d = new NotificationTokenDTO();
            d.setId(t.getId());
            d.setUserId(userId);
            d.setToken(t.getToken());
            return d;
        }).collect(Collectors.toList());
    }

    @Override
    public void delete(NotificationTokenDTO dto) {
        if (dto == null || dto.getUserId() == null || dto.getToken() == null) {
            throw new ValidationException("userId and token are required");
        }
        notificationTokenRepository.findByUserId(dto.getUserId()).stream()
                .filter(t -> dto.getToken().equals(t.getToken()))
                .findFirst()
                .ifPresent(notificationTokenRepository::delete);
    }

    @Override
    public void unsubscribe(String userId) {
        if (isBlank(userId)) {
            throw new ValidationException("userId is required");
        }
        if (!notificationUnsubscriptionRepository.existsByUserId(userId)) {
            NotificationUnsubscription record = new NotificationUnsubscription();
            record.setUser(new UserProxy(userId));
            notificationUnsubscriptionRepository.add(record);
        }
        notificationTokenRepository.findByUserId(userId).forEach(notificationTokenRepository::delete);
    }

    private void validate(NotificationTokenDTO dto) {
        if (dto == null) {
            throw new ValidationException("Token payload is required");
        }
        if (isBlank(dto.getUserId())) {
            throw new ValidationException("userId is required");
        }
        if (isBlank(dto.getToken())) {
            throw new ValidationException("token is required");
        }
    }

    private boolean isBlank(String v) {
        return v == null || v.trim().isEmpty();
    }

    // Lightweight proxy to avoid loading full User entity just to set relation
    private static class UserProxy extends User {
        UserProxy(String id) {
            setId(id);
        }
    }
}
