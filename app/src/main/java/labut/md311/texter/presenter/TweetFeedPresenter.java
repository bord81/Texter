package labut.md311.texter.presenter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import labut.md311.texter.BuildConfig;
import labut.md311.texter.network.OAuthForm;
import labut.md311.texter.retrofit.ApiTwitter;
import labut.md311.texter.retrofit.Credentials;
import labut.md311.texter.retrofit.FeedItem;
import labut.md311.texter.retrofit.LikeResponse;
import labut.md311.texter.retrofit.RetweetResponse;
import labut.md311.texter.retrofit.RetweetToDeleteResponse;
import labut.md311.texter.view.TweetFeedAct;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//logic for tweet feed
public class TweetFeedPresenter {

    protected static String ACCESS_TOKEN;
    protected static String ACCESS_SECRET_TOKEN;
    protected final static String tw_cons_key = BuildConfig.TwitterConsumerKey;
    protected final static String tw_priv_cons_key = BuildConfig.TwitterConsumerSecretKey;
    public final static int LOAD_SIZE = 40;
    public static int currentTotalItems;
    public static ArrayList<FeedItem> feedItems;

    private static OkHttpClient okHttpClient;
    private static Retrofit retrofit;
    private static ApiTwitter apiTwitter;

    public static Credentials userCredentials;
    public static ArrayList<RetweetToDeleteResponse> toDeleteResponses;
    private static int positionOnPrevious;
    long top_fallback = 0L;
    
    WeakReference<TweetFeedAct> tweetFeedActWeakReference;


    public TweetFeedPresenter(WeakReference<TweetFeedAct> tweetFeedActWeakReference) {
        this.tweetFeedActWeakReference = tweetFeedActWeakReference;
    }

    public void onRestoreView() {
        if (feedItems == null) {
            currentTotalItems = LOAD_SIZE;
            tweetFeedActWeakReference.get().readSavedTop();
        } else {
            restorePreviousScreen();
        }
    }

    public void initFirstLoad(long topItem) {
        loadTimeline(topItem, true);
        top_fallback = topItem;
    }

    public void loadPrevItems() {
        loadTimeline(feedItems.get(feedItems.size() - 1).getId(), false);
    }

    public void loadNewItems() {
        loadTimeline(-1, true);
    }

    public void saveCurrentState(int position) {
        positionOnPrevious = position;
    }

    private void restorePreviousScreen() {
        tweetFeedActWeakReference.get().setAdapter(new WeakReference<ArrayList<FeedItem>>(feedItems), positionOnPrevious);
    }

    public void setAccessTokens(String token, String secret) {
        ACCESS_TOKEN = token;
        ACCESS_SECRET_TOKEN = secret;
        loadCredentials();
    }

    private void loadCredentials() {
        okHttpClient = new OkHttpClient.Builder().addNetworkInterceptor(new OAuthForm(OAuthForm.AUTHORIZE_REQUEST, null, "GET", "HMAC-SHA1", "1.0", tw_cons_key, tw_priv_cons_key, ACCESS_TOKEN, ACCESS_SECRET_TOKEN, null, null)).build();
        retrofit = new Retrofit.Builder().baseUrl(OAuthForm.BASE_URL).addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        apiTwitter = retrofit.create(ApiTwitter.class);
        HashMap<String, String> map = new HashMap<>();
        map.put("include_entities", "false");
        map.put("skip_status", "true");
        map.put("include_email", "false");
        apiTwitter.getUserCredentials(map).enqueue(new Callback<Credentials>() {
            @Override
            public void onResponse(Call<Credentials> call, Response<Credentials> response) {
                int rc;
                if ((rc = response.code()) != 200) {
                    tweetFeedActWeakReference.get().notifyUserOnNetworkError(rc);
                } else {
                    userCredentials = (Credentials) response.body();
                    tweetFeedActWeakReference.get().checkShownName(userCredentials.getScreen_name());
                }
            }

            @Override
            public void onFailure(Call<Credentials> call, Throwable t) {
                tweetFeedActWeakReference.get().checkConnectivity();
            }
        });
    }

