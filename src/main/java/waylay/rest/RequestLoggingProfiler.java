package waylay.rest;

import android.util.Log;

import retrofit.Profiler;

public class RequestLoggingProfiler implements Profiler<Void> {

    private final String tag;

    public RequestLoggingProfiler(final String tag) {
        this.tag = tag;
    }

    @Override
    public Void beforeCall() {
        return null;
    }

    @Override
    public void afterCall(RequestInformation requestInformation, long elapsedTime, int statusCode, Void beforeCallData) {
        Log.i(tag, requestInformation.getMethod() + " " + requestInformation.getRelativePath() + " -> " + statusCode + " " + elapsedTime + " ms");
    }
}
