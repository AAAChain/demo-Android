package org.aaa.chain.activity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.squareup.leakcanary.RefWatcher;
import java.util.ArrayList;
import java.util.List;
import org.aaa.chain.ChainApplication;
import org.aaa.chain.R;
import org.aaa.chain.adapter.BaseRecyclerViewAdapter;
import org.aaa.chain.adapter.BaseViewHolder;
import org.aaa.chain.adapter.BindViewHolderInterface;
import org.aaa.chain.adapter.RecyclerViewAdapter;
import org.aaa.chain.entities.DataEntity;

public class MyTaskContentFragment extends BaseFragment {

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
                new RecyclerViewAdapter<>(getActivity(), dataEntities, R.layout.recyclerview_item, new BindViewHolderInterface<DataEntity>() {
                    @Override public void bindViewHolder(BaseViewHolder holder, DataEntity dataEntity) {
                        holder.getItemView().setOnClickListener(new View.OnClickListener() {
                            @Override public void onClick(View v) {
                                startActivity(new Intent(getActivity(),ResumeDetailsActivity.class).putExtra("type",0));
                            }
                        });
                        //((TextView) holder.getView(R.id.tv_item_description)).setText(dataEntity.getDescription());
                        //((TextView) holder.getView(R.id.tv_item_title)).setText(dataEntity.getTitle());
                        //holder.getView(R.id.btn_item_entry).setOnClickListener(new View.OnClickListener() {
                        //    @Override public void onClick(View v) {
                        //        ((BaseActivity) Objects.requireNonNull(getActivity())).startActivity(CreateSmallResumeActivity.class, null);
                        //    }
                        //});
                    }
                });
        recyclerView.setAdapter(adapter);

    }

    @Override public void onClick(View v) {

    }

    @Override public void onResume() {
        super.onResume();
    }

    @Override public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = ChainApplication.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }
}
