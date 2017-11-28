package labut.md311.texter.retrofit;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RetweetResponse {

    @SerializedName("created_at")
    @Expose
    private final String created_at;

    @SerializedName("id_str")
    @Expose
    private final String id_str;

    public RetweetResponse(String created_at, String id_str) {
        this.created_at = created_at;
        this.id_str = id_str;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getId() {
        return id_str;
    }
}
