package letshangllc.quoteoftheday;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {

    private final SharedPreferences pref;

    private static final String PREF_NAME = "quote_daily_preferences";
    private static final String PREF_HOUR = "pref_hour";
    private static final String PREF_MINUTE = "pref_minute";
    private static final String PREF_QUOTE = "pref_quote";

    public PreferencesManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void setTime(int hour, int minute) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(PREF_HOUR, hour);
        editor.putInt(PREF_MINUTE, minute);
        editor.apply();
    }

    public void setPrefQuote(String quote) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PREF_QUOTE, quote);
        editor.apply();
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
