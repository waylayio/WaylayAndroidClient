package waylay.client.statistcs;

import java.util.LinkedList;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class StdDev
{
	private LinkedList<Double> list = new LinkedList<Double>();
	private double stdDev;
	private DescriptiveStatistics stats = new DescriptiveStatistics();
	private static int SAMPLE_WINDOW = 50;

	/**
	 * Add a sample to the rolling window.
	 * 
	 * @param value
	 *            The sample value.
	 * @return The variance of the rolling window.
	 */
	public double addSample(double value)
	{
		list.addLast(value);

		enforceWindow();

		return calculateStdDev();
	}

	/**
	 * Enforce the rolling window.
	 */
	private void enforceWindow()
	{
		if (list.size() > SAMPLE_WINDOW)
		{
			list.removeFirst();
		}
	}

	/**
	 * Calculate the variance of the rolling window.
	 * @return The variance of the rolling window.
	 */
	private double calculateStdDev()
	{
		if (list.size() > SAMPLE_WINDOW)
		{		
			stats.clear();
			
			// Add the data from the array
			for (int i = 0; i < list.size(); i++)
			{
				stats.addValue(list.get(i));
			}

			stdDev = stats.getStandardDeviation();
		}

		return stdDev;
	}
}
