package grupo12.practico.services.PushNotificationSender;

import jakarta.ejb.Stateless;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

@Stateless
@Local(PushNotificationServiceLocal.class)
@Remote(PushNotificationServiceRemote.class)
public class PushNotificationServiceBean implements PushNotificationServiceRemote {
    @Override
    public void sendPushNotification(String title, String body) {
        ensureFirebaseInitialized();
        // No target provided in legacy method; choose a default strategy: no-op or
        // throw.
        throw new UnsupportedOperationException(
                "sendPushNotification(title, body) is deprecated; use token or topic variant.");
    }

    public void sendPushNotificationToToken(String title, String body, String token) {
        ensureFirebaseInitialized();
        FirebaseMessaging messaging = FirebaseMessaging.getInstance();
        Message messageToSend = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();
        try {
            messaging.send(messageToSend);
        } catch (FirebaseMessagingException e) {
            if ("registration-token-not-registered".equals(e.getErrorCode())) {
                // Consider removing token from storage in the caller
            }
            throw new RuntimeException("Failed to send push notification to token", e);
        }
    }

    public void sendPushNotificationToTopic(String title, String body, String topic) {
        ensureFirebaseInitialized();
        FirebaseMessaging messaging = FirebaseMessaging.getInstance();
        Message messageToSend = Message.builder()
                .setTopic(topic)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();
        try {
            messaging.send(messageToSend);
        } catch (FirebaseMessagingException e) {
            throw new RuntimeException("Failed to send push notification to topic", e);
        }
    }

    private void ensureFirebaseInitialized() {
        if (FirebaseApp.getApps().isEmpty()) {
            try {
                System.out.println(
                        "Checking if Firebase PK file exists: " + new File("/opt/secrets/firebase-sa.json").exists());
                FileInputStream serviceAccount = new FileInputStream("/opt/secrets/firebase-sa.json");
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                FirebaseApp.initializeApp(options);
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Didn't find the Firebase PK file", e);
            } catch (IOException e) {
                throw new RuntimeException("Failed to initialize Firebase", e);
            }
        }
    }
}
