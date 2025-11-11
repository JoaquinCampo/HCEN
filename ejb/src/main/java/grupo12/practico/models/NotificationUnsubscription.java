package grupo12.practico.models;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "notification_unsubscriptions", uniqueConstraints = {
        @UniqueConstraint(name = "uq_notification_unsubscription_user", columnNames = { "user_id" })
})
public class NotificationUnsubscription {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "subscribed_to_access_request", nullable = false)
    private boolean subscribedToAccessRequest = true;

    @Column(name = "subscribed_to_clinical_history_access", nullable = false)
    private boolean subscribedToClinicalHistoryAccess = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public NotificationUnsubscription() {
    }

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
