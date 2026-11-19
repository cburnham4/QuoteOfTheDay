package letshangllc.quoteoftheday;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_SHARE_QUOTE = "extra_share_quote";

    private TextView tvTime, tvQuote;

    private int hour = 8, minute = 0;
    private String quote;

    private static final String TAG = MainActivity.class.getSimpleName();

    private PendingIntent pendingIntent;
    private PreferencesManager prefManager;
    private AdsHelper adsHelper;

    private final ActivityResultLauncher<String> notificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted ->
                    Log.i(TAG, "POST_NOTIFICATIONS granted=" + granted));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View root = findViewById(R.id.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getPreferences();
        setupViews();

        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (pendingIntent != null && manager != null) {
            manager.cancel(pendingIntent);
        }
        pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        startAlarm();
        maybeRequestNotificationPermission();

        adsHelper = new AdsHelper(
                getWindow().getDecorView(),
                getString(R.string.admob_banner_main),
                this
        );
        adsHelper.runAds();

        maybeShareFromIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        maybeShareFromIntent(intent);
    }

    private void maybeShareFromIntent(Intent intent) {
        if (intent != null && intent.getBooleanExtra(EXTRA_SHARE_QUOTE, false)) {
            intent.removeExtra(EXTRA_SHARE_QUOTE);
            QuoteShareHelper.shareViaShareSheet(this, quote);
        }
    }

    private void maybeRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    public void setupViews() {
        tvTime = findViewById(R.id.tv_time);
        tvQuote = findViewById(R.id.tvQuote);

        tvQuote.setText(quote);
        tvTime.setText(getFormattedTime(hour, minute));
        tvTime.setOnClickListener(view -> showTimePicker());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void getPreferences() {
        prefManager = new PreferencesManager(this);
        hour = prefManager.getHour();
        minute = prefManager.getMinute();
        this.quote = prefManager.getQuote();
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (TimePicker timePicker, int selectedHour, int selectedMinute) -> {
            tvTime.setText(getFormattedTime(selectedHour, selectedMinute));
            hour = selectedHour;
            minute = selectedMinute;
            prefManager.setTime(hour, minute);

            AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (manager != null && pendingIntent != null) {
                manager.cancel(pendingIntent);
            }
            startAlarm();
        }, hour, minute, false);

        timePickerDialog.show();
    }

    private void startAlarm() {
        Log.i(TAG, "Start Alarm");
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (manager == null || pendingIntent == null) {
            return;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            Log.i(TAG, "Add Day");
        } else {
            Log.i(TAG, "Don't add day");
        }

        manager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_share_quote) {
            QuoteShareHelper.shareViaShareSheet(this, quote);
            return true;
        } else if (id == R.id.action_send_message) {
            QuoteShareHelper.shareViaSmsApp(this, quote);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (adsHelper != null) {
            adsHelper.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh quote if the daily alarm updated it while we were in background
        String latest = prefManager.getQuote();
        if (latest != null && !latest.equals(quote)) {
            quote = latest;
            tvQuote.setText(quote);
        }
        if (adsHelper != null) {
            adsHelper.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        if (adsHelper != null) {
            adsHelper.onDestroy();
        }
        super.onDestroy();
    }

    private static String getFormattedTime(int hour, int minutes) {
        String dayPart = "AM";
        if (hour == 24) {
            hour = 12;
        } else if (hour >= 12) {
            hour %= 12;
            dayPart = "PM";
            if (hour == 0) {
                hour = 12;
            }
        }
        return String.format(Locale.getDefault(), "%02d:%02d %s", hour, minutes, dayPart);
    }
}
