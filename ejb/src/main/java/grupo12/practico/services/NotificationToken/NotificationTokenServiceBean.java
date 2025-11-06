package grupo12.practico.services.NotificationToken;

import java.util.List;
import java.util.stream.Collectors;

import grupo12.practico.dtos.HealthUser.HealthUserDTO;
import grupo12.practico.dtos.NotificationToken.NotificationTokenDTO;
import grupo12.practico.services.HealthUser.HealthUserServiceLocal;
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

    @EJB
    private HealthUserServiceLocal healthUserService;

    @Override
    public NotificationTokenDTO add(NotificationTokenDTO dto) {
        validate(dto);

        HealthUserDTO user = healthUserService.findByCi(dto.getUserCi());
        if (user == null) {
            throw new ValidationException("User not found with CI: " + dto.getUserCi());
        }

        if (notificationUnsubscriptionRepository.existsByUserId(user.getId())) {
            throw new ValidationException("User has unsubscribed from notifications");
        }
        NotificationToken entity = new NotificationToken();
        NotificationTokenDTO out = new NotificationTokenDTO();
        NotificationToken saved = null;
        User dbUser = new UserProxy(user.getId());
        entity.setUser(dbUser);
        entity.setToken(dto.getToken());
        if (notificationTokenRepository.findByToken(dto.getToken()) != null) {
            saved = notificationTokenRepository.updateLastUsedAt(dto.getToken());
        } else {
            entity.setLastUsedAt(LocalDateTime.now());
            saved = notificationTokenRepository.add(entity);
        }
        out.setId(saved.getId());
        out.setUserCi(dto.getUserCi());
        out.setToken(saved.getToken());
        return out;
    }

    @Override
    public List<NotificationTokenDTO> findByUserCi(String userCi) {
        HealthUserDTO user = healthUserService.findByCi(userCi);
        if (user == null) {
            throw new ValidationException("User not found with CI: " + userCi);
        }

        return notificationTokenRepository.findByUserId(user.getId()).stream().map(t -> {
            NotificationTokenDTO d = new NotificationTokenDTO();
            d.setId(t.getId());
            d.setUserCi(userCi);
            d.setToken(t.getToken());
            return d;
        }).collect(Collectors.toList());
    }

    @Override
    public void delete(NotificationTokenDTO dto) {
        if (dto == null || dto.getUserCi() == null || dto.getToken() == null) {
            throw new ValidationException("userCi and token are required");
        }

        HealthUserDTO user = healthUserService.findByCi(dto.getUserCi());
        if (user == null) {
            throw new ValidationException("User not found with CI: " + dto.getUserCi());
        }

        notificationTokenRepository.findByUserId(user.getId()).stream()
                .filter(t -> dto.getToken().equals(t.getToken()))
                .findFirst()
                .ifPresent(notificationTokenRepository::delete);
    }

    @Override
    public void unsubscribe(String userCi) {
        if (isBlank(userCi)) {
            throw new ValidationException("userCi is required");
        }

        HealthUserDTO user = healthUserService.findByCi(userCi);
        if (user == null) {
            throw new ValidationException("User not found with CI: " + userCi);
        }

        if (!notificationUnsubscriptionRepository.existsByUserId(user.getId())) {
            NotificationUnsubscription record = new NotificationUnsubscription();
            record.setUser(new UserProxy(user.getId()));
            notificationUnsubscriptionRepository.add(record);
        }
        notificationTokenRepository.findByUserId(user.getId()).forEach(notificationTokenRepository::delete);
    }

    @Override
    public void subscribe(String userCi) {
        if (isBlank(userCi)) {
            throw new ValidationException("userCi is required");
        }

        HealthUserDTO user = healthUserService.findByCi(userCi);
        if (user == null) {
            throw new ValidationException("User not found with CI: " + userCi);
        }

        notificationUnsubscriptionRepository.remove(user.getId());
    }

    private void validate(NotificationTokenDTO dto) {
        if (dto == null) {
            throw new ValidationException("Token payload is required");
        }
        if (isBlank(dto.getUserCi())) {
            throw new ValidationException("userCi is required");
        }
        if (isBlank(dto.getToken())) {
            throw new ValidationException("token is required");
        }
    }

    private boolean isBlank(String v) {
        return v == null || v.trim().isEmpty();
    }

    private static class UserProxy extends User {
        UserProxy(String id) {
            setId(id);
        }
    }
}
