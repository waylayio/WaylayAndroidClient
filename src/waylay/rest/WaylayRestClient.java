package waylay.rest;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.android.AndroidLog;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import waylay.client.data.BayesServer;
import waylay.client.scenario.Scenario;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


/**
 * Entry point into the API.
 */
public class WaylayRestClient {
    protected static final String TAG = WaylayRestClient.class.getSimpleName();

	private final BayesServer server;
    private final WaylayRestApi service;

    public WaylayRestClient(final BayesServer server) {
        this.server = server;
        this.service = createRestClient(server);
    }

    public void postScenarioAction(Long scenarioId, String action, final PostResponseCallback<Void> callback){
        service.performScenarioAction(scenarioId, action, new RetrofitPostResponseCallback<Void>(callback));
	}

    public void postScenarioNodeValueAction(Long scenarioId, String node, String property, String value, final PostResponseCallback<Void> callback){
        if(node == null){
            service.setScenarioProperty(scenarioId, property, value, new RetrofitPostResponseCallback<Void>(callback));
        }else{
            service.setScenarioNodeProperty(scenarioId, node, property, value, new RetrofitPostResponseCallback<Void>(callback));
        }
    }
	
	public void deleteScenarioAction(final long scenarioId, final DeleteResponseCallback callback){
        service.deleteScenario(scenarioId, new RetrofitDeleteResponseCallback<Void>(callback));
	}
	
	/**
	 * Request a Scenarios from the REST servers.
	 * @param callback Callback to execute when the profile is available.
	 */
	public void getScenarios(final GetResponseCallback<List<Scenario>> callback){
        service.listScenarios(new RetrofitGetResponseCallback<List<Scenario>>(callback));
	}
	
	public void getScenario(final long scenarioId, final GetResponseCallback<Scenario> callback){
        service.getScenario(scenarioId, new RetrofitGetResponseCallback<Scenario>(callback));
	}

    private WaylayRestApi createRestClient(BayesServer server) {
        Gson gson = new GsonBuilder()
                //.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Scenario.class, new ScenarioAdapter())
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(apiBase())
                .setRequestInterceptor(new BasicAuthorizationInterceptor(server.getName(), server.getPassword()))
                .setProfiler(new RequestLoggingProfiler(TAG))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setLog(new AndroidLog(TAG))
                .setConverter(new GsonConverter(gson))
                .build();
        return restAdapter.create(WaylayRestApi.class);
    }

    private String apiBase() {
        return  "http://" + server.getHost() + "/api";
    }

    private static class RetrofitGetResponseCallback <T> implements Callback<T> {
        private final GetResponseCallback<T> callback;

        public RetrofitGetResponseCallback(GetResponseCallback<T> callback) {
            this.callback = callback;
        }

        @Override
        public void success(T value, Response response) {
            callback.onDataReceived(value, false, null);
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            Log.e(TAG, retrofitError.getMessage(), retrofitError);
            callback.onDataReceived(null, true, retrofitError.getResponse().getStatus() + " - " + retrofitError.getMessage());
        }
    }

    private static class RetrofitPostResponseCallback <T> implements Callback<T> {
        private final PostResponseCallback<T> callback;

        public RetrofitPostResponseCallback(PostResponseCallback<T> callback) {
            this.callback = callback;
        }

        @Override
        public void success(T value, Response response) {
            callback.onPostSuccess();
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            Log.e(TAG, retrofitError.getMessage(), retrofitError);
            //callback.onDataReceived(null, true, retrofitError.getMessage());
        }
    }

    private static class RetrofitDeleteResponseCallback <T> implements Callback<T> {
        private final DeleteResponseCallback callback;

        public RetrofitDeleteResponseCallback(DeleteResponseCallback callback) {
            this.callback = callback;
        }

        @Override
        public void success(T value, Response response) {
            callback.onDeleteSuccess();
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            Log.e(TAG, retrofitError.getMessage(), retrofitError);
            callback.onDeleteFailure();
        }
    }

}

