package waylay.rest.xively;

import retrofit.RequestInterceptor;

/**
 * https://xively.com/dev/docs/api/security/keys/
 */
public class XivelyAuthenticationInterceptor implements RequestInterceptor {

    private final String apiKey;

    public XivelyAuthenticationInterceptor(final String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public void intercept(RequestFacade request) {
        request.addHeader("X-ApiKey", apiKey);
    }
}
