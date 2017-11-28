package labut.md311.texter.retrofit;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RetweetToDeleteResponse {

    @SerializedName("id")
    @Expose
    private final String id_str;

    @SerializedName("user")
    @Expose
    private final UserShort user;

    public RetweetToDeleteResponse(String id_str, UserShort user) {
        this.id_str = id_str;
        this.user = user;
    }

    public String getId_str() {
        return id_str;
    }

    public UserShort getUser() {
        return user;
    }
}
