package waylay.rest;

import java.util.ArrayList;

import org.json.JSONException;

import waylay.client.data.MachineInfo;
import waylay.client.data.BayesServer;
import waylay.client.scenario.RemoteScenarioStatus;

import android.util.Log;


/**
 * Entry point into the API.
 */
public class RestAPI{   
	protected static final String TAG = "SSO REST API";

	private static RestAPI sso = null;

	static {
		sso = new RestAPI();
	}

	public static RestAPI getInstance() {
		return sso;
	}

	private RestAPI(){}


	/**
	 * Request a Scenarios from the REST server.
	 * @param callback Callback to execute when the profile is available.
	 */
	public void getMachines(String restUrl, String name, String filter, String password, final GetResponseCallback callback){

		String call1 = restUrl + filter;
		Log.d(TAG, "getMachines with url "+ call1);

		new GetTask(call1, name, password, new RestTaskCallback (){
			@Override
			public void onTaskComplete(String response){
				//parse response
				Log.i(TAG, response);
				boolean error = false;
				String message = null;
				ArrayList<Machine> machines = new ArrayList<Machine>();

				if(GetTask.NO_RESULT.equals(response)){
					error = true;
					message = GetTask.getError();
				} else {

					try {
						machines = Machine.getSSOMachinefromJSON(response);
					} catch (JSONException e) {
						error = true;
						message = e.getMessage();
						Log.e(TAG, message);
					}
				}
				callback.onDataReceived(machines, error, message);
			}
		}).execute("");

	}

	
	/**
	 * Request a Scenarios from the REST server.
	 * @param callback Callback to execute when the profile is available.
	 */
	public void getScenarios(String restUrl, String name, String filter, String password, final GetResponseCallback callback){

		String call1 = restUrl + filter;
		Log.d(TAG, "getScenarios with url "+ call1);

		new GetTask(call1, name, password, new RestTaskCallback (){
			@Override
			public void onTaskComplete(String response){
				//parse response
				Log.i(TAG, response);
				boolean error = false;
				String message = null;
				ArrayList<RemoteScenarioStatus> scenarios = new ArrayList<RemoteScenarioStatus>() ;

				if(GetTask.NO_RESULT.equals(response)){
					error = true;
					message = GetTask.getError();
				} else {

					try {
						scenarios = ScenarioUtil.getAllScenariosViaJSON(response);
					} catch (Exception e) {
						error = true;
						message = e.getMessage();
						Log.e(TAG, message);
					}
				}
				callback.onDataReceived(scenarios, error, message);
			}
		}).execute("");

	}

	/**
	 * Request a SSOMachines from the REST server.
	 * @param callback Callback to execute when the profile is available.
	 */
	public void getIPAddressesForMachine(final MachineInfo machine, String restUrl, String name, String password, final GetResponseCallback callback){

		String call1 = restUrl + machine.getGuid();
		Log.d(TAG, "getIPAddressesForMachine for machine "+ machine.getName()); 
		new GetTask(call1, name, password, new RestTaskCallback (){
			@Override
			public void onTaskComplete(String response){
				Log.i(TAG, response);
				boolean error = false;
				String message = null;

				if(GetTask.NO_RESULT.equals(response)){
					error = true;
					message = GetTask.getError();
				} else {
					try {
						machine.setIpAddress(Machine.getIPfromJSON(response));
					} catch (JSONException e) {
						error = true;
						message = e.getMessage();
						Log.e(TAG, message);
					}
				}
				callback.onUpdate(error, message);
			}
		}).execute("");
	}


	public void getDashboardData(String restUrl, String name, String password, final Dashboard dashboard, final GetResponseCallback callback){

		String call1 = restUrl;
		Log.d(TAG, "getDashboardData with url "+ call1);

		new GetTask(call1, name, password, new RestTaskCallback (){
			@Override
			public void onTaskComplete(String response){
				//parse response
				Log.i(TAG, response);
				boolean error = false;
				String message = null;
				if(GetTask.NO_RESULT.equals(response)){
					error = true;
					message = GetTask.getError();
				} else {
					try {
						dashboard.parseString(response);
					} catch (JSONException e) {
						error = true;
						message = e.getMessage();
						Log.e(TAG, message);
					}
				}
				callback.onDashboardReceived(dashboard, error, message);
			}
		}).execute("");

	}

	/**
	 * Submit call to to the server.
	 * @param callback The callback to execute when submission status is available.
	 */
	public void postUserProfile(String restUrl, String requestBody, ArrayList<Machine> machines, final PostResponseCallback callback){
		new PostTask(restUrl, requestBody, new RestTaskCallback(){
			public void onTaskComplete(String response){
				callback.onPostSuccess();
			}
		});
	}
}


