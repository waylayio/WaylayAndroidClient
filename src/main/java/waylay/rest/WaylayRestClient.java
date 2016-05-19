package waylay.rest;

import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.android.AndroidLog;
import retrofit.client.Client;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import waylay.client.data.BayesServer;
import waylay.client.scenario.Task;
import waylay.rest.auth.BasicAuthorizationInterceptor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;


/**
 * Entry point into the API.
 */
public class WaylayRestClient {
    protected static final String TAG = WaylayRestClient.class.getSimpleName();

	private final BayesServer server;
    private final WaylayRestApi service;
    private final WaylayRestApi dataService;
    private final DeviceGatewayRestApi deviceGatewayRestApi;

    public WaylayRestClient(final BayesServer server) {
        this.server = server;
        this.service = createRestClient(server);
        this.dataService = createBridgeRestClient(server);
        this.deviceGatewayRestApi = createDeviceGatewayClient(server);
    }

    private DeviceGatewayRestApi createDeviceGatewayClient(BayesServer server) {
        Gson gson = new GsonBuilder()
                //.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Map.class, new DeviceAdapter())
                .create();

        OkHttpClient client = new OkHttpClient();
        // If you have connection problems like "stream was reset: CANCEL" you can try
        // to disable SPDY for debugging:
        // client.setProtocols(Arrays.asList(Protocol.HTTP_1_1));
        Client restClient = new OkClient(client);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(server.devicesApiBase())
                .setRequestInterceptor(new BasicAuthorizationInterceptor(server.getName(), server.getPassword()))
                .setProfiler(new RequestLoggingProfiler(TAG))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setLog(new AndroidLog(TAG))
                .setConverter(new GsonConverter(gson))
                .setClient(restClient)
                .build();
        return restAdapter.create(DeviceGatewayRestApi.class);
    }

    public void postScenarioAction(Long scenarioId, String action, final PostResponseCallback<Void> callback){
        service.performTaskAction(scenarioId, action, new RetrofitPostResponseCallback<Void>(callback));
	}

    public void postResourceValue(String resource, Map<String, Object> sensorData, final PostResponseCallback<Void> callback){
        dataService.postRawData(resource, sensorData, new RetrofitPostResponseCallback<Void>(callback));
    }
	
	public void deleteScenarioAction(final long scenarioId, final DeleteResponseCallback callback){
        service.deleteTask(scenarioId, new RetrofitDeleteResponseCallback<Void>(callback));
	}

    public void createDevice(String godkey, final PostResponseCallback<Map<String, String>> callback) {
        deviceGatewayRestApi.createDevice(godkey, new RetrofitPostResponseCallback<Map<String, String>>(callback));
    }
	
	/**
	 * Request a Scenarios from the REST servers.
	 * @param callback Callback to execute when the profile is available.
	 */
	public void getScenarios(final GetResponseCallback<List<Task>> callback){
        service.listTasks(new RetrofitGetResponseCallback<List<Task>>(callback));
	}
	
	public void getScenario(final long scenarioId, final GetResponseCallback<Task> callback){
        service.getTask(scenarioId, new RetrofitGetResponseCallback<Task>(callback));
	}

    private WaylayRestApi createRestClient(BayesServer server) {
        Gson gson = new GsonBuilder()
                //.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Task.class, new ScenarioAdapter())
                .create();

        OkHttpClient client = new OkHttpClient();
        // If you have connection problems like "stream was reset: CANCEL" you can try
        // to disable SPDY for debugging:
        // client.setProtocols(Arrays.asList(Protocol.HTTP_1_1));
        Client restClient = new OkClient(client);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(server.apiBase())
                .setRequestInterceptor(new BasicAuthorizationInterceptor(server.getName(), server.getPassword()))
                .setProfiler(new RequestLoggingProfiler(TAG))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setLog(new AndroidLog(TAG))
                .setConverter(new GsonConverter(gson))
                .setClient(restClient)
                .build();
        return restAdapter.create(WaylayRestApi.class);
    }

    private WaylayRestApi createBridgeRestClient(BayesServer server) {
        Gson gson = new GsonBuilder()
                //.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Task.class, new ScenarioAdapter())
                .create();

        OkHttpClient client = new OkHttpClient();
        // If you have connection problems like "stream was reset: CANCEL" you can try
        // to disable SPDY for debugging:
        // client.setProtocols(Arrays.asList(Protocol.HTTP_1_1));
        Client restClient = new OkClient(client);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(server.brokerApiBase())
                .setRequestInterceptor(new BasicAuthorizationInterceptor(server.getName(), server.getPassword()))
                .setProfiler(new RequestLoggingProfiler(TAG))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setLog(new AndroidLog(TAG))
                .setConverter(new GsonConverter(gson))
                .setClient(restClient)
                .build();
        return restAdapter.create(WaylayRestApi.class);
    }

    /**
     * Makes sure unexpected errors crash the app
     * https://github.com/square/retrofit/issues/560
     * @param retrofitError the retrofit error
     */
    private static void failOnUnexpectedError(RetrofitError retrofitError) {
        // see RetrofitError.unexpectedError() for reasoning
        if(!retrofitError.isNetworkError() && retrofitError.getResponse() == null){
            throw new RuntimeException(retrofitError.getMessage(), retrofitError);
        }
    }

    private static class RetrofitGetResponseCallback <T> implements Callback<T> {
        private final GetResponseCallback<T> callback;

        public RetrofitGetResponseCallback(GetResponseCallback<T> callback) {
            this.callback = callback;
        }

        @Override
        public void success(T value, Response response) {
            callback.onDataReceived(value);
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            failOnUnexpectedError(retrofitError);
            callback.onError(retrofitError);
        }
    }

    private static class RetrofitPostResponseCallback <T> implements Callback<T> {
        private final PostResponseCallback<T> callback;

        public RetrofitPostResponseCallback(PostResponseCallback<T> callback) {
            this.callback = callback;
        }

        @Override
        public void success(T value, Response response) {
            callback.onPostSuccess(value);
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            failOnUnexpectedError(retrofitError);
            callback.onPostFailure(retrofitError);
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
            failOnUnexpectedError(retrofitError);
            callback.onDeleteFailure(retrofitError);
        }
    }

}


