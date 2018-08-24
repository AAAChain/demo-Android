package org.aaa.chain.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

public class BaseViewHolder extends RecyclerView.ViewHolder {

    private SparseArray<View> mViews;
    private View itemView;

    BaseViewHolder(View itemView) {
        super(itemView);
        mViews = new SparseArray<>();
        this.itemView = itemView;
    }

    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);

        if (view == null) {
            view = itemView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public View getItemView(){
        return itemView;
    }
}
