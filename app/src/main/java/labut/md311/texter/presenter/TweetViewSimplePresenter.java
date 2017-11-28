package labut.md311.texter.presenter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import labut.md311.texter.BuildConfig;
import labut.md311.texter.network.OAuthForm;
import labut.md311.texter.retrofit.ApiTwitter;
import labut.md311.texter.retrofit.LikeResponse;
import labut.md311.texter.retrofit.RetweetResponse;
import labut.md311.texter.retrofit.RetweetToDeleteResponse;
import labut.md311.texter.view.TweetViewAct;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//logic for single tweet display
public class TweetViewSimplePresenter {
    private final static String ACCESS_TOKEN = TweetFeedPresenter.ACCESS_TOKEN;
    private final static String ACCESS_SECRET_TOKEN = TweetFeedPresenter.ACCESS_SECRET_TOKEN;
    private final static String tw_cons_key = BuildConfig.TwitterConsumerKey;
    private final static String tw_priv_cons_key = BuildConfig.TwitterConsumerSecretKey;
    private static OkHttpClient okHttpClient;
    private static Retrofit retrofit;
    private static ApiTwitter apiTwitter;
    public ArrayList<RetweetToDeleteResponse> toDeleteResponses;

    public static final int RETWEET_POST = 0;
    public static final int RETWEET_DELETE = 1;
    public static final int LIKE_POST = 2;
    public static final int LIKE_DELETE = 3;

    WeakReference<TweetViewAct> tweetViewActWeakReference;

    public TweetViewSimplePresenter(WeakReference<TweetViewAct> tweetViewActWeakReference) {
        this.tweetViewActWeakReference = tweetViewActWeakReference;
    }

    public void setLike(int position) {
        okHttpClient = new OkHttpClient.Builder().addNetworkInterceptor(new OAuthForm(OAuthForm.AUTHORIZE_REQUEST, null, "POST", "HMAC-SHA1", "1.0", tw_cons_key, tw_priv_cons_key, ACCESS_TOKEN, ACCESS_SECRET_TOKEN, null, null)).build();
        retrofit = new Retrofit.Builder().baseUrl(OAuthForm.BASE_URL).addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        apiTwitter = retrofit.create(ApiTwitter.class);
        HashMap<String, String> map = new HashMap<>();
        map.put("include_entities", "false");
        map.put("id", String.valueOf(TweetFeedPresenter.feedItems.get(position).getId()));
        apiTwitter.createFavorite(map).enqueue(new Callback<LikeResponse>() {
            @Override
            public void onResponse(Call<LikeResponse> call, Response<LikeResponse> response) {
                int rc;
                if ((rc = response.code()) == 200) {
                    TweetFeedPresenter.feedItems.get(position).setFavorited(true);
                    if (TweetFeedPresenter.feedItems.get(position).getRetweeted_status() == null) {
                        TweetFeedPresenter.feedItems.get(position).setFavorite_count(TweetFeedPresenter.feedItems.get(position).getFavorite_count() + 1);
                    } else {
                        TweetFeedPresenter.feedItems.get(position).getRetweeted_status().setFavorite_count(TweetFeedPresenter.feedItems.get(position).getRetweeted_status().getFavorite_count() + 1);
                    }
                    tweetViewActWeakReference.get().updateViewOnUserEvent(LIKE_POST);
                } else {
                    tweetViewActWeakReference.get().notifyUserOnNetworkError(rc);
                }
            }

            @Override
            public void onFailure(Call<LikeResponse> call, Throwable t) {
                tweetViewActWeakReference.get().checkConnectivity();
            }
        });
    }

    public void removeLike(int position) {
        okHttpClient = new OkHttpClient.Builder().addNetworkInterceptor(new OAuthForm(OAuthForm.AUTHORIZE_REQUEST, null, "POST", "HMAC-SHA1", "1.0", tw_cons_key, tw_priv_cons_key, ACCESS_TOKEN, ACCESS_SECRET_TOKEN, null, null)).build();
        retrofit = new Retrofit.Builder().baseUrl(OAuthForm.BASE_URL).addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        apiTwitter = retrofit.create(ApiTwitter.class);
        HashMap<String, String> map = new HashMap<>();
        map.put("include_entities", "false");
        map.put("id", String.valueOf(TweetFeedPresenter.feedItems.get(position).getId()));
        apiTwitter.destroyFavorite(map).enqueue(new Callback<LikeResponse>() {
            @Override
            public void onResponse(Call<LikeResponse> call, Response<LikeResponse> response) {
                int rc;
                if ((rc = response.code()) == 200) {
                    TweetFeedPresenter.feedItems.get(position).setFavorited(false);
                    if (TweetFeedPresenter.feedItems.get(position).getRetweeted_status() == null) {
                        TweetFeedPresenter.feedItems.get(position).setFavorite_count(TweetFeedPresenter.feedItems.get(position).getFavorite_count() - 1);
                    } else {
                        TweetFeedPresenter.feedItems.get(position).getRetweeted_status().setFavorite_count(TweetFeedPresenter.feedItems.get(position).getRetweeted_status().getFavorite_count() - 1);
                    }
                    tweetViewActWeakReference.get().updateViewOnUserEvent(LIKE_DELETE);
                } else {
                    tweetViewActWeakReference.get().notifyUserOnNetworkError(rc);
                }
            }

            @Override
            public void onFailure(Call<LikeResponse> call, Throwable t) {
                tweetViewActWeakReference.get().checkConnectivity();
            }
        });
    }

