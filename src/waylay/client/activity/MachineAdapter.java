package waylay.client.activity;

import java.util.ArrayList;

import waylay.client.data.MachineInfo;

import com.waylay.client.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MachineAdapter extends ArrayAdapter<MachineInfo> {
	
	private final Context context;
	private final ArrayList<MachineInfo> values;

	public MachineAdapter(Context context, ArrayList<MachineInfo> values) {
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
		textStatus.setText(values.get(position).getStatus());
		if("null".equals(textAddress) || textAddress == null){
			textAddress.setText("");
		} else {
			textAddress.setText(values.get(position).getIpAddress());
		}
		// Change the icon for Windows and iPhone
		MachineInfo m = values.get(position);
		if (!m.isValid()) {
			imageView.setImageResource(R.drawable.nok);
		} else {
			imageView.setImageResource(R.drawable.ok);
		}

		return rowView;
	}
	
}
