package waylay.rest;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/*
 * 
 *  "result":
   [
       {
          ....
 */

/*
 * {
      "status": "IMAGEONLY", 
      "nrcpu": 1, 
      "machinetype": "VIRTUALDESKTOP", 
      "name": "MC_Base_Template_XP_32", 
      "hypervisor": "VIRTUALBOX", 
      "hostname": null, 
      "bootstatus": "FROMDISK", 
      "system": false, 
      "replicationstatus": null, 
      "importancefactor": 5, 
      "os": "windowsxp", 
      "template": true, 
      "memory": 10, 
      "machinerole": null, 
      "agentguid": null, 
      "isbackup": false, 
      "guid": "66a1d76e-2714-4888-a2eb-e2a589e53fc7", 
      "backup": false, 
      "replicationrole": null, 
      "description": "template_MC_Base_Template_XP_32"
    }, 
 */
public class Machine {
	private static final String TAG = null;
	private String status;
	private Long nrcpu; 
	private String machinetype ; 
	private String name; 
	private String hypervisor; 
	private String hostname; 
	private String bootstatus;
	private String system;
	private String replicationstatus;
	private Long importancefactor; 
	private String os; 
	private Boolean template; 
	private Long memory; 
	private String machinerole;
	private Long agentguid;
	private Boolean isbackup; 
	private String guid;
	private Boolean backup; 
	private String replicationrole; 
	private String description;

	// added ip address, not part of the JSON
	private String ipAddress;

	public Machine(String name, String hostname, String status, String machineType, String guid) {
		super();
		this.status = status;
		this.name = name;
		this.hostname = hostname;
		this.machinetype = machineType;
		this.guid = guid;
	}

	public String getStatus() {
		return status;
	}

	public String getName() {
		return name;
	}

	public String getHostname() {
		return hostname;
	}

	public String getGuid() {
		return guid;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	

	@Override
	public String toString() {
		return "SSOMachine [status=" + status + ", nrcpu=" + nrcpu
				+ ", machinetype=" + machinetype + ", name=" + name
				+ ", hypervisor=" + hypervisor + ", hostname=" + hostname
				+ ", bootstatus=" + bootstatus + ", system=" + system
				+ ", replicationstatus=" + replicationstatus
				+ ", importancefactor=" + importancefactor + ", os=" + os
				+ ", template=" + template + ", memory=" + memory
				+ ", machinerole=" + machinerole + ", agentguid=" + agentguid
				+ ", isbackup=" + isbackup + ", guid=" + guid + ", backup="
				+ backup + ", replicationrole=" + replicationrole
				+ ", description=" + description + ", ipAddress=" + ipAddress
				+ "]";
	}

	public static ArrayList<Machine> getSSOMachinefromJSON(String jsonString) throws JSONException{

		ArrayList<Machine> ssoMachines = new ArrayList<Machine>();

		JSONObject jObject;
		jObject = new JSONObject(jsonString);

		JSONArray machines = jObject.getJSONArray("result");

		for (int i = 0; i < machines.length(); i++) {
			String hostname = machines.getJSONObject(i).getString("hostname").toString();
			String name = machines.getJSONObject(i).getString("name").toString();
			String machineType = machines.getJSONObject(i).getString("machinetype").toString();
			String guid = machines.getJSONObject(i).getString("guid").toString();
			String status = machines.getJSONObject(i).getString("status").toString();
			Machine sso = new Machine(name, hostname, status, machineType, guid);
			Log.d(TAG, sso.toString());
			ssoMachines.add(sso);	
		}


		return ssoMachines;
	}

	/*
	 * 
{
  "jobguid": "66b477f7-1371-4ea8-a4c3-763b777971ef",
  "result": "10.101.107.254"
}

	 */

	public static String getIPfromJSON(String jsonString) throws JSONException{
		JSONObject jObject = new JSONObject(jsonString);
		String address = jObject.getString("result").toString();
		return address;
	}


}


