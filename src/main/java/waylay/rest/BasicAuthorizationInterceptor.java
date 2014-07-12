package waylay.rest;

import android.util.Base64;

import retrofit.RequestInterceptor;

class BasicAuthorizationInterceptor implements RequestInterceptor {
    private final String username;
    private final String password;

    public BasicAuthorizationInterceptor(final String username, final String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public void intercept(final RequestFacade request) {
        final String authorizationValue = encodeCredentialsForBasicAuthorization();
        request.addHeader("Authorization", authorizationValue);
        //request.addHeader("User-Agent", "Retrofit-Sample-App");
    }

    private String encodeCredentialsForBasicAuthorization() {
        final String userAndPassword = username + ":" + password;
        final int flags = 0;
        return "Basic " + Base64.encodeToString(userAndPassword.getBytes(), flags);
    }
}
