package org.aaa.chain.adapter;

import android.content.Context;
import java.util.List;

public class RecyclerViewAdapter<T> extends BaseRecyclerViewAdapter<T> {

    private BindViewHolderInterface<T> bindViewHolderInterface;

    public RecyclerViewAdapter(Context context, List<T> dataEntities, int layoutId,BindViewHolderInterface<T> bindViewHolderInterface) {
        super(context, dataEntities, layoutId);
        this.bindViewHolderInterface = bindViewHolderInterface;
    }

    @Override public void bindViewHolder(BaseViewHolder holder, T t) {
        bindViewHolderInterface.bindViewHolder(holder,t);
    }
}
