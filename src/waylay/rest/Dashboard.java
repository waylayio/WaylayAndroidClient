package waylay.rest;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import waylay.client.data.BackupInfo;
import waylay.client.data.ReplicationInfo;
import waylay.client.data.ResourceUsage;
import waylay.client.data.StorageUsage;


/*
 * keeps reponse of the call http://user:password@server/appserver/rest/cloud_api_cmc/getDashboard
 * DTO like objects which come out of this call will be in the data package
 * {
  "jobguid": null,
  "result": {
    "storageusage": [
      {
        "date": "06-23",
        "value": 7
      },
      {
        "date": "06-22",
        "value": 6
      },
      {
        "date": "06-21",
        "value": 5
      },
      {
        "date": "06-20",
        "value": 4
      },
      {
        "date": "06-19",
        "value": 3
      },
      {
        "date": "06-18",
        "value": 0
      },
      {
        "date": "06-26",
        "value": 8
      },
      {
        "date": "06-25",
        "value": 9
      },
      {
        "date": "06-24",
        "value": 8
      }
    ],
    "resourceusage": {
      "pausedsandbox": 0,
      "stoppedsandbox": 0,
      "cpulive": 11,
      "ramlive": 19327352832,
      "stoppedlive": 0,
      "ramsandbox": 0,
      "connectedtargets": 18,
      "unallocatedcpu": 1,
      "unconnectedtargets": 2,
      "startedsandbox": 0,
      "pausedlive": 0,
      "startedlive": 11,
      "unallocatedram": 49354375168,
      "cpusandbox": 0
    },
    "alerts": [
      {
        "healscript": null,
        "toHeal": 0,
        "description": "Unable to build up SMTP connection for email notifications",
        "weight": 5,
        "timestamp": "2012-06-18 08:10:38",
        "component": "management",
        "indexValue": 50,
        "subcomponent": "remotesupport",
        "guid": "af94a302-f777-4f1a-bfb9-aa131b1360bc",
        "typeID": "MR001",
        "severity": "WARNING",
        "sequencenr": 0
      }
    ],
    "replication": [
      {
        "state": "OK",
        "description": "Volumes with status OK/Syncing",
        "value": 0
      },
      {
        "state": "Broken",
        "description": "Volumes with status Degraded",
        "value": 0
      },
      {
        "state": "Not Configured",
        "description": "Volumes with status Standalone",
        "value": 0
      },
      {
        "state": "Unknown",
        "description": "Unknown",
        "value": 100
      }
    ],
    "healthstatus": 0,
    "localbackups": [
      {
        "state": "OK",
        "description": "Configured device with consistent snapshot earlier than 1 day and non-zero snapshot earlier than 4 hours",
        "value": 0
      },
      {
        "state": "Backup Delayed",
        "description": "Configured device with consistent snapshot earlier than 1 day but non-zero snapshot older than 4 hours",
        "value": 0
      },
      {
        "state": "Warning",
        "description": "Configured device with consistent snapshot between 1 and 7 days old",
        "value": 0
      },
      {
        "state": "Errors",
        "description": "Configured device with consistent snapshot older than 7 days",
        "value": 0
      },
      {
        "state": "Configured",
        "description": "Configured device with no consistent snapshot",
        "value": 100
      },
      {
        "state": "Not Active",
        "description": "Configured device with inactive connection",
        "value": 0
      },
      {
        "state": "Unconfigured",
        "description": "Device not configured",
        "value": 0
      }
    ],
    "pendingupdates": {
      "STORAGEMinor": 0,
      "OTHERMinor": 0,
      "mandatory": false,
      "MCAGENTMinor": 0,
      "MCAGENTCritical": 0,
      "OTHERCritical": 0,
      "CPUMinor": 0,
      "CPUMajor": 0,
      "STORAGECritical": 0,
      "CPUCritical": 0,
      "OTHERMajor": 0,
      "MCAGENTMajor": 0,
      "STORAGEMajor": 0
    }
  }
}
 * 
 */
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
