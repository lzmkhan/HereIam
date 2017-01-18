package crystrom.appsolut.hereiam;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.IBinder;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessagingService extends FirebaseMessagingService {
    public MessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        NotificationManager nm =(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(getApplicationContext())
                                                        .setSmallIcon(R.drawable.common_full_open_on_phone)
                                                        .setContentTitle("HereIam")
                                                        .setContentText(remoteMessage.getNotification().getBody())
                                                        .setColor(70123245);
        nm.notify(1,builder.build());

    }
}
