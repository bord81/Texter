package labut.md311.texter.network;

import android.net.Uri;
import android.os.AsyncTask;

import java.io.IOException;
import java.lang.ref.WeakReference;

import labut.md311.texter.view.LoginActivity;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AuthRequestPhase extends AsyncTask<Object, Void, Boolean> {
    private WeakReference<LoginActivity> loginActivity;
    private String tw_cons_key = null;
    private String tw_priv_cons_key = null;
    private String oauthToken = null;
    private String oauthTokenSecret = null;
    private final String postOauth2TokenUrlReal = "https://api.twitter.com/oauth/request_token";
    private final String getOauth2Authorize = "https://api.twitter.com/oauth/authorize";
    private final String callbackUrl = "http://11texterwi11.org/callback";
    private final String httpMethod = "POST";
    private final String sigMethod = "HMAC-SHA1";
    private final String oauthVer = "1.0";
    private final OkHttpClient client = new OkHttpClient();
    private String callToTwitter = null;
    private Uri uri = null;

    @Override
    protected Boolean doInBackground(Object... params) {
        //checking call parameters
        if (params[0] != null && params[1] != null && params[2] != null) {
            tw_cons_key = (String) params[0];
            tw_priv_cons_key = (String) params[1];
            loginActivity = (WeakReference<LoginActivity>) params[2];
        } else {
            return false;
        }
        //making request for tokens
        OAuthForm authForm = new OAuthForm(OAuthForm.GET_REQUEST_TOKENS, postOauth2TokenUrlReal, httpMethod, sigMethod, oauthVer, tw_cons_key, tw_priv_cons_key, null, null, callbackUrl, null);
        Request requestOAUTHToken = authForm.getHttpRequest();
        if (requestOAUTHToken == null) {
            return false;
        }
        try {
            Response response = client.newCall(requestOAUTHToken).execute();
            if (response.isSuccessful()) {
                String respBody = response.body().string();
                String ot = "oauth_token=";
                String ots = "&oauth_token_secret=";
                String cbc = "&oauth_callback_confirmed=";
                oauthToken = respBody.substring(respBody.indexOf(ot) + ot.length(), respBody.indexOf(ots));
                oauthTokenSecret = respBody.substring(respBody.indexOf(ots) + ots.length(), respBody.indexOf(cbc));
                callToTwitter = getOauth2Authorize + "?oauth_token=" + oauthToken;
                uri = Uri.parse(callToTwitter);
            } else {
                System.out.println("authHeader Resp code is: " + response.code());
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        //redirecting user to Twitter sign in
        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if (aBoolean) {
            loginActivity.get().toTwitter(aBoolean, uri, oauthToken, oauthTokenSecret);
        } else {
            loginActivity.get().toTwitter(aBoolean, null, null, null);
        }
    }
}
