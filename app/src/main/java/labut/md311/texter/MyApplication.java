package labut.md311.texter;

import android.app.Application;
import android.content.Context;

import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;

public class MyApplication extends Application {
    private final static String tw_cons_key = BuildConfig.TwitterConsumerKey;
    private final static String tw_priv_cons_key = BuildConfig.TwitterConsumerSecretKey;

    @Override
    public void onCreate() {
        super.onCreate();
        final Context context = this;
        TwitterConfig config = new TwitterConfig.Builder(this)
                .twitterAuthConfig(new TwitterAuthConfig(tw_cons_key, tw_priv_cons_key))
                .build();
        Twitter.initialize(config);
    }
}
