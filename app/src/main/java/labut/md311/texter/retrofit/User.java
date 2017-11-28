package labut.md311.texter.retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("id")
    @Expose
    private final long id;

    @SerializedName("name")
    @Expose
    private final String name;

    @SerializedName("screen_name")
    @Expose
    private final String screen_name;

    @SerializedName("profile_image_url_https")
    @Expose
    private final String profile_image_url_https;

    public User(long id, String name, String profile_image_url_https, String screen_name) {
        this.id = id;
        this.name = name;
        this.screen_name = screen_name;
        this.profile_image_url_https = profile_image_url_https;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getScreen_name() {
        return screen_name;
    }

    public String getProfile_image_url_https() {
        return profile_image_url_https;
    }
}
