package waylay.client.activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.waylay.client.R;

public class AlertDialogActivity extends BaseActivity {

    public static final String EXTRA_MESSAGE = "message";

    // TODO why this showing another activity?
    @Deprecated
    public static void showOld(Context context, String message){
        Intent i = new Intent(context, AlertDialogActivity.class)
                .putExtra(EXTRA_MESSAGE, message);
        context.startActivity(i);
    }

    public static void show(Context context, String message){
        new AlertDialog.Builder(context)
                .setTitle("Alert")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                })
        .create()
        .show();
    }

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.alert);

        Button button = viewById(R.id.buttonAlert);

		// add button listener
		button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                showDialog();
            }
        });
	}

    private void showDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle("Alert");

        // set dialog message
        alertDialogBuilder.setMessage(getIntent().getStringExtra(EXTRA_MESSAGE))
                .setCancelable(false)
                .setPositiveButton("Dismiss",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                        AlertDialogActivity.this.finish();
                    }
                });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
}