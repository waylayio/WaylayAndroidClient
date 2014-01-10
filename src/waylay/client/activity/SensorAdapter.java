package waylay.client.activity;

import java.util.ArrayList;

import waylay.client.sensor.LocalSensor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.waylay.client.R;

public class SensorAdapter extends ArrayAdapter<LocalSensor> {
	
	private final Context context;
	private final ArrayList<LocalSensor> values;

	public SensorAdapter(Context context, ArrayList<LocalSensor> values) {
		super(context, R.layout.rowlayout, values);
		this.context = context;
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.rowlayout, parent, false);
		TextView textHost = (TextView) rowView.findViewById(R.id.title);
		TextView textStatus = (TextView) rowView.findViewById(R.id.subtitle);
		TextView textAddress = (TextView) rowView.findViewById(R.id.rightcorner);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
		textHost.setText(values.get(position).getName());
		textStatus.setText(values.get(position).toString());
		textAddress.setText(values.get(position).getStatus());
		LocalSensor m = values.get(position);
		imageView.setImageResource(R.drawable.user);
		return rowView;
	}
}