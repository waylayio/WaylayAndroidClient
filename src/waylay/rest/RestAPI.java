package waylay.rest;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import waylay.client.data.MachineInfo;
import waylay.client.data.BayesServer;
import waylay.client.scenario.Scenario;

import android.util.Log;


/**
 * Entry point into the API.
 */
public class RestAPI{   
	protected static final String TAG = "RestAPI";

	private final BayesServer server;

    public RestAPI(BayesServer server) {
        this.server = server;
    }

    /**
	 * Request a Scenarios from the REST servers.
	 * @param callback Callback to execute when the profile is available.
	 */
	public void getMachines(String filter, final GetResponseCallback callback){

		String url = constructURLtoListAllMachines() + filter;
		Log.d(TAG, "getMachines with url "+ url);

		new GetTask(url, server.getName(), server.getPassword(), new RestTaskCallback (){
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

	public void postScenarioAction(Long scenarioId, String action, final PostResponseCallback callback){
        String url = constructURLtoForScenario(scenarioId);
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
        nameValuePairs.add(new BasicNameValuePair("action", action));
		new PostTask(url, nameValuePairs, new RestTaskCallback(){
			public void onTaskComplete(String response){
				callback.onPostSuccess();
			}
		}).execute("");
	}

    public void postScenarioNodeValueAction(Long scenarioId, String node, String property, String value, final PostResponseCallback callback){
        String url = constructURLtoForScenarioAndNode(scenarioId, node);
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
        nameValuePairs.add(new BasicNameValuePair("runtime_property", property ));
        nameValuePairs.add(new BasicNameValuePair("value", value ));
        new PostTask(url, nameValuePairs, new RestTaskCallback(){
            public void onTaskComplete(String response){
                callback.onPostSuccess();
            }
        }).execute("");
    }

    public void postScenarioNodeAction(Long scenarioId, String node, String action, final PostResponseCallback callback){
        String url = constructURLtoForScenarioAndNode(scenarioId, node);
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
        nameValuePairs.add(new BasicNameValuePair("action", action));
        new PostTask(url, nameValuePairs, new RestTaskCallback(){
            public void onTaskComplete(String response){
                callback.onPostSuccess();
            }
        }).execute("");
    }
	
	public void deleteScenarioAction(long scenarioId, final PostResponseCallback callback){
        String url = constructURLtoForScenario(scenarioId);
		new DeleteTask(url, new RestTaskCallback(){
			public void onTaskComplete(String response){
				callback.onPostSuccess();
			}
		}).execute("");
	}
	
	/**
	 * Request a Scenarios from the REST servers.
	 * @param callback Callback to execute when the profile is available.
	 */
	public void getScenarios(String filter, final GetResponseCallback<List<Scenario>> callback){
		String url = constructURLtoListAllScenarios() + filter;
		Log.d(TAG, "getScenarios with url " + url);

		new GetTask(url, server.getName(), server.getPassword(), new RestTaskCallback (){
			@Override
			public void onTaskComplete(String response){
				//parse response
				Log.i(TAG, response);
				boolean error = false;
				String message = null;
				ArrayList<Scenario> scenarios = new ArrayList<Scenario>() ;

				if(GetTask.NO_RESULT.equals(response)){
					error = true;
					message = GetTask.getError();
				} else {

					try {
						scenarios = ScenarioJSONParser.getAllScenariosViaJSON(response);
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
	
	public void getScenario(long scenarioId, String filter, final GetResponseCallback<Scenario> callback){
		String url = constructURLtoForScenario(scenarioId) + filter;
		Log.d(TAG, "getScenarios with url "+ url);

		new GetTask(url, server.getName(), server.getPassword(), new RestTaskCallback (){
			@Override
			public void onTaskComplete(String response){
				//parse response
				Log.i(TAG, response);
				boolean error = false;
				String message = null;

                Scenario scenario = null;
				if(GetTask.NO_RESULT.equals(response)){
					error = true;
					message = GetTask.getError();
				} else {
					try {
                        scenario = ScenarioJSONParser.parseScenario(response);
					} catch (Exception e) {
						error = true;
						message = e.getMessage();
						Log.e(TAG, message);
					}
				}
				callback.onDataReceived(scenario, error, message);
			}
		}).execute("");

	}

	/**
	 * Request a SSOMachines from the REST servers.
	 * @param callback Callback to execute when the profile is available.
	 */
	public void getIPAddressesForMachine(final MachineInfo machine, String restUrl, String name, String password, final GetResponseCallback<Void> callback){

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
				callback.onDataReceived(null, error, message);
			}
		}).execute("");
	}


//	public void getDashboardData(final Dashboard dashboard, final GetResponseCallback<Dashboard> callback){
//        final String url = constructURLtoGetDashboardData();
//		Log.d(TAG, "getDashboardData with url "+ url);
//        // TODO why the name-pass both in url and task?
//		new GetTask(url, server.getName(), server.getPassword(), new RestTaskCallback (){
//			@Override
//			public void onTaskComplete(String response){
//				//parse response
//				Log.i(TAG, response);
//				boolean error = false;
//				String message = null;
//				if(GetTask.NO_RESULT.equals(response)){
//					error = true;
//					message = GetTask.getError();
//				} else {
//					try {
//						dashboard.parseString(response);
//					} catch (JSONException e) {
//						error = true;
//						message = e.getMessage();
//						Log.e(TAG, message);
//					}
//				}
//				callback.onDataReceived(dashboard, error, message);
//			}
//		}).execute("");
//
//	}

	/**
	 * Submit call to to the servers.
	 * @param callback The callback to execute when submission status is available.
	 */
	private void postUserProfile(String restUrl, String requestBody, ArrayList<Machine> machines, final PostResponseCallback callback){

	}

    private String apiBase() {
        return  "http://" + server.getHost() + "/api";
    }

    private String apiBaseAuthenticated() {
        return  "http://" + server.getName() + ":" + server.getPassword() + "@" + server.getHost() + "/api";
    }

    private String constructURLtoListAllMachines(){
        return apiBaseAuthenticated() + "/appserver/rest/cloud_api_machine/list";

    }

    private String constructURLtoListAllScenarios(){
        return apiBase() + "/scenarios";
    }

    private String constructURLtoForScenario(Long id){
        return apiBase() + "/scenarios/" + id;
    }

    private String constructURLtoForScenarioAndNode(Long id, String node){
        return constructURLtoForScenario(id) + node;
    }

//    private String constructURLtoGetIpAddress(){
//        return apiBaseAuthenticated() + "/appserver/rest/cloud_api_machine/getPublicIpaddress?machineguid=";
//
//    }
//
//    private String constructURLtoGetDashboardData(){
//        return apiBaseAuthenticated() + "/appserver/rest/cloud_api_cmc/getDashboard";
//
//    }

}


