package labut.md311.texter.retrofit;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FeedItem {

    @SerializedName("created_at")
    @Expose
    private final String created_at;

    @SerializedName("id")
    @Expose
    private final long id;

    @SerializedName("text")
    @Expose
    private final String text;

    @SerializedName("in_reply_to_user_id")
    @Expose
    private final long in_reply_to_user_id;

    @SerializedName("user")
    @Expose
    private final User user;

    @SerializedName("is_quote_status")
    @Expose
    private final boolean is_quote_status;

    @SerializedName("retweet_count")
    @Expose
    private volatile int retweet_count;

    @SerializedName("favorite_count")
    @Expose
    private volatile int favorite_count;

    @SerializedName("favorited")
    @Expose
    private volatile boolean favorited;

    @SerializedName("retweeted")
    @Expose
    private volatile boolean retweeted;

    @SerializedName("retweeted_status")
    @Expose
    private final RetweetedItem retweeted_status;

    public FeedItem(String created_at, long id, String text, long in_reply_to_user_id, User user, boolean is_quote_status, int retweet_count, int favorite_count, boolean favorited, boolean retweeted, RetweetedItem retweeted_status) {
        this.created_at = created_at;
        this.id = id;
        this.text = text;
        this.in_reply_to_user_id = in_reply_to_user_id;
        this.user = user;
        this.is_quote_status = is_quote_status;
        this.retweet_count = retweet_count;
        this.favorite_count = favorite_count;
        this.favorited = favorited;
        this.retweeted = retweeted;
        this.retweeted_status = retweeted_status;
    }

    public String getCreated_at() {
        return created_at;
    }

    public long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public long getIn_reply_to_user_id() {
        return in_reply_to_user_id;
    }

    public User getUser() {
        return user;
    }

    public boolean isIs_quote_status() {
        return is_quote_status;
    }

    public int getRetweet_count() {
        return retweet_count;
    }

    public int getFavorite_count() {
        return favorite_count;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public boolean isRetweeted() {
        return retweeted;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    public void setRetweet_count(int retweet_count) {
        this.retweet_count = retweet_count;
    }

    public void setRetweeted(boolean retweeted) {
        this.retweeted = retweeted;
    }

    public void setFavorite_count(int favorite_count) {
        this.favorite_count = favorite_count;
    }

    public RetweetedItem getRetweeted_status() {
        return retweeted_status;
    }
}
