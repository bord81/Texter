package labut.md311.texter.view;


import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import labut.md311.texter.R;
import labut.md311.texter.retrofit.RetweetedItem;

public class RowHolder extends RecyclerView.ViewHolder {
    private ImageView avatar = null;
    private TextView retweeted = null;
    private TextView header = null;
    private TextView header_at = null;
    private TextView created_at = null;
    private TextView body = null;
    private TextView retwCount = null;
    private TextView likesCount = null;
    private ImageView retwIcon = null;
    private ImageView likesIcon = null;
    private OnItemClickListener onItemClickListener = null;

    private static final String RETW_BY = "Retweeted by ";


    public RowHolder(View itemView, OnItemClickListener onClickListener) {
        super(itemView);
        avatar = (ImageView) itemView.findViewById(R.id.img_avatar);
        retweeted = (TextView) itemView.findViewById(R.id.text_view_retweeted);
        header = (TextView) itemView.findViewById(R.id.text_view_header);
        header_at = (TextView) itemView.findViewById(R.id.text_view_header_at);
        created_at = (TextView) itemView.findViewById(R.id.text_view_created_at);
        body = (TextView) itemView.findViewById(R.id.text_view_body);
        retwCount = (TextView) itemView.findViewById(R.id.text_view_retweets_count);
        likesCount = (TextView) itemView.findViewById(R.id.text_view_likes_count);
        retwIcon = (ImageView) itemView.findViewById(R.id.img_retweets);
        likesIcon = (ImageView) itemView.findViewById(R.id.img_likes);
        onItemClickListener = onClickListener;
        retwCount.setOnClickListener(v -> {
            onItemClickListener.onItemClick(v, RecAdapter.RETWEET_VIEW, getAdapterPosition());
        });
        retwIcon.setOnClickListener(v -> {
            onItemClickListener.onItemClick(v, RecAdapter.RETWEET_VIEW, getAdapterPosition());
        });
        likesCount.setOnClickListener(v -> {
            onItemClickListener.onItemClick(v, RecAdapter.LIKE_VIEW, getAdapterPosition());
        });
        likesIcon.setOnClickListener(v -> {
            onItemClickListener.onItemClick(v, RecAdapter.LIKE_VIEW, getAdapterPosition());
        });
        body.setOnClickListener(v -> {
            onItemClickListener.onItemClick(v, RecAdapter.BODY_VIEW, getAdapterPosition());
        });
        header.setOnClickListener(v -> {
            onItemClickListener.onItemClick(v, RecAdapter.BODY_VIEW, getAdapterPosition());
        });
        header_at.setOnClickListener(v -> {
            onItemClickListener.onItemClick(v, RecAdapter.BODY_VIEW, getAdapterPosition());
        });
        created_at.setOnClickListener(v -> {
            onItemClickListener.onItemClick(v, RecAdapter.BODY_VIEW, getAdapterPosition());
        });
        avatar.setOnClickListener(v -> {
            onItemClickListener.onItemClick(v, RecAdapter.BODY_VIEW, getAdapterPosition());
        });
    }

    void bindModel(Context context,
                   String url,
                   String header,
                   String header_at,
                   String created,
                   String body,
                   int retweets,
                   int likes,
                   boolean isFavorited,
                   boolean isRetweeted,
                   RetweetedItem retweetedItem,
                   boolean stub) {
        if (!stub) {
            if (retweetedItem == null) {
                Picasso.with(context).load(url).into(avatar);
                if (retweeted.getVisibility() == View.VISIBLE)
                    retweeted.setVisibility(View.GONE);
                String at = "@" + header_at;
                String date = created.substring(4, 10);
                this.created_at.setText(date);
                this.header.setText(header);
                this.header_at.setText(at);
                this.body.setText(body);
                this.retwCount.setText(String.valueOf(retweets));
                this.likesCount.setText(String.valueOf(likes));
            } else {
                Picasso.with(context).load(retweetedItem.getUser().getProfile_image_url_https()).into(avatar);
                String retwBy = RETW_BY + header;
                retweeted.setText(retwBy);
                retweeted.setVisibility(View.VISIBLE);
                String at = "@" + retweetedItem.getUser().getScreen_name();
                String date = retweetedItem.getCreated_at().substring(4, 10);
                this.created_at.setText(date);
                this.header.setText(retweetedItem.getUser().getName());
                this.header_at.setText(at);
                this.body.setText(retweetedItem.getText());
                this.retwCount.setText(String.valueOf(retweetedItem.getRetweet_count()));
                this.likesCount.setText(String.valueOf(retweetedItem.getFavorite_count()));
            }
            this.likesCount.setTag(R.id.img_likes, isFavorited);
            this.likesIcon.setTag(R.id.img_likes, isFavorited);
            this.retwCount.setTag(R.id.img_retweets, isRetweeted);
            this.retwIcon.setTag(R.id.img_retweets, isRetweeted);
            if (isFavorited) {
                likesIcon.setColorFilter(Color.BLACK);
            } else {
                likesIcon.clearColorFilter();
            }
            if (isRetweeted) {
                retwIcon.setColorFilter(Color.BLACK);
            } else {
                retwIcon.clearColorFilter();
            }
            if (!likesIcon.isShown()) {
                switchIconsVisibility(View.VISIBLE);
            }
        } else {
            switchIconsVisibility(View.GONE);
        }
    }

    private void switchIconsVisibility(int visibility) {
        this.likesIcon.setVisibility(visibility);
        this.retwIcon.setVisibility(visibility);
    }
}
