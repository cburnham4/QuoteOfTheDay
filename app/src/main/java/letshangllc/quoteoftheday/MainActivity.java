package letshangllc.quoteoftheday;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private TextView tvTime, tvQuote;

    private int hour = 8, minute = 0;
    private boolean hasPermissions;
    private String quote;

    private static final String TAG = MainActivity.class.getSimpleName();

    private PendingIntent pendingIntent;
    private PreferencesManager prefManager;
    private AdsHelper adsHelper;

    private final ActivityResultLauncher<Intent> pickContactLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    handleContactPicked(result.getData().getData());
                }
            });

    private final ActivityResultLauncher<String[]> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), this::onPermissionsResult);

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
    }

    private void maybeRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(new String[]{Manifest.permission.POST_NOTIFICATIONS});
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
        this.hasPermissions = prefManager.hasPermission();
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
        if (id == R.id.action_review_group) {
            startActivity(new Intent(MainActivity.this, FriendsListActivity.class));
            return true;
        } else if (id == R.id.action_add_person) {
            addFriend();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addFriend() {
        if (!hasPermissions) {
            askForPermissions();
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            pickContactLauncher.launch(intent);
        }
    }

    public void askForPermissions() {
        ArrayList<String> perms = new ArrayList<>();
        perms.add(Manifest.permission.READ_CONTACTS);
        perms.add(Manifest.permission.SEND_SMS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            perms.add(Manifest.permission.POST_NOTIFICATIONS);
        }
        permissionLauncher.launch(perms.toArray(new String[0]));
    }

    private void onPermissionsResult(Map<String, Boolean> results) {
        boolean contactsGranted = Boolean.TRUE.equals(results.get(Manifest.permission.READ_CONTACTS));
        boolean smsGranted = Boolean.TRUE.equals(results.get(Manifest.permission.SEND_SMS));
        if (contactsGranted && smsGranted) {
            prefManager.setHasPermission(true);
            hasPermissions = true;
            addFriend();
        } else if (!results.containsKey(Manifest.permission.READ_CONTACTS)
                && !results.containsKey(Manifest.permission.SEND_SMS)) {
            // Only notification permission was requested
            Log.i(TAG, "Notification permission result received");
        } else {
            Toast.makeText(this, "Contacts and SMS permission are required to add friends", Toast.LENGTH_LONG).show();
        }
    }

    private void handleContactPicked(Uri contactData) {
        if (contactData == null) {
            return;
        }
        try (Cursor c = getContentResolver().query(contactData, null, null, null, null)) {
            if (c == null || !c.moveToFirst()) {
                return;
            }

            String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
            int hasPhoneIndex = c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
            String hasPhone = hasPhoneIndex >= 0 ? c.getString(hasPhoneIndex) : "0";
            String cNumber = "";

            if ("1".equalsIgnoreCase(hasPhone)) {
                try (Cursor phones = getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        new String[]{id},
                        null)) {
                    if (phones != null && phones.moveToFirst()) {
                        int numberIndex = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        if (numberIndex >= 0) {
                            cNumber = phones.getString(numberIndex);
                        }
                    }
                } catch (SecurityException e) {
                    Toast.makeText(this, "Error Getting Contact Data", Toast.LENGTH_SHORT).show();
                }
            }

            String name = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
            Log.i(TAG, name);
            prefManager.addPerson(name, cNumber);
        }
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
