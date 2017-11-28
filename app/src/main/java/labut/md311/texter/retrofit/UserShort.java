package labut.md311.texter.retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserShort {

    @SerializedName("id")
    @Expose
    private final long id;

    public UserShort(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

}
