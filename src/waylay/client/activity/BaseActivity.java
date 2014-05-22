package waylay.client.activity;

import android.app.Activity;
import android.content.Intent;

public class BaseActivity extends Activity{

    protected void alert(String message) {
        AlertDialogActivity.show(this, message);
    }

    @SuppressWarnings("unchecked")
    protected <T> T viewById(int resourceId){
        return (T) findViewById(resourceId);
    }

    @SuppressWarnings("unchecked")
    protected <T> T fragmentByTag(String tag){
        return (T) getFragmentManager().findFragmentByTag(tag);
    }
}
