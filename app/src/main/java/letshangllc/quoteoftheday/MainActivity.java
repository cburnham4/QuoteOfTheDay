package letshangllc.quoteoftheday;

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
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextView tvTime, tvQuote;

    /* Preferences */
    private int hour = 8, minute = 0;
    private boolean hasPermissions;
    private String quote;

    private static final String TAG = MainActivity.class.getSimpleName();

    private PendingIntent pendingIntent;
    private PreferencesManager prefManager;


    private AdsHelper adsHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.getPreferences();

        this.setupViews();
        /* Retrieve a PendingIntent that will perform a broadcast */
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);

        startAlarm();

        adsHelper = new AdsHelper(getWindow().getDecorView(), getResources().getString(R.string.admob_banner_main), this);
        adsHelper.runAds();
    }

    public void setupViews(){
        tvTime = (TextView) findViewById(R.id.tv_time);
        tvQuote = (TextView) findViewById(R.id.tvQuote);

        tvQuote.setText(quote);

        tvTime.setText(getFormattedTime(hour, minute));
        tvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    private void getPreferences(){
        prefManager = new PreferencesManager(this);
        hour = prefManager.getHour();
        minute = prefManager.getMinute();
        this.quote = prefManager.getQuote();
        this.hasPermissions = prefManager.hasPermission();
    }

    private void showTimePicker(){
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                tvTime.setText(getFormattedTime(selectedHour, selectedMinute));


                hour = selectedHour;
                minute = selectedMinute;

                prefManager.setTime(hour, minute);

                /* Cancel alarm and set new one */
                AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                manager.cancel(pendingIntent);
                startAlarm();
            }
        }, hour, minute, false);//Yes 24 hour time


        //timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }

    private void startAlarm(){
        Log.i("class", "Start Alarm");
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        if(calendar.getTimeInMillis() < System.currentTimeMillis()){
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            Log.i(TAG, "Add Day");
        }else{
            Log.i(TAG, "Don't add day");
        }


        /* Repeating on every day interval */
        manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_review_group:
                /* Go to create a new Task */
                startActivity(new Intent(MainActivity.this, FriendsListActivity.class));
                break;
            case R.id.action_add_person:
                addFriend();
                break;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
        return true;
    }

    private void addFriend(){
        //Check permissions if marshmellow or greater
        if((Build.VERSION.SDK_INT> Build.VERSION_CODES.LOLLIPOP_MR1 && !hasPermissions)){
            askForPermissions();
        }else{
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(intent, 0);
        }
    }

    int permsRequestCode = 200;
    public void askForPermissions(){

        String[] perms = {"android.permission.READ_CONTACTS", "android.permission.SEND_SMS"};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(perms, permsRequestCode);
        }

    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){
        switch(permsRequestCode){
            case 200:
                boolean contactsGranted = grantResults[0]==PackageManager.PERMISSION_GRANTED;
                boolean smsGranted = grantResults[1]==PackageManager.PERMISSION_GRANTED;
                if(smsGranted && contactsGranted){
                    prefManager.setHasPermission(true);
                    addFriend();
                }
                break;
        }
    }


    //code
    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        switch (reqCode) {
            case (0):
                if (resultCode == Activity.RESULT_OK) {

                    Uri contactData = data.getData();
                    Cursor c = managedQuery(contactData, null, null, null, null);
                    if (c.moveToFirst()) {


                        String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

                        String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                        String cNumber = "";
                        if (hasPhone.equalsIgnoreCase("1")) {
                            Cursor phones = getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                                    null, null);
                            phones.moveToFirst();
                            cNumber = phones.getString(phones.getColumnIndex("data1"));
                            System.out.println("number is:" + cNumber);
                        }
                        String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                        Log.i(TAG, name);
                        prefManager.addPerson(name, cNumber);
                    }
                }
                break;
        }
    }


    private static String getFormattedTime(int hour, int minutes){
        String dayPart = "AM";
        if(hour == 24){
            hour = 12;
        } else if(hour >= 12){
            hour %=12;
            dayPart = "PM";
            if(hour == 0){
                hour = 12;
            }
        }
        return String.format(Locale.getDefault(), "%02d:%02d %s", hour, minutes, dayPart);
    }
}
