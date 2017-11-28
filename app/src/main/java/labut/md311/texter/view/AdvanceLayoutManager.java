package labut.md311.texter.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

public class AdvanceLayoutManager extends LinearLayoutManager {
    private static final int EXTRA_SPACE = 600;

    public AdvanceLayoutManager(Context context) {
        super(context);
    }

    public AdvanceLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public AdvanceLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected int getExtraLayoutSpace(RecyclerView.State state) {
        return EXTRA_SPACE;
    }
}
