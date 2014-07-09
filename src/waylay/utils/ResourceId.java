package waylay.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;

/**
 * from http://stackoverflow.com/questions/2785485/is-there-a-unique-android-device-id
 *
 * Warning: Id is lost on app uninstall, see above link for other solutions
 */
public final class ResourceId {
    private static String uniqueID = null;
    private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";

    public synchronized static String get(Context context) {
        if (uniqueID == null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(PREF_UNIQUE_ID, Context.MODE_PRIVATE);
            uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null);
            if (uniqueID == null) {
                uniqueID = UUID.randomUUID().toString();
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString(PREF_UNIQUE_ID, uniqueID);
                editor.commit();
            }
        }
        return uniqueID;
    }

    public synchronized static String set(Context context, String resource) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(PREF_UNIQUE_ID, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(PREF_UNIQUE_ID, resource);
        editor.commit();
        uniqueID = resource;
        return uniqueID;
    }
}
