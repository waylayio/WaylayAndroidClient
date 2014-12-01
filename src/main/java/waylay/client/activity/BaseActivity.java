package waylay.client.activity;

import android.support.v7.app.ActionBarActivity;

import waylay.client.WaylayApplication;

public class BaseActivity extends ActionBarActivity implements LoadingListener{

    //private ProgressDialog progress;

    private int loadingCount = 0;

    protected void alert(String message) {
        AlertDialogActivity.show(this, message);
    }

    @SuppressWarnings("unchecked")
    protected <T> T viewById(int resourceId){
        return (T) findViewById(resourceId);
    }

    @SuppressWarnings("unchecked")
    protected <T> T fragmentByTag(String tag){
        return (T) getSupportFragmentManager().findFragmentByTag(tag);
    }

    public void startLoading(){
//        if(loadingCount == 0) {
//            progress = ProgressDialog.show(this, "", "Loading. Please wait...", true);
//        }
        loadingCount++;
        try {
            setSupportProgressBarIndeterminateVisibility(true);
        }catch(NullPointerException ex){
            // this happens on lollipop
        }
    }

    public void endLoading(){
        loadingCount = Math.max(0, loadingCount -1);
        if(loadingCount == 0) {
//            if(progress != null){
//                progress.dismiss();
//            }
            try{
                setSupportProgressBarIndeterminateVisibility(false);
            }catch(NullPointerException ex){
                // this happens on lollipop
            }
        }
    }

    public WaylayApplication getWaylayApplication(){
        return (WaylayApplication) getApplication();
    }
}
