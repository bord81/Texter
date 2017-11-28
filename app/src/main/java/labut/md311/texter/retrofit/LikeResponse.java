package labut.md311.texter.retrofit;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LikeResponse {

    @SerializedName("created_at")
    @Expose
    private final String created_at;

    @SerializedName("id")
    @Expose
    private final long id;


    public LikeResponse(String created_at, long id) {
        this.created_at = created_at;
        this.id = id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public long getId() {
        return id;
    }
}
