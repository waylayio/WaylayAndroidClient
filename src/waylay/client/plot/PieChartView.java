package waylay.client.plot;


import org.afree.chart.ChartFactory;
import org.afree.chart.AFreeChart;
import org.afree.chart.plot.PiePlot;
import org.afree.chart.title.TextTitle;
import org.afree.data.general.DefaultPieDataset;
import org.afree.data.general.PieDataset;
import org.afree.graphics.geom.Font;

import android.content.Context;
import android.graphics.Typeface;

/**
 * PieChartDemo01View
 */
public class PieChartView extends DemoView {

    private String title;
    
    public PieChartView(Context context, String title) {
        super(context);
        this.title = title;
    }

    public void createDataset(DefaultPieDataset dataset) {
    	AFreeChart chart = createChart(dataset);
        setChart(chart);
    }

    private AFreeChart createChart(PieDataset dataset) {

        AFreeChart chart = ChartFactory.createPieChart(
        		title, // chart title
                dataset, // data
                true, // include legend
                true,
                false);
        TextTitle title = chart.getTitle();
        title.setToolTipText("");

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setLabelFont(new Font("SansSerif", Typeface.NORMAL, 12));
        plot.setNoDataMessage("No data available");
        plot.setCircular(false);
        plot.setLabelGap(0.02);
        return chart;

    }
}