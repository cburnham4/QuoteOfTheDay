package letshangllc.quoteoftheday;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.telephony.SmsManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import java.util.ArrayList;

/**
 * Daily alarm: pick a bundled quote, notify the user, and SMS friends if configured.
 */
public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = AlarmReceiver.class.getSimpleName();
    public static final String CHANNEL_ID = "daily_quote_channel";
    private static final int NOTIFICATION_ID = 6;

    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Run Alarm");
        this.context = context;
        deliverQuote(LocalQuotes.randomQuote());
    }

    private void deliverQuote(String message) {
        Log.i(TAG, message);

        PreferencesManager preferencesManager = new PreferencesManager(context);
        preferencesManager.setPrefQuote(message);

        ArrayList<Person> persons = preferencesManager.getPeople();
        Log.i(TAG, "Send Message to : " + persons.size());
        for (Person person : persons) {
            sendSMS(person.getNumber(), message);
        }
        createNotification(message);
    }

    private void sendSMS(String phoneNumber, String message) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return;
        }
        try {
            Log.i(TAG, "Send message");
            SmsManager sms = SmsManager.getDefault();
            ArrayList<String> parts = sms.divideMessage(message);
            sms.sendMultipartTextMessage(phoneNumber, null, parts, null, null);
        } catch (Exception e) {
            Log.e(TAG, "Failed to send SMS", e);
        }
    }

    private void createNotification(String message) {
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

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.trophy_114)
                .setContentTitle("Motivational Quote")
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
