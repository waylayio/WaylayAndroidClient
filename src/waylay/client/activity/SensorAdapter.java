package waylay.client.activity;

import java.util.List;

import waylay.client.sensor.AbstractLocalSensor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.waylay.client.R;

public class SensorAdapter extends ArrayAdapter<AbstractLocalSensor> {
	
	private final Context context;
	private final List<AbstractLocalSensor> values;

	public SensorAdapter(Context context, List<AbstractLocalSensor> values) {
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
		AbstractLocalSensor m = values.get(position);
		imageView.setImageResource(R.drawable.sensor);
		return rowView;
	}
}