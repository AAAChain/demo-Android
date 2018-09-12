package org.aaa.chain.views;

import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import org.aaa.chain.adapter.BaseRecyclerViewAdapter;
import org.aaa.chain.entities.DataEntity;

public class RecyclerScrollListener<T> extends RecyclerView.OnScrollListener {

    private int lastVisibleItem = 0;
    private final int PAGE_COUNT = 20;
    private BaseRecyclerViewAdapter adapter;
    private List<T> dataEntityList;

    public RecyclerScrollListener(List<T> dataEntities) {
        this.dataEntityList = dataEntities;
    }

    @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        adapter = (BaseRecyclerViewAdapter) recyclerView.getAdapter();
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            // 如果没有隐藏footView，那么最后一个条目的位置就比我们的getItemCount少1
            if (!adapter.isFadeTips() && lastVisibleItem + 1 == adapter.getItemCount()) {
                new Handler().postDelayed(() -> {
                    updateRecyclerView(adapter.getRealLastPosition(), adapter.getRealLastPosition() + PAGE_COUNT);
                }, 500);
            }

            // 如果隐藏了提示条，上拉加载时，那么最后一个条目就要比getItemCount要少1
            if (adapter.isFadeTips() && lastVisibleItem + 1 == adapter.getItemCount()) {
                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                        updateRecyclerView(adapter.getRealLastPosition(), adapter.getRealLastPosition() + PAGE_COUNT);
                    }
                }, 500);
            }
        }
    }

    @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        lastVisibleItem = layoutManager.findLastVisibleItemPosition();
    }

    private List<T> getDatas(final int firstIndex, final int lastIndex) {
        List<T> resList = new ArrayList<>();
        for (int i = firstIndex; i < lastIndex; i++) {
            if (i < dataEntityList.size()) {
                resList.add(dataEntityList.get(i));
            }
        }
        return resList;
    }

    private void updateRecyclerView(int fromIndex, int toIndex) {
        List<T> dataEntities = getDatas(fromIndex, toIndex);
        if (dataEntities.size() > 0) {
            adapter.updateList(dataEntities, true);
        } else {
            adapter.updateList(null, false);
        }
    }
}
