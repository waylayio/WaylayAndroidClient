package waylay.client.activity;

import java.util.ArrayList;

import waylay.client.data.BayesServer;

import com.waylay.client.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SetupAdapter extends ArrayAdapter<BayesServer> {
	
	private final Context context;
	private final ArrayList<BayesServer> values;

	public SetupAdapter(Context context, ArrayList<BayesServer> values) {
		super(context, R.layout.rowlayout, values);
		this.context = context;
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.rowlayout, parent, false);
		TextView textURL = (TextView) rowView.findViewById(R.id.title);
		TextView textUser = (TextView) rowView.findViewById(R.id.subtitle);
		TextView textAddress = (TextView) rowView.findViewById(R.id.rightcorner);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
		textURL.setText(values.get(position).getURL());
		textUser.setText(values.get(position).getName());
		textAddress.setText("");
		// Change the icon for Windows and iPhone
		BayesServer m = values.get(position);
		if(m.equals(MainActivity.bayesServer))
			imageView.setImageResource(R.drawable.network);
		else
			imageView.setImageResource(R.drawable.tools);
		return rowView;
	}
}
