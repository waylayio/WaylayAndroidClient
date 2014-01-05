package waylay.client.plot;

import java.text.SimpleDateFormat;

import org.afree.chart.ChartFactory;
import org.afree.chart.AFreeChart;
import org.afree.chart.axis.DateAxis;
import org.afree.chart.axis.NumberAxis;
import org.afree.chart.plot.XYPlot;
import org.afree.chart.renderer.xy.XYItemRenderer;
import org.afree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.afree.data.xy.XYDataset;
import org.afree.graphics.SolidColor;
import org.afree.ui.RectangleInsets;

import android.content.Context;
import android.graphics.Color;

public class TimeSeriesView  extends DemoView {
	private String title;
	private String xLabel;
	private String yLabel;
	private boolean percentage = true;

	public TimeSeriesView(Context context, String title, String xLabel, String ylabel) {
		super(context);
		this.title = title;
		this.xLabel = xLabel;
		this.yLabel = ylabel;
	}
	
	public TimeSeriesView(Context context, String title, String xLabel, String ylabel, boolean percentage) {
		super(context);
		this.title = title;
		this.xLabel = xLabel;
		this.yLabel = ylabel;
		this.percentage = percentage;
	}



	public void createDataSet(XYDataset dataset){
		AFreeChart chart = createChart(dataset);
		setChart(chart);
	}

	private AFreeChart createChart(XYDataset dataset) {

		AFreeChart chart = ChartFactory.createTimeSeriesChart(
				title,  // title
				xLabel,             // x-axis label
				yLabel,   // y-axis label
				dataset,            // data
				true,               // create legend?
				true,               // generate tooltips?
				false               // generate URLs?
				);

		chart.setBackgroundPaintType(new SolidColor(Color.WHITE));

		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaintType(new SolidColor(Color.LTGRAY));
		plot.setDomainGridlinePaintType(new SolidColor(Color.WHITE));
		plot.setRangeGridlinePaintType(new SolidColor(Color.WHITE));
		plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);

		if(percentage ){
			NumberAxis rangeAxis = (NumberAxis)plot.getRangeAxis();
			rangeAxis.setRange(0,100);
		
		}

		XYItemRenderer r = plot.getRenderer();
		if (r instanceof XYLineAndShapeRenderer) {
			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
			renderer.setBaseShapesVisible(true);
			renderer.setBaseShapesFilled(true);
			renderer.setDrawSeriesLineAsPath(true);
		}

		DateAxis axis = (DateAxis) plot.getDomainAxis();
		axis.setDateFormatOverride(new SimpleDateFormat("MM-dd"));

		return chart;

	}
}