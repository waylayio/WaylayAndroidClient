package waylay.client.data;

import org.afree.data.general.DefaultPieDataset;

public class ReplicationInfo {
	private Long ok;
	private Long broken;
	private Long notConfigured;
	private Long unknown;
	public ReplicationInfo(Long ok, Long broken, Long notConfigured,
			Long unknown) {
		super();
		this.ok = ok;
		this.broken = broken;
		this.notConfigured = notConfigured;
		this.unknown = unknown;
	}
	public ReplicationInfo() {

	}
	public Long getOk() {
		return ok;
	}
	public Long getBroken() {
		return broken;
	}
	public Long getNotConfigured() {
		return notConfigured;
	}
	public Long getUnknown() {
		return unknown;
	}
	public void setValues(String state, Long value) {
		if("OK".equals(state)) {
			ok = value;
		} else if("Broken".equals(state)) {
			broken = value;
		} else if("Not Configured".equals(state)) {
			notConfigured = value;
		} else if("Unknown".equals(state)) {
			unknown = value;
		} 

	}
	public DefaultPieDataset createDataset() {
		DefaultPieDataset dataset = new DefaultPieDataset();
		dataset.setValue("OK", getOk());
		dataset.setValue("Broken",getBroken());
		dataset.setValue("Not Configured", getNotConfigured());
		dataset.setValue("Unknown", getUnknown());

		return dataset;

	}
}	

