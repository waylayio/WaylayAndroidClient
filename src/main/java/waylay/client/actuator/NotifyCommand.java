package waylay.client.actuator;


import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;

import com.google.gson.JsonObject;

import waylay.client.R;
import waylay.client.WaylayApplication;

/**
 * Created by Jasper De Vrient on 19/05/2016.
 */
public class NotifyCommand implements AndroidCommand {
    private static final String MESSAGE = "message";
    private final WaylayApplication wapp;
    private static int id = 1;

    public NotifyCommand(WaylayApplication wapp) {
        this.wapp = wapp;
    }

    @Override
    public void runCommand(JsonObject parameters) {
        if (parameters.get(MESSAGE) != null) {
            NotificationManager nm =
                    (NotificationManager) wapp.getSystemService(wapp.NOTIFICATION_SERVICE);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(wapp.getApplicationContext())
                    .setSmallIcon(R.drawable.ic_waylay)
                    .setContentTitle("Waylay push notification")
                    .setContentText(parameters.get(MESSAGE).getAsString());

            nm.notify(id++, builder.build());
        }
    }
}
