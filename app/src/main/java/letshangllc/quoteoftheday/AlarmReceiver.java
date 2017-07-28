package letshangllc.quoteoftheday;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

/**
 * Created by Carl on 11/1/2016.
 */

public class AlarmReceiver extends BroadcastReceiver {
    /* Volley request queue */
    private RequestQueue queue;

    private static final String TAG = AlarmReceiver.class.getSimpleName();

    private Context context;
    @Override
    public void onReceive(Context context, Intent intent) {


        Log.i(TAG, "Run Alarm");

        this.context = context;
        getQuotes();
    }

    //private void makeInitialHTTPRequest(){
        // Instantiate the RequestQueue.
//        queue = Volley.newRequestQueue(context);
//        String url = "http://quotes.rest/qod.json?category=inspire";
//
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
//                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
//
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.i(TAG, response.toString());
//                        parseResponse(response);
//                    }
//                }, new Response.ErrorListener() {
//
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.e(TAG, error.toString());
//
//                    }
//                });
//
//        // Add the request to the RequestQueue.
//        queue.add(jsonObjectRequest);
    //}

    private void getQuotes(){
        Log.i(TAG, "Get quotes ");
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Quotes");

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> messages = new ArrayList<>();
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    String author = dataSnapshot1.getKey();
                    String quote = (String) dataSnapshot1.getValue();
                    String message =String.format(Locale.getDefault(), "%s \n- %s", quote, author);
                    messages.add(message);
                }
                int randomInt = new Random().nextInt(messages.size());

                parseResponse(messages.get(randomInt));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };

        mDatabase.addListenerForSingleValueEvent(postListener);
    }

    private void parseResponse(String message){

        Log.i(TAG, message);

        PreferencesManager preferencesManager = new PreferencesManager(context);

        preferencesManager.setPrefQuote(message);
        ArrayList<Person> persons = preferencesManager.getPeople();

        Log.i(TAG, "Send Message to : "  +persons.size());
        String messageAlert = "Persons: ";
        for(Person person: persons){
            sendSMS(person.getNumber(), message);
            messageAlert += "| Name: "+ person.getName() +"| Number: " + person.getNumber()+"\n";
        }
        //sendSMS("5039297690", messageAlert);
        createNotification(message);

    }

    private void sendSMS(String phoneNumber, String message)
    {
        try {
            Log.i(TAG, "Send message");
            SmsManager sms = SmsManager.getDefault();
            ArrayList<String> parts = sms.divideMessage(message);
            sms.sendMultipartTextMessage(phoneNumber, null, parts, null, null);
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    private void createNotification(String message){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.trophy_114)
                        .setContentTitle("Motivational Quote")
                        .setContentText(message);
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, MainActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
    // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(6, mBuilder.build());
    }


}
