package labut.md311.texter.view;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class OnScrollDownListener extends RecyclerView.OnScrollListener {

    private int prevTotalCurrentItems;
    private int thresholdLoad = 20;
    private long currentTop;
    private boolean loading = false;
    private boolean forceLoad = false;


    LinearLayoutManager layoutManager;

    public OnScrollDownListener(LinearLayoutManager layoutManager, int totalCurrentItems, long currentTop) {
        this.layoutManager = layoutManager;
        this.prevTotalCurrentItems = totalCurrentItems;
        this.currentTop = currentTop;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (currentTop > 0) {
            int lastBottomItem = layoutManager.findLastVisibleItemPosition();
            int totalCurrentItems = layoutManager.getItemCount();
            if (loading && (totalCurrentItems > prevTotalCurrentItems)) {
                loading = false;
                prevTotalCurrentItems = totalCurrentItems;
            }
            if ((!loading && lastBottomItem > (totalCurrentItems - thresholdLoad)) || (forceLoad && lastBottomItem == totalCurrentItems - 1)) {
                forceLoad = false;
                loading = true;
                loadPrevious();
            }
        }
    }

    void requestForceLoad() {
        this.forceLoad = true;
    }

    public void setCurrentTop(long currentTop) {
        this.currentTop = currentTop;
    }

    public abstract void loadPrevious();
}
