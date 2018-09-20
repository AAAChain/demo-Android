package org.aaa.chain.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;
import org.aaa.chain.R;

public abstract class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<T> dataEntityList;
    private int normalType = 1;
    private int footType = 2;
    private boolean hasMore = true;   //have more data or not
    private boolean fadeTips = true; //have hide bottom prompt or not
    private int layoutId;

    BaseRecyclerViewAdapter(Context context, List<T> dataEntities, int layoutId) {
        this.context = context;
        this.dataEntityList = dataEntities;
        this.layoutId = layoutId;
    }

    @NonNull @Override public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == normalType) {
            return new BaseViewHolder(LayoutInflater.from(context).inflate(layoutId, parent, false));
        } else {
            TextView textView = new TextView(context);
            return new FootViewHolder(textView);
        }
    }

    @Override public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof BaseViewHolder) {

            bindViewHolder((BaseViewHolder) holder, dataEntityList.get(position));
        } else {
            ((FootViewHolder) holder).tvLoadMore.setVisibility(View.VISIBLE);
            if (hasMore) {
                fadeTips = true;
                ((FootViewHolder) holder).tvLoadMore.setText(context.getResources().getString(R.string.loading_more));
            } else {
                fadeTips = false;
                ((FootViewHolder) holder).tvLoadMore.setText(context.getResources().getString(R.string.no_more));
            }
        }
    }

    @Override public int getItemCount() {
        return dataEntityList.size() + 1;
    }

    @Override public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return footType;
        } else {
            return normalType;
        }
    }

    public boolean isFadeTips() {
        return fadeTips;
    }

    public void updateList(boolean hasMore) {
        this.hasMore = hasMore;

        notifyDataSetChanged();
    }

    class FootViewHolder extends RecyclerView.ViewHolder {

        TextView tvLoadMore;

        FootViewHolder(View itemView) {
            super(itemView);
            tvLoadMore = (TextView) itemView;
        }
    }

    public abstract void bindViewHolder(BaseViewHolder holder, T t);
}
