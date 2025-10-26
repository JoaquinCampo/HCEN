package grupo12.practico.services.NotificationToken;

import java.util.List;
import java.util.stream.Collectors;

import grupo12.practico.dtos.NotificationToken.NotificationTokenDTO;
import grupo12.practico.models.NotificationToken;
import grupo12.practico.models.User;
import grupo12.practico.repositories.NotificationToken.NotificationTokenRepositoryLocal;
import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.validation.ValidationException;

@Stateless
@Local(NotificationTokenServiceLocal.class)
@Remote(NotificationTokenServiceRemote.class)
public class NotificationTokenServiceBean implements NotificationTokenServiceRemote {

    @EJB
    private NotificationTokenRepositoryLocal notificationTokenRepository;

    @Override
    public NotificationTokenDTO add(NotificationTokenDTO dto) {
        validate(dto);
        NotificationToken entity = new NotificationToken();
        User user = new UserProxy(dto.getUserId());
        entity.setUser(user);
        entity.setToken(dto.getToken());
        NotificationToken saved = notificationTokenRepository.add(entity);
        NotificationTokenDTO out = new NotificationTokenDTO();
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
