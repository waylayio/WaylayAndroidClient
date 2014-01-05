package waylay.client.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.afree.data.time.Day;
import org.afree.data.time.TimeSeries;
import org.afree.data.time.TimeSeriesCollection;
import org.afree.data.xy.XYDataset;

import android.util.Log;

public class DataSetFactoryUtil {

	private static final String TAG = "DataSetFactoryUtil";

	public static XYDataset createStorageUsageDataset( ArrayList<StorageUsage> storageUsage) {
		TimeSeries s1 = new TimeSeries("Storage usage data");
		try{
			for(StorageUsage st: storageUsage){
				Log.d(TAG, st.toString());
				s1.add(new Day(st.getDate()), st.getValue());
				//s1.add(new Day(st.getDay(), st.getMonth(),Calendar.getInstance().get(Calendar.YEAR)), st.getValue());
			} 
			} catch (Exception e){
				Log.e(TAG, e.getMessage());
			}

		TimeSeriesCollection dataset = new TimeSeriesCollection();
		dataset.addSeries(s1);
		return dataset;

	}

}
