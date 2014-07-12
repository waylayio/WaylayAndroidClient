package waylay.client.activity;

import java.util.ArrayList;
import waylay.client.scenario.Task;
import waylay.client.scenario.ScenarioStatus;

import com.waylay.client.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ScenarioAdapter extends ArrayAdapter<Task> {
	
	private final Context context;
	private final ArrayList<Task> values;

	public ScenarioAdapter(Context context, ArrayList<Task> values) {
		super(context, R.layout.rowlayout, values);
		this.context = context;
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.rowlayout, parent, false);
        }
		TextView textTitle = (TextView) convertView.findViewById(R.id.title);
		TextView textStatus = (TextView) convertView.findViewById(R.id.subtitle);
		TextView textID = (TextView) convertView.findViewById(R.id.rightcorner);
		ImageView imageView = (ImageView) convertView.findViewById(R.id.icon);
		textTitle.setText(values.get(position).getName());
		textStatus.setText(values.get(position).getScenarioStatus().toString());
	    textID.setText(values.get(position).getId().toString());

		Task m = values.get(position);
		if (!m.getScenarioStatus().equals(ScenarioStatus.RUNNING)) {
			imageView.setImageResource(R.drawable.nok);
		} else {
			imageView.setImageResource(R.drawable.ok);
		}

		return convertView;
	}
}
