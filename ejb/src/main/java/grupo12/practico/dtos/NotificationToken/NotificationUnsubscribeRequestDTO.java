package grupo12.practico.dtos.NotificationToken;

public class NotificationUnsubscribeRequestDTO {
    private String userCi;
    private String notificationType;

    public String getUserCi() {
        return userCi;
    }

    public void setUserCi(String userCi) {
        this.userCi = userCi;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }
}
