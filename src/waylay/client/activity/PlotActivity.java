package waylay.client.activity;


import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import com.waylay.client.R;

public class PlotActivity extends Activity {
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plot_sso);
        ListView gridview = (ListView) findViewById(R.id.listPlotview);
        gridview.setAdapter(new PlotAdapter(this));
    }
}