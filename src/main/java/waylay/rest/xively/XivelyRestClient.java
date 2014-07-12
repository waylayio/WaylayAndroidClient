package waylay.rest.xively;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.HttpStatus;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.android.AndroidLog;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import waylay.client.scenario.Task;
import waylay.rest.RequestLoggingProfiler;
import waylay.rest.ScenarioAdapter;

public class XivelyRestClient {

    protected static final String TAG = XivelyRestClient.class.getSimpleName();

    private static final String API_BASE = "https://api.xively.com/";

    private final XivelyRestApi restClient;

    public XivelyRestClient(final String apiKey) {
        this.restClient = createRestClient(apiKey);
    }

    public void makeSureDeviceExists(final String productId, final String serial, Callback<Void> callback){

        // TODO we clearly need something (RxJava?) to get this callback hell cleaner
        // https://github.com/Netflix/RxJava/wiki

        // on the other hand we could store everything in the preferences and once registered not have
        // to do the check on startup?

        restClient.getDevice(productId, serial, new Callback<DeviceResponse>() {
            @Override
            public void success(DeviceResponse deviceResponse, Response response) {
                Log.i(TAG, "Device exists");
            }

            @Override
            public void failure(RetrofitError error) {
                if(error.getResponse().getStatus() == HttpStatus.SC_NOT_FOUND){
                    Devices devices = new Devices();
                    devices.devices.add(new Serial(serial));
                    restClient.createDevice(productId, devices, new Callback<Void>() {
                        @Override
                        public void success(Void aVoid, Response response) {
                            Log.i(TAG, "Device created");
                            restClient.listDevices(productId, null, null, null, null, new Callback<DeviceList>() {
                                @Override
                                public void success(DeviceList devices, Response response) {
                                    Log.i(TAG, devices.toString());
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    Log.e(TAG, error.getMessage(), error);
                                }
                            });
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.e(TAG, ((XivelyError)error.getBodyAs(XivelyError.class)).errors, error);
                        }
                    });
                }else {
                    Log.e(TAG, ((XivelyError)error.getBodyAs(XivelyError.class)).errors, error);
                }
            }
        });
    }

    private XivelyRestApi createRestClient(final String apiKey) {
        Gson gson = new GsonBuilder()
                //.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Task.class, new ScenarioAdapter())
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(API_BASE)
                .setRequestInterceptor(new XivelyAuthenticationInterceptor(apiKey))
                .setProfiler(new RequestLoggingProfiler(TAG))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setLog(new AndroidLog(TAG))
                .setConverter(new GsonConverter(gson))
                .build();
        return restAdapter.create(XivelyRestApi.class);
    }
}
