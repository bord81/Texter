package labut.md311.texter.view;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.regex.Pattern;

import labut.md311.texter.R;
import labut.md311.texter.network.ConnectivityChecker;
import labut.md311.texter.presenter.TweetViewSimplePresenter;


public class TweetViewAct extends AppCompatActivity {

    private ImageView avatar = null;
    private TextView retweeted = null;
    private TextView header = null;
    private TextView header_at = null;
    private TextView created_at = null;
    private TextView body = null;
    private TextView retwCount = null;
    private TextView likesCount = null;
    private ImageView retwIcon = null;
    private ImageView likesIcon = null;
    private static final String RETW_BY = "Retweeted by ";
    public static final String TWEET_PARCEL = "tp";
    public static TweetParcel tweetParcel;

    TweetViewSimplePresenter tweetViewPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_view);
        Toolbar singleTweetToolbar = (Toolbar) findViewById(R.id.st_toolbar);
        setSupportActionBar(singleTweetToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        initView();
        tweetViewPresenter = new TweetViewSimplePresenter(new WeakReference<TweetViewAct>(this));
    }

    private void initView() {
        avatar = (ImageView) findViewById(R.id.st_img_avatar);
        retweeted = (TextView) findViewById(R.id.st_text_view_retweeted);
        header = (TextView) findViewById(R.id.st_text_view_header);
        header_at = (TextView) findViewById(R.id.st_text_view_header_at);
        created_at = (TextView) findViewById(R.id.st_text_view_created_at);
        body = (TextView) findViewById(R.id.st_text_view_body);
        retwCount = (TextView) findViewById(R.id.st_text_view_retweets_count);
        likesCount = (TextView) findViewById(R.id.st_text_view_likes_count);
        retwIcon = (ImageView) findViewById(R.id.st_img_retweets);
        likesIcon = (ImageView) findViewById(R.id.st_img_likes);
        Intent intent = getIntent();
        tweetParcel = (TweetParcel) intent.getParcelableExtra(TWEET_PARCEL);
        body.setOnClickListener(v -> showLink());
        retwIcon.setOnClickListener(v -> ifRetweet());
        retwCount.setOnClickListener(v -> ifRetweet());
        likesIcon.setOnClickListener(v -> ifLike());
        likesCount.setOnClickListener(v -> ifLike());
        Picasso.with(getApplicationContext()).load(tweetParcel.profile_image_url_https).into(avatar);
        if (tweetParcel.retweeted_by.length() > 0) {
            String retwBy = RETW_BY + tweetParcel.retweeted_by;
            retweeted.setText(retwBy);
            retweeted.setVisibility(View.VISIBLE);
        }
        header.setText(tweetParcel.name);
        String at = "@" + tweetParcel.screen_name;
        String date = tweetParcel.created_at.substring(4, 10);
        header_at.setText(at);
        created_at.setText(date);
        body.setText(tweetParcel.text);
        retwCount.setText(String.valueOf(tweetParcel.retweet_count));
        likesCount.setText(String.valueOf(tweetParcel.favorite_count));
        updateIcons();
    }

    protected void ifRetweet() {
        if (tweetParcel.retweeted > 0) {
            tweetViewPresenter.deleteRetweet(tweetParcel.position);
        } else {
            tweetViewPresenter.postRetweet(tweetParcel.position);
        }
    }

    protected void ifLike() {
        if (tweetParcel.favorited > 0) {
            tweetViewPresenter.removeLike(tweetParcel.position);
        } else {
            tweetViewPresenter.setLike(tweetParcel.position);
        }
    }

    protected void showLink() {
        if (tweetParcel.text.contains("http")) {
            String[] with_link = tweetParcel.text.split(Pattern.quote("https"));
            String url = new String();
            for (int i = 0; i < with_link.length; i++) {
                if (with_link[i].startsWith("://")) {
                    int end = with_link[i].indexOf(' ');
                    if (end < 0)
                        end = with_link[i].length();
                    url = with_link[i].substring(0, end);
                    break;
                }
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https" + url));
            startActivity(intent);
        }
    }

    private void updateIcons() {
        if (tweetParcel.retweeted > 0) {
            retwIcon.setColorFilter(Color.BLACK);
        } else {
            retwIcon.clearColorFilter();
        }
        if (tweetParcel.favorited > 0) {
            likesIcon.setColorFilter(Color.BLACK);
        } else {
            likesIcon.clearColorFilter();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateViewOnUserEvent(int type) {
        switch (type) {
            case TweetViewSimplePresenter.RETWEET_POST:
                tweetParcel.retweeted = 1;
                retwCount.setText(String.valueOf(++tweetParcel.retweet_count));
                break;
            case TweetViewSimplePresenter.RETWEET_DELETE:
                tweetParcel.retweeted = 0;
                retwCount.setText(String.valueOf(--tweetParcel.retweet_count));
                break;
            case TweetViewSimplePresenter.LIKE_POST:
                tweetParcel.favorited = 1;
                likesCount.setText(String.valueOf(++tweetParcel.favorite_count));
                break;
            case TweetViewSimplePresenter.LIKE_DELETE:
                tweetParcel.favorited = 0;
                likesCount.setText(String.valueOf(--tweetParcel.favorite_count));
                break;
        }
        updateIcons();
    }

    public void notifyUserOnNetworkError(int status) {
        switch (status) {
            case 403:
                showLimitSnackbar();
            case 429:
                showLimitSnackbar();
                break;
        }
    }

    private void showLimitSnackbar() {
        Snackbar.make(findViewById(R.id.constr_tweet_view), "Call limit to Twitter reached, try again later", Snackbar.LENGTH_LONG).show();
    }

    private void showConnectionMissing() {
        Toast.makeText(getApplicationContext(), "Internet connection is missing, please check it.", Toast.LENGTH_LONG).show();
    }

    private void showConnectionError() {
        Toast.makeText(getApplicationContext(), "Some connection error happened, please try again later.", Toast.LENGTH_LONG).show();
    }

    public void notifyUserUnRetweetFailed() {
        Snackbar.make(findViewById(R.id.constr_tweet_view), "Unretweet failed, please try to use Twitter app instead", Snackbar.LENGTH_LONG).show();
    }

    public void checkConnectivity() {
        if (ConnectivityChecker.checkConnectivity(new WeakReference<AppCompatActivity>(this))) {
            showConnectionError();
        } else {
            showConnectionMissing();
        }
    }
}
