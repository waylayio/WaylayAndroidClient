package waylay.rest;

import java.util.List;

import retrofit.Callback;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import waylay.client.scenario.Scenario;

public interface WaylayRestApi {

    @GET("/scenarios")
    void listScenarios(Callback<List<Scenario>> cb);

    // TODO add support for filters?
    @GET("/scenarios/{scenarioId}")
    void getScenario(@Path("scenarioId") Long scenarioId, Callback<Scenario> cb);

    @DELETE("/scenarios/{scenarioId}")
    void deleteScenario(@Path("scenarioId") Long scenarioId, Callback<Void> cb);

    @POST("/scenarios/{scenarioId}")
    @FormUrlEncoded
    void performScenarioAction(@Path("scenarioId") Long scenarioId, @Field("action") String action, Callback<Void> cb);

    @POST("/scenarios/{scenarioId}")
    @FormUrlEncoded
    void setScenarioProperty(
            @Path("scenarioId") Long scenarioId,
            @Field("runtime_property") String property,
            @Field("value") String value,
            Callback<Void> cb);

    @POST("/scenarios/{scenarioId}/{node}")
    @FormUrlEncoded
    void setScenarioNodeProperty(
            @Path("scenarioId") Long scenarioId,
            @Path("node") String node,
            @Field("runtime_property") String property,
            @Field("value") String value,
            Callback<Void> cb);

}