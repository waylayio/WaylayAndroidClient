package waylay.rest.auth;

import android.util.Base64;
import android.util.Log;

import retrofit.RequestInterceptor;

public class BasicAuthorizationInterceptor implements RequestInterceptor {
    private final String authorizationValue;

    public BasicAuthorizationInterceptor(final String username, final String password) {
        this.authorizationValue = encodeCredentialsForBasicAuthorization(username, password);
    }

    @Override
    public void intercept(final RequestFacade request) {
        request.addHeader("Authorization", authorizationValue);
    }

    private String encodeCredentialsForBasicAuthorization(final String username, final String password) {
        final String userAndPassword = username + ":" + password;
        return "Basic " + Base64.encodeToString(userAndPassword.getBytes(), Base64.NO_WRAP);
    }
}
