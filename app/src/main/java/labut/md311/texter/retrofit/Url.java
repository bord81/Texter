package labut.md311.texter.retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Url {

    @SerializedName("url")
    @Expose
    private final String url;

    @SerializedName("indices")
    @Expose
    private final List<Integer> indices;

    public Url(String url, List<Integer> indices) {
        this.url = url;
        this.indices = indices;
    }

    public String getUrl() {
        return url;
    }

    public List<Integer> getIndices() {
        return indices;
    }
}
