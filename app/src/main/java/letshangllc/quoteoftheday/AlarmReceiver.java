package letshangllc.quoteoftheday;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

/**
 * Daily alarm: pick a bundled quote and notify the user.
 * Sharing uses the system share sheet from MainActivity (no SMS permission).
 */
public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = AlarmReceiver.class.getSimpleName();
    public static final String CHANNEL_ID = "daily_quote_channel";
    private static final int NOTIFICATION_ID = 6;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Run Alarm");
        String message = LocalQuotes.randomQuote();
        Log.i(TAG, message);

        PreferencesManager preferencesManager = new PreferencesManager(context);
        preferencesManager.setPrefQuote(message);
        createNotification(context, message);
    }

    private void createNotification(Context context, String message) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Daily Motivational Quotes",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Daily quote reminders");
            notificationManager.createNotificationChannel(channel);
        }

        Intent resultIntent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Intent shareIntent = new Intent(context, MainActivity.class);
        shareIntent.putExtra(MainActivity.EXTRA_SHARE_QUOTE, true);
        PendingIntent sharePendingIntent = PendingIntent.getActivity(
                context,
                1,
                shareIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.trophy_114)
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentIntent(resultPendingIntent)
                .addAction(0, context.getString(R.string.share_quote), sharePendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
