package waylay.client.data;

import org.afree.data.general.DefaultPieDataset;

public class BackupInfo {
	private Long ok;
	private Long delayed;
	private Long warning;
	private Long errors;
	private Long configured;
	private Long notActive;
	private Long unconfigured;
	public BackupInfo(Long ok, Long delayed, Long warning, Long errors,
			Long configured, Long notActive, Long unconfigured) {
		super();
		this.ok = ok;
		this.delayed = delayed;
		this.warning = warning;
		this.errors = errors;
		this.configured = configured;
		this.notActive = notActive;
		this.unconfigured = unconfigured;
	}
	public BackupInfo() {
		// TODO Auto-generated constructor stub
	}
	public Long getOk() {
		return ok;
	}
	public Long getDelayed() {
		return delayed;
	}
	public Long getWarning() {
		return warning;
	}
	public Long getErrors() {
		return errors;
	}
	public Long getConfigured() {
		return configured;
	}
	public Long getNotActive() {
		return notActive;
	}
	public Long getUnconfigured() {
		return unconfigured;
	}

	public DefaultPieDataset createDataset() {
		DefaultPieDataset dataset = new DefaultPieDataset();
		dataset.setValue("Backup OK", getOk());
		dataset.setValue("Backup Delayed",getDelayed());
		dataset.setValue("Warning", getWarning());
		dataset.setValue("Errors", getErrors());
		dataset.setValue("Configured", getConfigured());
		dataset.setValue("Not Active", getNotActive());
		dataset.setValue("Not Configured", getUnconfigured());

		return dataset;

	}
	public void setValues(String state, Long value) {
		if("OK".equals(state)) {
			ok = value;
		} else if("Backup Delayed".equals(state)) {
			delayed = value;
		} else if("Warning".equals(state)) {
			warning = value;
		} else if("Errors".equals(state)) {
			errors = value;
		} else if("Configured".equals(state)) {
			configured = value;
		}  else if("Not Active".equals(state)) {
			notActive = value;
		} else if("Unconfigured".equals(state)) {
			unconfigured = value;
		} 

	}

}
