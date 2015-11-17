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

import waylay.client.R;

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
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.rowlayout, parent, false);
        }
        TextView textHost = (TextView) convertView.findViewById(R.id.title);
        TextView textStatus = (TextView) convertView.findViewById(R.id.subtitle);
        TextView textAddress = (TextView) convertView.findViewById(R.id.rightcorner);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.icon);
        AbstractLocalSensor sensor = values.get(position);
        textHost.setText(sensor.getName());
        textStatus.setText(sensor.toString());
        textAddress.setText(sensor.getStatus());
        // TODO show other image when pushing
        imageView.setImageResource(R.drawable.sensor);
		return convertView;
	}
}