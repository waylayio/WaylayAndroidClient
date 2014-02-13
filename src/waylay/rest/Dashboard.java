package waylay.rest;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import waylay.client.data.BackupInfo;
import waylay.client.data.ReplicationInfo;
import waylay.client.data.ResourceUsage;
import waylay.client.data.StorageUsage;

public class Dashboard {
	ArrayList <StorageUsage> storageUsage = new ArrayList <StorageUsage> ();
	ResourceUsage resourceUsage;
	ReplicationInfo replicationInfo = new ReplicationInfo();
	BackupInfo backupInfo;

	public void parseString(String jsonString) throws JSONException{

		JSONObject jObject = new JSONObject(jsonString);
		JSONObject resultObject = jObject.getJSONObject("result");
		
		JSONArray storageusage = resultObject.getJSONArray("storageusage");

		for (int i = 0; i < storageusage.length(); i++) {
			storageUsage.add(new StorageUsage(storageusage.getJSONObject(i).get("date").toString(),
					Long.parseLong(storageusage.getJSONObject(i).get("value").toString())));

		}
		JSONObject resource = resultObject.getJSONObject("resourceusage");
		resourceUsage = new ResourceUsage(Long.parseLong(resource.get("pausedsandbox").toString()), 
				Long.parseLong(resource.get("stoppedsandbox").toString()),  
				Long.parseLong(resource.get("cpulive").toString()), 
				Double.parseDouble(resource.get("ramlive").toString()),
				Long.parseLong(resource.get("stoppedlive").toString()), 
				Long.parseLong(resource.get("ramsandbox").toString()),
				Long.parseLong(resource.get("connectedtargets").toString()), 
				Long.parseLong(resource.get("unallocatedcpu").toString()),
				Long.parseLong(resource.get("unconnectedtargets").toString()),
				Long.parseLong(resource.get("startedsandbox").toString()),
				Long.parseLong(resource.get("pausedlive").toString()),
				Long.parseLong(resource.get("startedlive").toString()),
				Double.parseDouble(resource.get("unallocatedram").toString()),
				Long.parseLong(resource.get("cpusandbox").toString()));

		JSONArray backup = resultObject.getJSONArray("localbackups");
		backupInfo = new BackupInfo();
		for (int i = 0; i < backup.length(); i++) {
			String state = backup.getJSONObject(i).get("state").toString();
			Long value = Long.parseLong(backup.getJSONObject(i).get("value").toString());
			backupInfo.setValues(state,value);
			
		}
		

		JSONArray replication = resultObject.getJSONArray("replication");
		
		for (int i = 0; i < replication.length(); i++) {
			String state = replication.getJSONObject(i).get("state").toString();
			Long value = Long.parseLong(replication.getJSONObject(i).get("value").toString());
			replicationInfo.setValues(state,value);
			
		}
	}

	public ArrayList<StorageUsage> getStorageUsage() {
		return storageUsage;
	}

	public ResourceUsage getResourceUsage() {
		return resourceUsage;
	}

	public ReplicationInfo getReplicationInfo() {
		return replicationInfo;
	}

	public BackupInfo getBackupInfo() {
		return backupInfo;
	}

	public boolean isValid() {
		return (resourceUsage != null) && (backupInfo !=null);
	}

}
