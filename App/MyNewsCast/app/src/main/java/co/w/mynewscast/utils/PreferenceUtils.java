package co.w.mynewscast.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import co.w.mynewscast.MyNewsCastApplication;

public class PreferenceUtils {
    private static SharedPreferences getDefaultSharedPreference(Context context) {
        if (PreferenceManager.getDefaultSharedPreferences(MyNewsCastApplication.getInstance().getApplicationContext()) != null)
            return PreferenceManager.getDefaultSharedPreferences(MyNewsCastApplication.getInstance().getApplicationContext());
        else
            return null;
    }

    public static void setSelectedLanguageId(String id){
        final SharedPreferences prefs = getDefaultSharedPreference(MyNewsCastApplication.getInstance().getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("app_language_id", id);
        editor.apply();
    }

    public static String getSelectedLanguageId(){
        return getDefaultSharedPreference(MyNewsCastApplication.getInstance().getApplicationContext())
                .getString("app_language_id", "en");
    }
}
