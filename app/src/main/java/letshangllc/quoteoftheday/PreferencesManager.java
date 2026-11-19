package letshangllc.quoteoftheday;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PreferencesManager {
    private static final String TAG = PreferencesManager.class.getSimpleName();

    private final SharedPreferences pref;

    private static final String PREF_NAME = "quote_daily_preferences";
    private static final String PREF_HOUR = "pref_hour";
    private static final String PREF_MINUTE = "pref_minute";
    private static final String PREF_PEOPLE = "pref_people";
    private static final String PREF_QUOTE = "pref_quote";
    private static final String PREF_HAS_PERMISSIONS = "pref_has_permissions";

    public PreferencesManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void setTime(int hour, int minute) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(PREF_HOUR, hour);
        editor.putInt(PREF_MINUTE, minute);
        editor.apply();
    }

    public void addPerson(String name, String number) {
        SharedPreferences.Editor editor = pref.edit();

        Gson gson = new Gson();
        String jsonPeople = pref.getString(PREF_PEOPLE, "");
        Type type = new TypeToken<List<Person>>() {}.getType();

        ArrayList<Person> persons = gson.fromJson(jsonPeople, type);
        if (persons == null) {
            persons = new ArrayList<>();
        }
        persons.add(new Person(name, number));

        String json = gson.toJson(persons);
        editor.putString(PREF_PEOPLE, json);
        editor.apply();
    }

    public void setPrefQuote(String quote) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PREF_QUOTE, quote);
        editor.apply();
    }

    public ArrayList<Person> getPeople() {
        Gson gson = new Gson();
        String jsonPeople = pref.getString(PREF_PEOPLE, "");
        Type type = new TypeToken<List<Person>>() {}.getType();

        ArrayList<Person> persons = gson.fromJson(jsonPeople, type);
        if (persons == null) {
            return new ArrayList<>();
        }
        return persons;
    }

    public void removePerson(Person person, int index) {
        SharedPreferences.Editor editor = pref.edit();

        Gson gson = new Gson();
        String jsonPeople = pref.getString(PREF_PEOPLE, "");
        Type type = new TypeToken<List<Person>>() {}.getType();

        ArrayList<Person> persons = gson.fromJson(jsonPeople, type);
        if (persons != null) {
            Log.i(TAG, "Persons not null");
            boolean isRemoved = persons.remove(person);

            Log.i(TAG, "Person Removed: " + isRemoved);
            if (!isRemoved && index >= 0 && index < persons.size()) {
                persons.remove(index);
            }

            String json = gson.toJson(persons);
            editor.putString(PREF_PEOPLE, json);
            editor.apply();
        }
    }

    public void setHasPermission(boolean hasPermission) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(PREF_HAS_PERMISSIONS, hasPermission);
        editor.apply();
    }

    public boolean hasPermission() {
        return pref.getBoolean(PREF_HAS_PERMISSIONS, false);
    }

    public int getHour() {
        return pref.getInt(PREF_HOUR, 8);
    }

    public int getMinute() {
        return pref.getInt(PREF_MINUTE, 0);
    }

    public String getQuote() {
        return pref.getString(PREF_QUOTE, LocalQuotes.randomQuote());
    }
}