    public void postRetweet(int position) {
        okHttpClient = new OkHttpClient.Builder().addNetworkInterceptor(new OAuthForm(OAuthForm.AUTHORIZE_REQUEST, null, "POST", "HMAC-SHA1", "1.0", tw_cons_key, tw_priv_cons_key, ACCESS_TOKEN, ACCESS_SECRET_TOKEN, null, null)).build();
        retrofit = new Retrofit.Builder().baseUrl(OAuthForm.BASE_URL).addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        apiTwitter = retrofit.create(ApiTwitter.class);
        HashMap<String, String> map = new HashMap<>();
        map.put("trim_user", "true");
        apiTwitter.postRetweet(String.valueOf(TweetFeedPresenter.feedItems.get(position).getId()), map).enqueue(new Callback<RetweetResponse>() {
            @Override
            public void onResponse(Call<RetweetResponse> call, Response<RetweetResponse> response) {
                int rc;
                if ((rc = response.code()) == 200) {
                    TweetFeedPresenter.feedItems.get(position).setRetweeted(true);
                    if (TweetFeedPresenter.feedItems.get(position).getRetweeted_status() == null) {
                        TweetFeedPresenter.feedItems.get(position).setRetweet_count(TweetFeedPresenter.feedItems.get(position).getRetweet_count() + 1);
                    } else {
                        TweetFeedPresenter.feedItems.get(position).getRetweeted_status().setRetweet_count(TweetFeedPresenter.feedItems.get(position).getRetweeted_status().getRetweet_count() + 1);
                    }
                    tweetViewActWeakReference.get().updateViewOnUserEvent(RETWEET_POST);
                } else {
                    tweetViewActWeakReference.get().notifyUserOnNetworkError(rc);
                }
            }

            @Override
            public void onFailure(Call<RetweetResponse> call, Throwable t) {
                tweetViewActWeakReference.get().checkConnectivity();
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
        apiTwitter.getRetweets(String.valueOf(TweetFeedPresenter.feedItems.get(position).getId()), map).enqueue(new Callback<List<RetweetToDeleteResponse>>() {
            @Override
            public void onResponse(Call<List<RetweetToDeleteResponse>> call, Response<List<RetweetToDeleteResponse>> response) {
                int rc;
                if ((rc = response.code()) != 200) {
                    tweetViewActWeakReference.get().notifyUserOnNetworkError(rc);
                } else {
                    toDeleteResponses = (ArrayList<RetweetToDeleteResponse>) response.body();
                    removeRetweet(position);
                }
            }

            @Override
            public void onFailure(Call<List<RetweetToDeleteResponse>> call, Throwable t) {
                tweetViewActWeakReference.get().checkConnectivity();
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
            if (toDeleteResponses.get(i).getUser().getId() == TweetFeedPresenter.userCredentials.getId()) {
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
                        TweetFeedPresenter.feedItems.get(position).setRetweeted(false);
                        if (TweetFeedPresenter.feedItems.get(position).getRetweeted_status() == null) {
                            TweetFeedPresenter.feedItems.get(position).setRetweet_count(TweetFeedPresenter.feedItems.get(position).getRetweet_count() - 1);
                        } else {
                            TweetFeedPresenter.feedItems.get(position).getRetweeted_status().setRetweet_count(TweetFeedPresenter.feedItems.get(position).getRetweeted_status().getRetweet_count() - 1);
                        }
                        tweetViewActWeakReference.get().updateViewOnUserEvent(RETWEET_DELETE);
                    } else {
                        tweetViewActWeakReference.get().notifyUserOnNetworkError(rc);
                    }
                }

                @Override
                public void onFailure(Call<RetweetResponse> call, Throwable t) {
                    tweetViewActWeakReference.get().checkConnectivity();
                }
            });
        } else {
            tweetViewActWeakReference.get().notifyUserUnRetweetFailed();
        }
    }
}
