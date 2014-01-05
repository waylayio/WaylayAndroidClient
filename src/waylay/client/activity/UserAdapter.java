package waylay.client.activity;

import java.util.ArrayList;

import waylay.client.data.UserInfo;

import com.waylay.client.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class UserAdapter extends ArrayAdapter<UserInfo> {
	
	private final Context context;
	private final ArrayList<UserInfo> values;

	public UserAdapter(Context context, ArrayList<UserInfo> values) {
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
		textStatus.setText(values.get(position).getMachine().getName());
		textAddress.setText(values.get(position).getConnectionType().toString());
		UserInfo m = values.get(position);
		imageView.setImageResource(R.drawable.user);
		return rowView;
	}
}