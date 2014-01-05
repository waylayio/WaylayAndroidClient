package waylay.client.activity;


import java.util.HashMap;

import waylay.client.data.DataSetFactoryUtil;
import waylay.client.plot.BarPlotView;
import waylay.client.plot.PieChartView;
import waylay.client.plot.TimeSeriesView;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ListView;

public class PlotAdapter extends BaseAdapter {
	private Context mContext;
	private HashMap<Integer, View> mapOfViews = new HashMap<Integer, View>();

	public PlotAdapter(Context context){
		mContext = context;
	}

	@Override
	public int getCount() {
		return 5;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View plotView = null;
		WindowManager mWinMgr = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
		int displayWidth = mWinMgr.getDefaultDisplay().getWidth() - 8;
		int displayHeight = mWinMgr.getDefaultDisplay().getHeight() - 120;
		int displayRowSize = Math.min(displayHeight, displayWidth);
		
		//something crazy is happening with position and the view, so I use the set instead of checking for the null on the view
		if(mapOfViews.get(position) == null) {
			if(position == 1){
				plotView = new BarPlotView(mContext, "Machine states");
				((BarPlotView) plotView).createDataSet(MainActivity.resourceUsage.createDataset());
				plotView.setLayoutParams(new ListView.LayoutParams(displayRowSize, displayRowSize));
				plotView.setPadding(8, 8, 8, 8);
			} else if(position == 3){
				plotView = new PieChartView(mContext, "Backup Info");
				((PieChartView) plotView).createDataset(MainActivity.backupInfo.createDataset());
				plotView.setLayoutParams(new ListView.LayoutParams(displayRowSize, (int) (0.8 * displayRowSize)));
				plotView.setPadding(8, 8, 8, 8);

			} else if(position == 4){
				plotView = new PieChartView(mContext, "Replication Info");
				((PieChartView) plotView).createDataset(MainActivity.replicationInfo.createDataset());
				plotView.setLayoutParams(new ListView.LayoutParams(displayRowSize, (int) (0.8 * displayRowSize)));
				plotView.setPadding(8, 8, 8, 8);

			} else if(position == 0){
				plotView = new TimeSeriesView(mContext, "Storage Usage", "Date", "Usage [%]");
				((TimeSeriesView) plotView).createDataSet(DataSetFactoryUtil.createStorageUsageDataset(MainActivity.storageUsage));
				plotView.setLayoutParams(new ListView.LayoutParams(displayRowSize, displayRowSize));
				plotView.setPadding(8, 8, 8, 8);
			} else if(position == 2){
				plotView = new BarPlotView(mContext, "CPU and RAM Usage");
				((BarPlotView) plotView).createDataSet(MainActivity.resourceUsage.createCPURamDataset());
				plotView.setLayoutParams(new ListView.LayoutParams(displayRowSize, displayRowSize));
				plotView.setPadding(8, 8, 8, 8);
			}
			mapOfViews.put(position, plotView);
		}		
		else {
			plotView = mapOfViews.get(position);
		}

		return plotView;
	}



}
