package controllers;

/**
 * Created by rizrusn on 12/04/16.
 */

import org.apache.commons.codec.binary.Base64;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

public class BasicAuthenticator extends Security.Authenticator
{
    private static final String AUTHORIZATION = "authorization";
    private static final String WWW_AUTHENTICATE = "WWW-Authenticate";
    private static final String REALM = "Basic realm=\"APIRealm\"";

    @Override
    public String getUsername(Http.Context ctx) {
        try {
            String authHeader = ctx.request().getHeader(AUTHORIZATION);
            if (authHeader != null) {
                ctx.response().setHeader(WWW_AUTHENTICATE,REALM);
                String auth = authHeader.substring(6);
                byte[] decodedAuth = Base64.decodeBase64(auth);
                String[] credentials = new String(decodedAuth,"UTF-8").split(":");
                if (credentials != null && credentials.length == 2) {
                    String username = credentials[0];
                    String password = credentials[1];
                    if (isAuthenticated(username, password)) {
                        return username;
                    } else {
                        return null;
                    }
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    private boolean isAuthenticated(String username, String password) {
        return username != null && username.equals("rizrusn") &&password != null && password.equals("password");
    }
    @Override
    public Result onUnauthorized(Http.Context context) {
        return unauthorized();
    }
}
