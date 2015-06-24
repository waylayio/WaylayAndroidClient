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

public interface WaylayRestApi {

    @GET("/tasks")
    void listTasks(Callback<List<Task>> cb);

    // TODO add support for filters?
    @GET("/tasks/{taskId}")
    void getTask(@Path("taskId") Long taskId, Callback<Task> cb);

    @DELETE("/tasks/{taskId}")
    void deleteTask(@Path("taskId") Long taskId, Callback<Void> cb);

    @POST("/tasks/{taskId}/command/{action}")
    void performTaskAction(@Path("taskId") Long taskId, @Path("action") String action, Callback<Void> cb);

    @POST("/resources/{resourceId}")
    void postRawData(@Path("resourceId") String resourceId, @Body Map<String, Object> data, Callback<Void> cb);
}