package labut.md311.texter.retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Entities {

    @SerializedName("hashtags")
    @Expose
    private final List<HashTag> hashtags;

    @SerializedName("user_mentions")
    @Expose
    private final List<HashTag> user_mentions;

    @SerializedName("urls")
    @Expose
    private final List<Url> urls;

    public Entities(List<HashTag> hashtags, List<HashTag> user_mentions, List<Url> urls) {
        this.hashtags = hashtags;
        this.user_mentions = user_mentions;
        this.urls = urls;
    }

    public List<HashTag> getHashtags() {
        return hashtags;
    }

    public List<HashTag> getUser_mentions() {
        return user_mentions;
    }

    public List<Url> getUrls() {
        return urls;
    }
}
