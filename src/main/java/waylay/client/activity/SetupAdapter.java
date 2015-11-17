package waylay.client.activity;

import java.util.List;

import waylay.client.WaylayApplication;
import waylay.client.data.BayesServer;

import waylay.client.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SetupAdapter extends ArrayAdapter<BayesServer> {

    private final WaylayApplication waylayApplication;
	private final List<BayesServer> values;

	public SetupAdapter(Context context, WaylayApplication waylayApplication, List<BayesServer> values) {
		super(context, R.layout.rowlayout, values);
        this.waylayApplication = waylayApplication;
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.rowlayout, parent, false);
		TextView textURL = (TextView) rowView.findViewById(R.id.title);
		TextView textUser = (TextView) rowView.findViewById(R.id.subtitle);
		TextView textAddress = (TextView) rowView.findViewById(R.id.rightcorner);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
		textURL.setText(values.get(position).getHost());
		textUser.setText(values.get(position).getName());
		textAddress.setText("");
		// Change the icon for Windows and iPhone
		BayesServer m = values.get(position);
		if(m.equals(waylayApplication.getSelectedServer())) {
            imageView.setImageResource(R.drawable.network);
        }else {
            imageView.setImageResource(R.drawable.tools);
        }
		return rowView;
	}
}
