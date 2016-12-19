package letshangllc.quoteoftheday;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.android.volley.VolleyLog.TAG;

/**
 * Created by Carl on 11/1/2016.
 */

public class PreferencesManager {
    SharedPreferences pref;
    Context context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "quote_daily_preferences";
    private static final String PREF_HOUR = "pref_hour";
    private static final String PREF_MINUTE = "pref_minute";
    private static final String PREF_PEOPLE = "pref_people";
    private static final String PREF_QUOTE = "pref_quote";
    private static final String PREF_HAS_PERMISSIONS = "pref_has_permissions";

    public PreferencesManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
    }

    public void setTime(int hour, int minute) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(PREF_HOUR, hour);
        editor.putInt(PREF_MINUTE, minute);
        editor.commit();
    }

    public void addPerson(String name, String number){
        SharedPreferences.Editor editor = pref.edit();

        Gson gson = new Gson();
        String jsonPeople = pref.getString(PREF_PEOPLE, "");
        Type type = new TypeToken<List<Person>>() {}.getType();

        ArrayList<Person> persons = gson.fromJson(jsonPeople, type);
        if(persons == null){
            persons = new ArrayList<>();
        }
        persons.add(new Person(name, number));

        gson = new Gson();
        String json = gson.toJson(persons);
        editor.putString(PREF_PEOPLE, json);

        editor.commit();
    }

    public void setPrefQuote(String quote){
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PREF_QUOTE, quote);
        editor.commit();
    }

    public ArrayList<Person> getPeople(){
        Gson gson = new Gson();
        String jsonPeople = pref.getString(PREF_PEOPLE, "");
        Type type = new TypeToken<List<Person>>() {}.getType();

        ArrayList<Person> persons = gson.fromJson(jsonPeople, type);
        if(persons == null){
            return new ArrayList<>();
        }
        return persons;
    }

    public void removePerson(Person person, int index){
        SharedPreferences.Editor editor = pref.edit();

        Gson gson = new Gson();
        String jsonPeople = pref.getString(PREF_PEOPLE, "");
        Type type = new TypeToken<List<Person>>() {}.getType();

        ArrayList<Person> persons = gson.fromJson(jsonPeople, type);
        if(persons != null){
            Log.i(TAG, "Persons not null");
            boolean isRemoved = persons.remove(person);

            Log.i(TAG, "Person Removed: " + isRemoved);
            if(isRemoved == false){
                persons.remove(index);
            }

            gson = new Gson();
            String json = gson.toJson(persons);
            editor.putString(PREF_PEOPLE, json);

            editor.commit();
        }


    }

    public void setHasPermission(boolean hasPermission){
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(PREF_HAS_PERMISSIONS, hasPermission);
        editor.commit();
    }

    public boolean hasPermission(){
        return pref.getBoolean(PREF_HAS_PERMISSIONS, false);
    }


    public int getHour() {
        return pref.getInt(PREF_HOUR, 8);
    }

    public int getMinute(){
        return pref.getInt(PREF_MINUTE, 0);
    }

    public String getQuote(){
        return pref.getString(PREF_QUOTE, "In order to succeed, we must first believe we can. \n- Nikos Kazantzakis");
    }

}
