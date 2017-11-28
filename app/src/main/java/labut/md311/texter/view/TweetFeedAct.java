package labut.md311.texter.view;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import labut.md311.texter.R;
import labut.md311.texter.network.ConnectivityChecker;
import labut.md311.texter.presenter.TweetFeedPresenter;
import labut.md311.texter.retrofit.FeedItem;
import labut.md311.texter.retrofit.RetweetedItem;
import labut.md311.texter.retrofit.User;

//user's home timeline feed
public class TweetFeedAct extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private RecAdapter recAdapter;
    private OnScrollDownListener onScrollDownListener;
    public RecyclerView recyclerView;
    public SwipeRefreshLayout swipeRefreshLayout;
    public static WeakReference<ArrayList<FeedItem>> feedItems;
    private TweetFeedPresenter tweetFeedPresenter;
    private ActionBar actionBar;
    private boolean name_loaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_feed);
        initPresenter();
        Toolbar feedToolbar = (Toolbar) findViewById(R.id.feed_toolbar);
        setSupportActionBar(feedToolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            String name = getSharedPreferences(LoginActivity.TWITTER_LOGIN, MODE_PRIVATE).getString(LoginActivity.TWITTER_LOGIN_NAME, "your");
            if (!name.equals("your"))
                name_loaded = true;
            getSupportActionBar().setTitle("@" + name + " tweet feed");
        }
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_rl);
        initRecyclerView();
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feed_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout_button:
                getSharedPreferences(LoginActivity.TWITTER_LOGIN, MODE_PRIVATE).edit().clear().commit();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        saveCurrentVisibleTop(layoutManager.findFirstVisibleItemPosition());
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        tweetFeedPresenter.onRestoreView();
    }

    public void initPresenter() {
        tweetFeedPresenter = new TweetFeedPresenter(new WeakReference<TweetFeedAct>(this));
        tweetFeedPresenter.setAccessTokens(getSharedPreferences(LoginActivity.TWITTER_LOGIN, MODE_PRIVATE).getString(LoginActivity.TWITTER_LOGIN_TOKEN, ""), getSharedPreferences(LoginActivity.TWITTER_LOGIN, MODE_PRIVATE).getString(LoginActivity.TWITTER_LOGIN_SECRET, ""));
    }

    public void checkShownName(String name) {
        if (actionBar == null)
            actionBar = getSupportActionBar();
        if (!name_loaded && actionBar != null) {
            actionBar.setTitle("@" + name + " tweet feed");
            name_loaded = true;
        }
    }

    public void setAdapter(WeakReference<ArrayList<FeedItem>> feedItems, int prevPosition) {
        this.feedItems = feedItems;
        AdvanceLayoutManager adm = new AdvanceLayoutManager(getApplicationContext());
        if (prevPosition > 0) {
            setAdapterInit(adm, false);
            setAdapterContentOnRestore(adm, prevPosition);
        } else {
            if (feedItems.get().size() > 1) {
                setAdapterContent(adm);
            } else {
                setAdapterInit(adm, true);
            }
        }
    }

    private void setAdapterInit(AdvanceLayoutManager adm, boolean stub) {
        recAdapter = new RecAdapter(getApplicationContext(), feedItems.get(), stub, (view, type, position) -> {
            switch (type) {
                case RecAdapter.RETWEET_VIEW:
                    if (!(Boolean) view.getTag(R.id.img_retweets)) {
                        tweetFeedPresenter.postRetweet(position);
                    } else {
                        tweetFeedPresenter.deleteRetweet(position);
                    }
                    break;
                case RecAdapter.LIKE_VIEW:
                    if (!(Boolean) view.getTag(R.id.img_likes)) {
                        tweetFeedPresenter.setLike(position);
                    } else {
                        tweetFeedPresenter.removeLike(position);
                    }
                    break;
                case RecAdapter.BODY_VIEW:
                    Intent intent = new Intent(getApplicationContext(), TweetViewAct.class);
                    TweetParcel tweetParcel = new TweetParcel(ParcelGenerator.generateParcel(this.feedItems, position));
                    tweetFeedPresenter.saveCurrentState(adm.findFirstVisibleItemPosition());
                    intent.putExtra(TweetViewAct.TWEET_PARCEL, tweetParcel);
                    startActivity(intent);
                    break;
            }


        });
        recyclerView = (RecyclerView) findViewById(R.id.rec_view);
        recyclerView.setItemViewCacheSize(40);
        recyclerView.setLayoutManager(adm);
        recyclerView.clearOnScrollListeners();
        onScrollDownListener = new OnScrollDownListener(adm, TweetFeedPresenter.LOAD_SIZE, -1) {
            @Override
            public void loadPrevious() {
                tweetFeedPresenter.loadPrevItems();
            }
        };
        recyclerView.addOnScrollListener(onScrollDownListener);

        recyclerView.setAdapter(recAdapter);
    }

    private void setAdapterContent(AdvanceLayoutManager adm) {
        saveCurrentTop(feedItems);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), adm.getOrientation()));
        onScrollDownListener.setCurrentTop(feedItems.get().get(0).getId());
        recAdapter.updateFeedItems(feedItems.get());
        recAdapter.notifyDataSetChanged();
    }

    private void setAdapterContentOnRestore(AdvanceLayoutManager adm, int position) {
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), adm.getOrientation()));
        recAdapter.updateFeedItems(feedItems.get());
        onScrollDownListener.setCurrentTop(position);
        adm.scrollToPosition(position);
    }

    public void updateAdapter(WeakReference<ArrayList<FeedItem>> feedItems, int itemsAdded, boolean isNew) {
        this.feedItems = feedItems;
        recAdapter.updateFeedItems(feedItems.get());
        if (isNew) {
            saveCurrentTop(feedItems);
            recAdapter.notifyItemRangeInserted(0, itemsAdded);
            swipeRefreshLayout.setRefreshing(false);
        } else {
            recAdapter.notifyItemRangeInserted(feedItems.get().size() - itemsAdded, itemsAdded);
        }
    }

    public void updateAdapterOnUserEvent(int position) {
        recAdapter.notifyItemChanged(position);
    }

    public void notifyUserOnNetworkError(int status) {
        if (swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
        switch (status) {
            case 403:
                showLimitSnackbar();
            case 429:
                showLimitSnackbar();
                onScrollDownListener.requestForceLoad();
                break;
        }
    }

    public void notifyUserUnRetweetFailed() {
        Snackbar.make(findViewById(R.id.rel_tweet_feed), "Unretweet failed, please try to use Twitter app instead", Snackbar.LENGTH_LONG).show();
    }

    private void showLimitSnackbar() {
        Snackbar.make(findViewById(R.id.rel_tweet_feed), "Call limit to Twitter reached, try again later", Snackbar.LENGTH_LONG).show();
    }

    private void initRecyclerView() {
        //stub object to initialize recyclerview and avoid warnings/errors on later stages
        ArrayList<FeedItem> stub = new ArrayList<>();
        stub.add(new FeedItem("", 0L, "", 0L, new User(0L, "", "", ""), false, 0, 0, false, false, new RetweetedItem("6", 0, "", 0, new User(0L, "", "", ""), false, 0, 0, false, false)));
        setAdapter(new WeakReference<ArrayList<FeedItem>>(stub), -1);
    }

    private void saveCurrentTop(WeakReference<ArrayList<FeedItem>> feedItems) {
        getSharedPreferences("currentTop", MODE_PRIVATE).edit().putLong("top", feedItems.get().get(0).getId()).apply();
    }

    private void saveCurrentVisibleTop(int position) {
        getSharedPreferences("currentTop", MODE_PRIVATE).edit().putLong("top", feedItems.get().get(position).getId()).apply();
    }

    @Override
    public void onRefresh() {
        tweetFeedPresenter.loadNewItems();
    }

    public void disableRefresh() {
        swipeRefreshLayout.setRefreshing(false);
    }

    public void readSavedTop() {
        tweetFeedPresenter.initFirstLoad(getSharedPreferences("currentTop", MODE_PRIVATE).getLong("top", -1));
    }

    private void showConnectionMissing() {
        Toast.makeText(getApplicationContext(), "Internet connection is missing, please check it.", Toast.LENGTH_LONG).show();
        onScrollDownListener.requestForceLoad();
    }

    private void showConnectionError() {
        Toast.makeText(getApplicationContext(), "Some connection error happened, please try again later.", Toast.LENGTH_LONG).show();
        onScrollDownListener.requestForceLoad();
    }

    public void checkConnectivity() {
        if (ConnectivityChecker.checkConnectivity(new WeakReference<AppCompatActivity>(this))) {
            showConnectionError();
        } else {
            showConnectionMissing();
        }
    }
}
