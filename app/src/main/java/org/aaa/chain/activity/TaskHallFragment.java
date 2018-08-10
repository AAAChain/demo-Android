package org.aaa.chain.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.squareup.leakcanary.RefWatcher;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.aaa.chain.IpfsApplication;
import org.aaa.chain.R;
import org.aaa.chain.adapter.BaseViewHolder;
import org.aaa.chain.adapter.BindViewHolderInterface;
import org.aaa.chain.adapter.RecyclerViewAdapter;
import org.aaa.chain.views.RecyclerScrollListener;
import org.aaa.chain.adapter.BaseRecyclerViewAdapter;
import org.aaa.chain.entities.DataEntity;

public class TaskHallFragment extends BaseFragment {

    private RecyclerView recyclerView;
    private List<DataEntity> dataEntities = new ArrayList<>();

    LinearLayoutManager manager;
    BaseRecyclerViewAdapter adapter;

    @Override public int initLayout() {
        return R.layout.fragment_task_hall;
    }

    @Override public void getViewById() {

        ((BaseActivity) Objects.requireNonNull(getActivity())).back.setVisibility(View.GONE);
        ((BaseActivity) Objects.requireNonNull(getActivity())).setTitleName(getResources().getString(R.string.task_hall_title_name));

        recyclerView = $(R.id.recycleView);
        manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        dataEntities.clear();
        for (int i = 0; i < 10; i++) {
            DataEntity entity = new DataEntity();
            entity.setDescription("task" + i);
            entity.setTitle("hello" + i);
            dataEntities.add(entity);
        }
        adapter = new RecyclerViewAdapter<>(getActivity(), dataEntities, R.layout.recyclerview_item, new BindViewHolderInterface<DataEntity>() {
            @Override public void bindViewHolder(BaseViewHolder holder, DataEntity dataEntity) {
                ((TextView) holder.getView(R.id.tv_item_description)).setText(dataEntity.getDescription());
                ((TextView) holder.getView(R.id.tv_item_title)).setText(dataEntity.getTitle());
                holder.getView(R.id.btn_item_entry).setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        ((BaseActivity) Objects.requireNonNull(getActivity())).startActivity(ProjectDetailActivity.class, null);
                    }
                });
            }
        });
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerScrollListener(dataEntities));
    }

    @Override public void onClick(View v) {

    }

    @Override public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = IpfsApplication.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }
}
