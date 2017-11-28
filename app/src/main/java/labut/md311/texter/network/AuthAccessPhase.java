package labut.md311.texter.network;

import android.net.Uri;
import android.os.AsyncTask;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.regex.Pattern;

import labut.md311.texter.view.LoginActivity;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AuthAccessPhase extends AsyncTask<Object, Void, Boolean> {
    private WeakReference<LoginActivity> loginActivity;
    private String tw_cons_key = null;
    private String tw_priv_cons_key = null;
    private Uri cbUri = null;
    private String oauthRToken = null;
    private String oauthRTokenSecret = null;
    private String oauthAToken = null;
    private String oauthATokenSecret = null;
    private final static String postOauth2TokenUrlReal = "https://api.twitter.com/oauth/access_token";
    private final String httpMethod = "POST";
    private final static String sigMethod = "HMAC-SHA1";
    private final static String oauthVer = "1.0";
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected Boolean doInBackground(Object... params) {
        //checking call parameters
        if (params[0] != null && params[1] != null && params[2] != null && params[3] != null && params[4] != null && params[5] != null) {
            cbUri = (Uri) params[0];
            tw_cons_key = (String) params[1];
            tw_priv_cons_key = (String) params[2];
            oauthRToken = (String) params[3];
            oauthRTokenSecret = (String) params[4];
            loginActivity = (WeakReference<LoginActivity>) params[5];
        } else {
            return false;
        }

        //making request for access tokens
        if (!oauthRToken.equals(cbUri.getQueryParameter("oauth_token"))) {
            return false;
        }

        OAuthForm authForm = new OAuthForm(OAuthForm.GET_ACCESS_TOKENS, postOauth2TokenUrlReal, httpMethod, sigMethod, oauthVer, tw_cons_key, tw_priv_cons_key, oauthRToken, oauthRTokenSecret, null, cbUri.getQueryParameter("oauth_verifier"));
        Request requestOAUTHToken = authForm.getHttpRequest();
        if (requestOAUTHToken == null) {
            return false;
        }
        try {
            Response response = client.newCall(requestOAUTHToken).execute();
            if (response.isSuccessful()) {
                String respBody = response.body().string();
                String ot = "oauth_token";
                String ots = "oauth_token_secret";
                String[] split = respBody.split(Pattern.quote("="));
                for (int i = 0; i < split.length; i++) {
                    if (split[i].contains(ot) && !split[i].contains(ots)) {
                        int endIndex = split[i + 1].indexOf("&");
                        if (endIndex < 0)
                            endIndex = split[i + 1].length();
                        oauthAToken = split[i + 1].substring(0, endIndex);
                    } else if (split[i].contains(ots)) {
                        int endIndex = split[i + 1].indexOf("&");
                        if (endIndex < 0)
                            endIndex = split[i + 1].length();
                        oauthATokenSecret = split[i + 1].substring(0, endIndex);
                    }
                }
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if (aBoolean) {
            loginActivity.get().onAuthResult(aBoolean, oauthAToken, oauthATokenSecret);
        } else {
            loginActivity.get().onAuthResult(aBoolean, null, null);
        }
    }
}