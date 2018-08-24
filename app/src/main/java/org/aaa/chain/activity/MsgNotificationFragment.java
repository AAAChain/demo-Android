package org.aaa.chain.activity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.aaa.chain.R;
import org.aaa.chain.adapter.BaseRecyclerViewAdapter;
import org.aaa.chain.adapter.BaseViewHolder;
import org.aaa.chain.adapter.BindViewHolderInterface;
import org.aaa.chain.adapter.RecyclerViewAdapter;
import org.aaa.chain.entities.DataEntity;

public class MsgNotificationFragment extends BaseFragment {

    private RecyclerView recyclerView;
    private List<DataEntity> dataEntities = new ArrayList<>();

    @Override public int initLayout() {
        return R.layout.fragment_tablayout_content;
    }

    @Override public void getViewById() {
        recyclerView = $(R.id.recycleView);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);

        dataEntities.clear();
        for (int i = 0; i < 50; i++) {
            DataEntity entity = new DataEntity();
            entity.setDescription("test" + i);
            entity.setTitle("hello" + i);
            dataEntities.add(entity);
        }
        BaseRecyclerViewAdapter adapter =
                new RecyclerViewAdapter<>(getActivity(), dataEntities, R.layout.recyclerview_msg_item, new BindViewHolderInterface<DataEntity>() {
                    @Override public void bindViewHolder(BaseViewHolder holder, DataEntity dataEntity) {
                        holder.getItemView().setOnClickListener(new View.OnClickListener() {
                            @Override public void onClick(View v) {
                                startActivity(new Intent(getActivity(), ResumeDetailsActivity.class).putExtra("type", 1));
                            }
                        });
                    }
                });
        recyclerView.setAdapter(adapter);
    }

    @Override public void onClick(View v) {

    }
}
