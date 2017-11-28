package labut.md311.texter.view;

import android.os.Parcel;
import android.os.Parcelable;

public class TweetParcel implements Parcelable {
    String retweeted_by;
    String created_at;
    long id;
    int position;
    String text;
    String name;
    String screen_name;
    String profile_image_url_https;
    int retweet_count;
    int favorite_count;
    int retweeted;
    int favorited;

    protected TweetParcel(Parcel in) {
        retweeted_by = in.readString();
        created_at = in.readString();
        id = in.readLong();
        position = in.readInt();
        text = in.readString();
        name = in.readString();
        screen_name = in.readString();
        profile_image_url_https = in.readString();
        retweet_count = in.readInt();
        favorite_count = in.readInt();
        retweeted = in.readInt();
        favorited = in.readInt();
    }

    public static final Creator<TweetParcel> CREATOR = new Creator<TweetParcel>() {
        @Override
        public TweetParcel createFromParcel(Parcel in) {
            return new TweetParcel(in);
        }

        @Override
        public TweetParcel[] newArray(int size) {
            return new TweetParcel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(retweeted_by);
        dest.writeString(created_at);
        dest.writeLong(id);
        dest.writeInt(position);
        dest.writeString(text);
        dest.writeString(name);
        dest.writeString(screen_name);
        dest.writeString(profile_image_url_https);
        dest.writeInt(retweet_count);
        dest.writeInt(favorite_count);
        dest.writeInt(retweeted);
        dest.writeInt(favorited);
    }
}
