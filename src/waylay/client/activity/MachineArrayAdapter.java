package waylay.client.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.waylay.client.R;

public class MachineArrayAdapter extends ArrayAdapter<String> {
	private final Context context;
	private final String[] values;

	public MachineArrayAdapter(Context context, String[] values) {
		super(context, R.id.Machines, values);
		this.context = context;
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.id.Machines, parent, false);
		//TextView textView = (TextView) rowView.findViewById(R.id.label);
		//ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
		//textView.setText(values[position]);
		// Change the icon for Windows and iPhone
		String s = values[position];
		/*if (s.startsWith("iPhone")) {
			imageView.setImageResource(R.drawable.no);
		} else {
			imageView.setImageResource(R.drawable.ok);
		}*/

		return rowView;
	}
}