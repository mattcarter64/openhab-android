package org.openhab.habdroid.core;

import android.util.Log;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import org.openhab.habdroid.R;
import org.openhab.habdroid.ui.MainActivity;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

public class McsoftFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = McsoftFirebaseMessagingService.class.getSimpleName();

    public McsoftFirebaseMessagingService() {
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "message received:");
        Log.d(TAG, "from: " + remoteMessage.getFrom());
        Log.d(TAG, "notification: " + remoteMessage.getNotification());
        Log.d(TAG, "data: " + remoteMessage.getData());

        // Check if message contains a data payload.
        if (Objects.requireNonNull(remoteMessage.getNotification()).getTitle() != null) {

            String id = (remoteMessage.getData().isEmpty() || remoteMessage.getData().get("id") == null) ? "1" : remoteMessage.getData().get("id");

            sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(),
                Integer.parseInt(id));
        }
    }

    private void sendNotification(String title, String text, int id) {

        Log.d(TAG, "send notification: title=" + title + ", text=" + title + ", id=" + id);

        Intent intent = new Intent(this, MainActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        String channelId = getString(R.string.default_notification_channel_id);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder =
            new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.icon)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(id /* ID of notification */, notificationBuilder.build());
    }
}
