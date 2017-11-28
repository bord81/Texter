package labut.md311.texter.view;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import labut.md311.texter.R;
import labut.md311.texter.retrofit.FeedItem;

public class RecAdapter extends RecyclerView.Adapter<RowHolder> {
    private Context context;
    private ArrayList<FeedItem> feedItems;
    private boolean ifStub = false;
    private OnItemClickListener onClickListener = null;
    public final static int BODY_VIEW = 0;
    public final static int RETWEET_VIEW = 1;
    public final static int LIKE_VIEW = 2;
    public final static int MAIL_VIEW = 3;


    public RecAdapter() {
    }

    public RecAdapter(Context context, ArrayList<FeedItem> feedItems, boolean stub, OnItemClickListener onClickListener) {
        this.context = context;
        this.feedItems = feedItems;
        this.ifStub = stub;
        this.onClickListener = onClickListener;
    }

    public void updateFeedItems(ArrayList<FeedItem> feedItems) {
        this.feedItems = feedItems;
        ifStub = false;
    }

    @Override
    public RowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RowHolder(LayoutInflater.from(context).inflate(R.layout.row, parent, false), onClickListener);
    }

    @Override
    public void onBindViewHolder(RowHolder holder, int position) {
        holder.bindModel(context,
                feedItems.get(position).getUser().getProfile_image_url_https(),
                feedItems.get(position).getUser().getName(),
                feedItems.get(position).getUser().getScreen_name(),
                feedItems.get(position).getCreated_at(),
                feedItems.get(position).getText(),
                feedItems.get(position).getRetweet_count(),
                feedItems.get(position).getFavorite_count(),
                feedItems.get(position).isFavorited(),
                feedItems.get(position).isRetweeted(),
                feedItems.get(position).getRetweeted_status(),
                ifStub);
    }

    @Override
    public int getItemCount() {
        return feedItems.size();
    }


}
