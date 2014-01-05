package waylay.client.data;

import org.afree.data.general.DefaultPieDataset;

public class DiskStats {
	private Long occupiedSpace;
	private Long totalSpace;

	public DiskStats(Long occupiedSpace, Long totalSpace) {
		super();
		this.occupiedSpace = occupiedSpace;
		this.totalSpace = totalSpace;
	}
	public Long getOccupiedSpace() {
		return occupiedSpace;
	}
	public Long getTotalSpace() {
		return totalSpace;
	}


	public DefaultPieDataset createDataset() {

		DefaultPieDataset dataset = new DefaultPieDataset();
		dataset.setValue("Used", getOccupiedSpace());
		dataset.setValue("Remaining", getTotalSpace() - getOccupiedSpace());

		return dataset;
	}

}
