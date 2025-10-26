package grupo12.practico.services.PushNotificationSender;

import jakarta.ejb.Local;

@Local
public interface PushNotificationServiceLocal {

    void sendPushNotification(String title, String body);

    void sendPushNotificationToToken(String title, String body, String token);

    void sendPushNotificationToTopic(String title, String body, String topic);
}
