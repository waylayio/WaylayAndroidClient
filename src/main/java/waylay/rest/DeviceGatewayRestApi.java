package waylay.rest;

import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import waylay.client.scenario.Task;

public interface DeviceGatewayRestApi {
    @POST("/godkeys/{masterkey}")
    public void createDevice(@Path("masterkey") String masterkey, Callback<Map<String, String>> callback);
}
