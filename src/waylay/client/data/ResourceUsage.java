package waylay.client.data;

import org.afree.data.category.CategoryDataset;
import org.afree.data.category.DefaultCategoryDataset;

public class ResourceUsage {
	private Long pausedsandbox;
	private Long stoppedsandbox;
	private Long cpulive;
	private Double ramlive;
	private Long stoppedlive;
	private Long ramsandbox;
	private Long connectedtargets;
	private Long unallocatedcpu;
	private Long unconnectedtargets;
	private Long startedsandbox;
	private Long pausedlive;
	private Long startedlive;
	private Double unallocatedram;
	private Long cpusandbox;

	//TODO you should really do a builder pattern here
	public ResourceUsage(Long pausedsandbox, Long stoppedsandbox, Long cpulive,
			Double ramlive, Long stoppedlive, Long ramsandbox,
			Long connectedtargets, Long unallocatedcpu,
			Long unconnectedtargets, Long startedsandbox, Long pausedlive,
			Long startedlive, Double unallocatedram, Long cpusandbox) {
		super();
		this.pausedsandbox = pausedsandbox;
		this.stoppedsandbox = stoppedsandbox;
		this.cpulive = cpulive;
		this.ramlive = ramlive;
		this.stoppedlive = stoppedlive;
		this.ramsandbox = ramsandbox;
		this.connectedtargets = connectedtargets;
		this.unallocatedcpu = unallocatedcpu;
		this.unconnectedtargets = unconnectedtargets;
		this.startedsandbox = startedsandbox;
		this.pausedlive = pausedlive;
		this.startedlive = startedlive;
		this.unallocatedram = unallocatedram;
		this.cpusandbox = cpusandbox;
	}


	public Long getPausedsandbox() {
		return pausedsandbox;
	}

	public Long getStoppedsandbox() {
		return stoppedsandbox;
	}

	public Long getCpulive() {
		return cpulive;
	}

	public Double getRamlive() {
		return ramlive;
	}

	public Long getStoppedlive() {
		return stoppedlive;
	}

	public Long getRamsandbox() {
		return ramsandbox;
	}

	public Long getConnectedtargets() {
		return connectedtargets;
	}

	public Long getUnallocatedcpu() {
		return unallocatedcpu;
	}

	public Long getUnconnectedtargets() {
		return unconnectedtargets;
	}

	public Long getStartedsandbox() {
		return startedsandbox;
	}

	public Long getPausedlive() {
		return pausedlive;
	}

	public Long getStartedlive() {
		return startedlive;
	}

	public Double getUnallocatedram() {
		return unallocatedram;
	}

	public Long getCpusandbox() {
		return cpusandbox;
	}


	public CategoryDataset createDataset() {

		// row keys...
		String series1 = "Live";

		// column keys...
		String category1 = "Started";
		String category2 = "Paused";
		String category3 = "Stopped";
		String category4 = "ConnectedTargets";

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();


		dataset.addValue(getStartedlive(), series1, category1);
		dataset.addValue(getPausedlive(), series1, category2);
		dataset.addValue(getStoppedlive(), series1, category3);
		dataset.addValue(getConnectedtargets(), series1, category4);


		// row keys...
		String series2 = "Sandbox";

		// column keys...
		String category11 = "Started";
		String category22 = "Paused";
		String category33 = "Stopped";


		dataset.addValue(getStartedsandbox(), series2, category11);
		dataset.addValue(getPausedsandbox(), series2, category22);
		dataset.addValue(getStoppedsandbox(), series2, category33);


		// row keys...
		String series3 = "Targets";

		// column keys...
		String category44 = "Started";
		String category55 = "Paused";

		dataset.addValue(getConnectedtargets(), series3, category44);
		dataset.addValue(getUnconnectedtargets(), series3, category55);


		return dataset;

	}


	public CategoryDataset createCPURamDataset() {

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		// row keys...

		String series1 = "RAM Usage";
		String series2 = "CPU Usage";

		// column keys...
		String category1 = "Allocated RAM Live";
		String category2 = "Allocated RAM Sandbox";
		String category3 = "Unallocated RAM";
		
		String category4 = "CPU Live";
		String category5 = "CPU Sandbox";
		String category6 = "Unallocated CPU";

		dataset.addValue(getRamlive()/1000000000, series1, category1);
		dataset.addValue(getRamsandbox()/1000000000, series1, category2);
		dataset.addValue(getUnallocatedram()/1000000000, series1, category3);
		dataset.addValue(getCpulive(), series2, category4);
		dataset.addValue(getCpusandbox(), series2, category5);
		dataset.addValue(getUnallocatedcpu(), series2, category6);

		return dataset;

	}

}
