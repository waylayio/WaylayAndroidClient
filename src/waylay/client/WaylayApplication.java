package waylay.client;

import android.app.Application;

public class WaylayApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        com.estimote.sdk.utils.L.enableDebugLogging(true);
    }
}
