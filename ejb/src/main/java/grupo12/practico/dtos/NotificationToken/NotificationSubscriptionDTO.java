package grupo12.practico.dtos.NotificationToken;

public class NotificationSubscriptionDTO {
    private String userCi;
    private boolean subscribedToAccessRequest;
    private boolean subscribedToClinicalHistoryAccess;

    public NotificationSubscriptionDTO() {
    }

    public NotificationSubscriptionDTO(String userCi, boolean subscribedToAccessRequest,
            boolean subscribedToClinicalHistoryAccess) {
        this.userCi = userCi;
        this.subscribedToAccessRequest = subscribedToAccessRequest;
        this.subscribedToClinicalHistoryAccess = subscribedToClinicalHistoryAccess;
    }

    public String getUserCi() {
        return userCi;
    }

    public void setUserCi(String userCi) {
        this.userCi = userCi;
    }

    public boolean isSubscribedToAccessRequest() {
        return subscribedToAccessRequest;
    }

    public void setSubscribedToAccessRequest(boolean subscribedToAccessRequest) {
        this.subscribedToAccessRequest = subscribedToAccessRequest;
    }

    public boolean isSubscribedToClinicalHistoryAccess() {
        return subscribedToClinicalHistoryAccess;
    }

    public void setSubscribedToClinicalHistoryAccess(boolean subscribedToClinicalHistoryAccess) {
        this.subscribedToClinicalHistoryAccess = subscribedToClinicalHistoryAccess;
    }
}