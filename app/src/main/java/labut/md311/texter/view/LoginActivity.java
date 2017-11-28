package labut.md311.texter.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.lang.ref.WeakReference;

import labut.md311.texter.BuildConfig;
import labut.md311.texter.R;
import labut.md311.texter.network.AuthAccessPhase;
import labut.md311.texter.network.AuthRequestPhase;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String tw_cons_key = BuildConfig.TwitterConsumerKey;
    private final static String tw_priv_cons_key = BuildConfig.TwitterConsumerSecretKey;
    private TwitterLoginButton twitterLoginButton;
    protected static final String TWITTER_LOGIN = "t_login";
    protected static final String TWITTER_LOGIN_NAME = "t_login_name";
    protected static final String TWITTER_LOGIN_ID = "t_login_id";
    protected static final String TWITTER_LOGIN_TOKEN = "t_login_token";
    protected static final String TWITTER_LOGIN_SECRET = "t_login_secret";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar loginToolbar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(loginToolbar);
        getSupportActionBar().setTitle("TexterQ login");
        twitterLoginButton = (TwitterLoginButton) findViewById(R.id.login_button);
        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                SharedPreferences.Editor editor = getSharedPreferences(TWITTER_LOGIN, MODE_PRIVATE).edit();
                editor.putString(TWITTER_LOGIN_NAME, result.data.getUserName());
                editor.putString(TWITTER_LOGIN_ID, String.valueOf(result.data.getUserId()));
                editor.putString(TWITTER_LOGIN_TOKEN, result.data.getAuthToken().token);
                editor.putString(TWITTER_LOGIN_SECRET, result.data.getAuthToken().secret);
                editor.commit();
                launchTweetFeed();
            }

            @Override
            public void failure(TwitterException exception) {
                Snackbar.make(findViewById(R.id.constr_login), "Couldn't get authentication from Twitter", Snackbar.LENGTH_LONG).show();
            }
        });

        if (getSharedPreferences(TWITTER_LOGIN, MODE_PRIVATE).contains(TWITTER_LOGIN_TOKEN) && getSharedPreferences(TWITTER_LOGIN, MODE_PRIVATE).contains(TWITTER_LOGIN_SECRET)) {
            launchTweetFeed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        twitterLoginButton.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!getIntent().getAction().equals(Intent.ACTION_MAIN)) {
            AuthAccessPhase accessPhase = new AuthAccessPhase();
            String oauthToken = getSharedPreferences("oauthFirst", MODE_PRIVATE).getString("oauthToken", null);
            String oauthTokenSecret = getSharedPreferences("oauthFirst", MODE_PRIVATE).getString("oauthTokenSecret", null);
            if (oauthToken != null && oauthTokenSecret != null) {
                accessPhase.execute(getIntent().getData(), tw_cons_key, tw_priv_cons_key, oauthToken, oauthTokenSecret, new WeakReference(this));
                getSharedPreferences("oauthFirst", MODE_PRIVATE).edit().clear().apply();
            }
        }
    }

    public void toTwitter(boolean success, Uri uri, String oauthToken, String oauthTokenSecret) {
        if (success) {
            getSharedPreferences("oauthFirst", MODE_PRIVATE).edit().putString("oauthToken", oauthToken).apply();
            getSharedPreferences("oauthFirst", MODE_PRIVATE).edit().putString("oauthTokenSecret", oauthTokenSecret).apply();
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), "Couldn't get authorization tokens, please retry", Toast.LENGTH_LONG).show();
        }
    }


    public void onAuthResult(boolean success, String oauthToken, String oauthTokenSecret) {
        if (success) {
            getSharedPreferences(TWITTER_LOGIN, MODE_PRIVATE).edit().putString(TWITTER_LOGIN_TOKEN, oauthToken).commit();
            getSharedPreferences(TWITTER_LOGIN, MODE_PRIVATE).edit().putString(TWITTER_LOGIN_SECRET, oauthTokenSecret).commit();
            launchTweetFeed();
        } else {
            Toast.makeText(getApplicationContext(), "Couldn't get authorization from Twitter, please retry", Toast.LENGTH_LONG).show();
        }
    }


    private void launchAuth() {
        AuthRequestPhase requestPhase = new AuthRequestPhase();
        requestPhase.execute(tw_cons_key, tw_priv_cons_key, new WeakReference(this));
    }

    protected void launchTweetFeed() {
        Intent intent = new Intent(getApplicationContext(), TweetFeedAct.class);
        startActivity(intent);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                launchAuth();
                break;
        }
    }
}
