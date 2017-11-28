package labut.md311.texter.retrofit;


import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface ApiTwitter {
    @GET("account/verify_credentials.json")
    Call<Credentials> getUserCredentials(@QueryMap Map<String, String> params);

    @GET("statuses/home_timeline.json")
    Call<List<FeedItem>> getHomeTimeline(@QueryMap Map<String, String> params);

    @GET("statuses/retweets/{id}.json")
    Call<List<RetweetToDeleteResponse>> getRetweets(@Path("id") String id, @QueryMap Map<String, String> params);

    @POST("favorites/create.json")
    Call<LikeResponse> createFavorite(@QueryMap Map<String, String> params);

    @POST("favorites/destroy.json")
    Call<LikeResponse> destroyFavorite(@QueryMap Map<String, String> params);

    @POST("statuses/retweet/{id}.json")
    Call<RetweetResponse> postRetweet(@Path("id") String id, @QueryMap Map<String, String> params);

    @POST("statuses/unretweet/{id}.json")
    Call<RetweetResponse> deleteRetweet(@Path("id") String id, @QueryMap Map<String, String> params);
}
