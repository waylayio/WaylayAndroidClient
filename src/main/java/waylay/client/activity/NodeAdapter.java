package waylay.client.activity;

import java.util.ArrayList;

import retrofit.http.POST;
import waylay.client.scenario.Node;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import waylay.client.R;

public class NodeAdapter extends ArrayAdapter<Node> {
	
	private final Context context;
	private final ArrayList<Node> values;
    private final String targetNode;

	public NodeAdapter(Context context, ArrayList<Node> values, String tagetNode) {
		super(context, R.layout.rowlayout, values);
		this.context = context;
		this.values = values;
        this.targetNode = tagetNode;
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
		Node node = values.get(position);
		//Map<String, Double> nodeStates = node.getStates();
		String name = node.getName(); 
		textTitle.setText(name);
		textStatus.setText(node.getStatesAsString());
		textID.setText(node.getMostLikelyState());
		if (node.getName().equals(targetNode)) {
			imageView.setImageResource(R.drawable.trigger);
		} else {
            int resourceID = 0;
            if(node.getSensorName() != null){
                resourceID = context.getResources().getIdentifier(node.getSensorName().toLowerCase(), "drawable", context.getPackageName());
            }
			if(resourceID == 0){
                resourceID = R.drawable.sensor;
            }
            imageView.setImageResource(resourceID);
		}

		return convertView;
	}
}