    private void loadTimeline(final long topItem, boolean firstLoad) {
        okHttpClient = new OkHttpClient.Builder().addNetworkInterceptor(new OAuthForm(OAuthForm.AUTHORIZE_REQUEST, null, "GET", "HMAC-SHA1", "1.0", tw_cons_key, tw_priv_cons_key, ACCESS_TOKEN, ACCESS_SECRET_TOKEN, null, null)).build();
        retrofit = new Retrofit.Builder().baseUrl(OAuthForm.BASE_URL).addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        apiTwitter = retrofit.create(ApiTwitter.class);
        HashMap<String, String> map = new HashMap<>();
        map.put("include_entities", "false");
        map.put("count", String.valueOf(LOAD_SIZE));
        if (firstLoad) {
            if (topItem > 0) {
                map.put("max_id", String.valueOf(topItem));
            } else if (feedItems != null && feedItems.size() > 1) {
                map.put("since_id", String.valueOf(feedItems.get(0).getId()));
            } else if (top_fallback > 0) {
                map.put("since_id", String.valueOf(top_fallback));
            }
            apiTwitter.getHomeTimeline(map).enqueue(new Callback<List<FeedItem>>() {
                @Override
                public void onResponse(Call<List<FeedItem>> call, Response<List<FeedItem>> response) {
                    int rc;
                    if ((rc = response.code()) != 200) {
                        tweetFeedActWeakReference.get().notifyUserOnNetworkError(rc);
                    } else {
                        ArrayList<FeedItem> newFeedItems = (ArrayList<FeedItem>) response.body();
                        if (feedItems == null) {
                            feedItems = newFeedItems;
                            tweetFeedActWeakReference.get().setAdapter(new WeakReference<ArrayList<FeedItem>>(feedItems), -1);
                        } else {
                            if (newFeedItems.size() > 0) {
                                feedItems.addAll(0, newFeedItems);
                                tweetFeedActWeakReference.get().updateAdapter(new WeakReference<ArrayList<FeedItem>>(feedItems), newFeedItems.size(), true);
                            } else {
                                tweetFeedActWeakReference.get().disableRefresh();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<FeedItem>> call, Throwable t) {
                    tweetFeedActWeakReference.get().disableRefresh();
                    tweetFeedActWeakReference.get().checkConnectivity();
                }
            });

        } else {
            map.put("max_id", String.valueOf(topItem));
            apiTwitter.getHomeTimeline(map).enqueue(new Callback<List<FeedItem>>() {
                @Override
                public void onResponse(Call<List<FeedItem>> call, Response<List<FeedItem>> response) {
                    int rc;
                    if ((rc = response.code()) != 200) {
                        tweetFeedActWeakReference.get().notifyUserOnNetworkError(rc);
                    } else {
                        ArrayList<FeedItem> newFeedItems = (ArrayList<FeedItem>) response.body();
                        newFeedItems.remove(0);
                        feedItems.addAll(newFeedItems);
                        tweetFeedActWeakReference.get().updateAdapter(new WeakReference<ArrayList<FeedItem>>(feedItems), newFeedItems.size(), false);
                    }
                    tweetFeedActWeakReference.get().disableRefresh();
                }

                @Override
                public void onFailure(Call<List<FeedItem>> call, Throwable t) {
                    tweetFeedActWeakReference.get().disableRefresh();
                    tweetFeedActWeakReference.get().checkConnectivity();
                }
            });
        }
    }

    public void setLike(int position) {
        okHttpClient = new OkHttpClient.Builder().addNetworkInterceptor(new OAuthForm(OAuthForm.AUTHORIZE_REQUEST, null, "POST", "HMAC-SHA1", "1.0", tw_cons_key, tw_priv_cons_key, ACCESS_TOKEN, ACCESS_SECRET_TOKEN, null, null)).build();
        retrofit = new Retrofit.Builder().baseUrl(OAuthForm.BASE_URL).addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        apiTwitter = retrofit.create(ApiTwitter.class);
        HashMap<String, String> map = new HashMap<>();
        map.put("include_entities", "false");
        map.put("id", String.valueOf(feedItems.get(position).getId()));
        apiTwitter.createFavorite(map).enqueue(new Callback<LikeResponse>() {
            @Override
            public void onResponse(Call<LikeResponse> call, Response<LikeResponse> response) {
                int rc;
                if ((rc = response.code()) == 200) {
                    feedItems.get(position).setFavorited(true);
                    if (feedItems.get(position).getRetweeted_status() == null) {
                        feedItems.get(position).setFavorite_count(feedItems.get(position).getFavorite_count() + 1);
                    } else {
                        feedItems.get(position).getRetweeted_status().setFavorite_count(feedItems.get(position).getRetweeted_status().getFavorite_count() + 1);
                    }
                    tweetFeedActWeakReference.get().updateAdapterOnUserEvent(position);
                } else {
                    tweetFeedActWeakReference.get().notifyUserOnNetworkError(rc);
                }
            }

            @Override
            public void onFailure(Call<LikeResponse> call, Throwable t) {
                tweetFeedActWeakReference.get().checkConnectivity();
            }
        });
    }

    public void removeLike(int position) {
        okHttpClient = new OkHttpClient.Builder().addNetworkInterceptor(new OAuthForm(OAuthForm.AUTHORIZE_REQUEST, null, "POST", "HMAC-SHA1", "1.0", tw_cons_key, tw_priv_cons_key, ACCESS_TOKEN, ACCESS_SECRET_TOKEN, null, null)).build();
        retrofit = new Retrofit.Builder().baseUrl(OAuthForm.BASE_URL).addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        apiTwitter = retrofit.create(ApiTwitter.class);
        HashMap<String, String> map = new HashMap<>();
        map.put("include_entities", "false");
        map.put("id", String.valueOf(feedItems.get(position).getId()));
        apiTwitter.destroyFavorite(map).enqueue(new Callback<LikeResponse>() {
            @Override
            public void onResponse(Call<LikeResponse> call, Response<LikeResponse> response) {
                int rc;
                if ((rc = response.code()) == 200) {
                    feedItems.get(position).setFavorited(false);
                    if (feedItems.get(position).getRetweeted_status() == null) {
                        feedItems.get(position).setFavorite_count(feedItems.get(position).getFavorite_count() - 1);
                    } else {
                        feedItems.get(position).getRetweeted_status().setFavorite_count(feedItems.get(position).getRetweeted_status().getFavorite_count() - 1);
                    }
                    tweetFeedActWeakReference.get().updateAdapterOnUserEvent(position);
                } else {
                    tweetFeedActWeakReference.get().notifyUserOnNetworkError(rc);
                }
            }

            @Override
            public void onFailure(Call<LikeResponse> call, Throwable t) {
                tweetFeedActWeakReference.get().checkConnectivity();
            }
        });
    }

    public void postRetweet(int position) {
        okHttpClient = new OkHttpClient.Builder().addNetworkInterceptor(new OAuthForm(OAuthForm.AUTHORIZE_REQUEST, null, "POST", "HMAC-SHA1", "1.0", tw_cons_key, tw_priv_cons_key, ACCESS_TOKEN, ACCESS_SECRET_TOKEN, null, null)).build();
        retrofit = new Retrofit.Builder().baseUrl(OAuthForm.BASE_URL).addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        apiTwitter = retrofit.create(ApiTwitter.class);
        HashMap<String, String> map = new HashMap<>();
        map.put("trim_user", "true");
        apiTwitter.postRetweet(String.valueOf(feedItems.get(position).getId()), map).enqueue(new Callback<RetweetResponse>() {
            @Override
            public void onResponse(Call<RetweetResponse> call, Response<RetweetResponse> response) {
                int rc;
                if ((rc = response.code()) == 200) {
                    feedItems.get(position).setRetweeted(true);
                    if (feedItems.get(position).getRetweeted_status() == null) {
                        feedItems.get(position).setRetweet_count(feedItems.get(position).getRetweet_count() + 1);
                    } else {
                        feedItems.get(position).getRetweeted_status().setRetweet_count(feedItems.get(position).getRetweeted_status().getRetweet_count() + 1);
                    }
                    tweetFeedActWeakReference.get().updateAdapterOnUserEvent(position);
                } else {
                    tweetFeedActWeakReference.get().notifyUserOnNetworkError(rc);
                }
            }

            @Override
            public void onFailure(Call<RetweetResponse> call, Throwable t) {
                tweetFeedActWeakReference.get().checkConnectivity();
            }
        });
    }

    public void deleteRetweet(int position) {
        okHttpClient = new OkHttpClient.Builder().addNetworkInterceptor(new OAuthForm(OAuthForm.AUTHORIZE_REQUEST, null, "GET", "HMAC-SHA1", "1.0", tw_cons_key, tw_priv_cons_key, ACCESS_TOKEN, ACCESS_SECRET_TOKEN, null, null)).build();
        retrofit = new Retrofit.Builder().baseUrl(OAuthForm.BASE_URL).addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        apiTwitter = retrofit.create(ApiTwitter.class);
        HashMap<String, String> map = new HashMap<>();
        map.put("trim_user", "true");
        map.put("count", "100");
        apiTwitter.getRetweets(String.valueOf(feedItems.get(position).getId()), map).enqueue(new Callback<List<RetweetToDeleteResponse>>() {
            @Override
            public void onResponse(Call<List<RetweetToDeleteResponse>> call, Response<List<RetweetToDeleteResponse>> response) {
                int rc;
                if ((rc = response.code()) != 200) {
                    tweetFeedActWeakReference.get().notifyUserOnNetworkError(rc);
                } else {
                    toDeleteResponses = (ArrayList<RetweetToDeleteResponse>) response.body();
                    removeRetweet(position);
                }
            }

            @Override
            public void onFailure(Call<List<RetweetToDeleteResponse>> call, Throwable t) {
                tweetFeedActWeakReference.get().checkConnectivity();
            }
        });
    }

    protected void removeRetweet(int position) {
        okHttpClient = new OkHttpClient.Builder().addNetworkInterceptor(new OAuthForm(OAuthForm.AUTHORIZE_REQUEST, null, "POST", "HMAC-SHA1", "1.0", tw_cons_key, tw_priv_cons_key, ACCESS_TOKEN, ACCESS_SECRET_TOKEN, null, null)).build();
        retrofit = new Retrofit.Builder().baseUrl(OAuthForm.BASE_URL).addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        apiTwitter = retrofit.create(ApiTwitter.class);
        String deleteId = new String();
        boolean found = false;
        for (int i = 0; i < toDeleteResponses.size(); i++) {
            if (toDeleteResponses.get(i).getUser().getId() == userCredentials.getId()) {
                deleteId = toDeleteResponses.get(i).getId_str();
                found = true;
            }
        }
        if (found) {
            toDeleteResponses = null;
            HashMap<String, String> map = new HashMap<>();
            map.put("trim_user", "true");
            apiTwitter.deleteRetweet(deleteId, map).enqueue(new Callback<RetweetResponse>() {
                @Override
                public void onResponse(Call<RetweetResponse> call, Response<RetweetResponse> response) {
                    int rc;
                    if ((rc = response.code()) == 200) {
                        feedItems.get(position).setRetweeted(false);
                        if (feedItems.get(position).getRetweeted_status() == null) {
                            feedItems.get(position).setRetweet_count(feedItems.get(position).getRetweet_count() - 1);
                        } else {
                            feedItems.get(position).getRetweeted_status().setRetweet_count(feedItems.get(position).getRetweeted_status().getRetweet_count() - 1);
                        }
                        tweetFeedActWeakReference.get().updateAdapterOnUserEvent(position);
                    } else {
                        tweetFeedActWeakReference.get().notifyUserOnNetworkError(rc);
                    }
                }

                @Override
                public void onFailure(Call<RetweetResponse> call, Throwable t) {
                    tweetFeedActWeakReference.get().checkConnectivity();
                }
            });
        } else {
            tweetFeedActWeakReference.get().notifyUserUnRetweetFailed();
        }
    }
}
