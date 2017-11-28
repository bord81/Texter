package labut.md311.texter.retrofit;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HashTag {

    @SerializedName("text")
    @Expose
    private final String text;

    @SerializedName("indices")
    @Expose
    private final List<Integer> indices;

    public HashTag(String text, List<Integer> indices) {
        this.text = text;
        this.indices = indices;
    }

    public String getText() {
        return text;
    }

    public List<Integer> getIndices() {
        return indices;
    }
}
