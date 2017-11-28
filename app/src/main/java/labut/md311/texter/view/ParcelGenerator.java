package labut.md311.texter.view;

import android.os.Parcel;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import labut.md311.texter.retrofit.FeedItem;

public class ParcelGenerator {

    public static Parcel generateParcel(WeakReference<ArrayList<FeedItem>> feedItems, int position) {
        Parcel parcel = Parcel.obtain();
        if (feedItems.get().get(position).getRetweeted_status() != null) {
            parcel.writeString(feedItems.get().get(position).getUser().getName());
            parcel.writeString(feedItems.get().get(position).getRetweeted_status().getCreated_at());
            parcel.writeLong(feedItems.get().get(position).getId());
            parcel.writeInt(position);
            parcel.writeString(feedItems.get().get(position).getRetweeted_status().getText());
            parcel.writeString(feedItems.get().get(position).getRetweeted_status().getUser().getName());
            parcel.writeString(feedItems.get().get(position).getRetweeted_status().getUser().getScreen_name());
            parcel.writeString(feedItems.get().get(position).getRetweeted_status().getUser().getProfile_image_url_https());
            parcel.writeInt(feedItems.get().get(position).getRetweeted_status().getRetweet_count());
            parcel.writeInt(feedItems.get().get(position).getRetweeted_status().getFavorite_count());
            parcel.writeInt(feedItems.get().get(position).isRetweeted() ? 1 : 0);
            parcel.writeInt(feedItems.get().get(position).isFavorited() ? 1 : 0);
        } else {
            parcel.writeString(new String());
            parcel.writeString(feedItems.get().get(position).getCreated_at());
            parcel.writeLong(feedItems.get().get(position).getId());
            parcel.writeInt(position);
            parcel.writeString(feedItems.get().get(position).getText());
            parcel.writeString(feedItems.get().get(position).getUser().getName());
            parcel.writeString(feedItems.get().get(position).getUser().getScreen_name());
            parcel.writeString(feedItems.get().get(position).getUser().getProfile_image_url_https());
            parcel.writeInt(feedItems.get().get(position).getRetweet_count());
            parcel.writeInt(feedItems.get().get(position).getFavorite_count());
            parcel.writeInt(feedItems.get().get(position).isRetweeted() ? 1 : 0);
            parcel.writeInt(feedItems.get().get(position).isFavorited() ? 1 : 0);
        }
        parcel.setDataPosition(0);
        return parcel;
    }
}
