package waylay.client.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class StorageUsage {
	private Date date;
	private Long value;
	SimpleDateFormat format =
	            new SimpleDateFormat("MM-dd");
	public StorageUsage(String date, Long value) {
		super();
		try {
			this.date = format.parse(date);
			//this.date.setYear(Calendar.getInstance().get(Calendar.YEAR));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		this.value = value;
	}
	public Date getDate() {
		return date;
	}
	public Long getValue() {
		return value;
	}
	
	public int getDay(){
		return date.getDay();
	}
	
	public int getMonth(){
		return date.getMonth();
	}
	@Override
	public String toString() {
		return "StorageUsage [date=" + date + ", value=" + value + "]";
	}
	
	
	

}
