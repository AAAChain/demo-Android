package org.aaa.chain.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import org.aaa.chain.R;

public abstract class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<T> dataEntityList;
    private int normalType = 1;
    private int footType = 2;
    private boolean hasMore = false;   //have more data or not
    private boolean fadeTips = false; //have hide bottom prompt or not
    private int layoutId;

    BaseRecyclerViewAdapter(Context context, List<T> dataEntities, int layoutId) {
        this.context = context;
        this.dataEntityList = dataEntities;
        this.layoutId = layoutId;
    }

    //获取列表中数据源的最后一个位置，比getItemCount少1，因为不计上footView
    public int getRealLastPosition() {
        return dataEntityList.size();
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
                fadeTips = false;
                if (dataEntityList.size() > 0) {
                    ((FootViewHolder) holder).tvLoadMore.setText(context.getResources().getString(R.string.loading_more));
                }
            } else {
                if (dataEntityList.size() > 0) {
                    ((FootViewHolder) holder).tvLoadMore.setText(context.getResources().getString(R.string.no_more));

                    new Handler().postDelayed(new Runnable() {
                        @Override public void run() {
                            ((FootViewHolder) holder).tvLoadMore.setVisibility(View.GONE);
                            fadeTips = true;
                            hasMore = true;
                        }
                    }, 500);
                }
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

    public void resetDatas() {
        dataEntityList = new ArrayList<>();
    }

    public void updateList(List<T> dataEntities, boolean hasMore) {
        if (dataEntities != null) {
            dataEntityList.addAll(dataEntities);
        }
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
