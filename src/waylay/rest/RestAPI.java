package waylay.rest;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

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
				List<Scenario> scenarios = new ArrayList<Scenario>() ;

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


    private String apiBase() {
        return  "http://" + server.getHost() + "/api";
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

}


