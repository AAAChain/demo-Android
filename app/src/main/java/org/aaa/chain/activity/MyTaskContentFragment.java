package org.aaa.chain.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.squareup.leakcanary.RefWatcher;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Response;
import org.aaa.chain.ChainApplication;
import org.aaa.chain.R;
import org.aaa.chain.adapter.BaseRecyclerViewAdapter;
import org.aaa.chain.adapter.BaseViewHolder;
import org.aaa.chain.adapter.BindViewHolderInterface;
import org.aaa.chain.adapter.RecyclerViewAdapter;
import org.aaa.chain.entities.SearchResponseEntity;
import org.aaa.chain.utils.HttpUtils;

public class MyTaskContentFragment extends BaseFragment {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SearchResponseEntity searchResponse;
    private int page = 1;

    @Override public int initLayout() {
        return R.layout.fragment_tablayout_content;
    }

    @Override public void getViewById() {
        recyclerView = $(R.id.recycleView);
        swipeRefreshLayout = $(R.id.swiperefreshlayout);
        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);

        initRecyclerView();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {
                initRecyclerView();
            }
        });
    }

    @Override public void onClick(View v) {

    }

    private void initRecyclerView() {

        new Thread(new Runnable() {
            @Override public void run() {
                HttpUtils.getInstance().searchResource(page, new HttpUtils.ServerCallBack() {
                    @Override public void onFailure(Call call, IOException e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override public void run() {
                                swipeRefreshLayout.setRefreshing(false);
                                Toast.makeText(getActivity(), getResources().getString(R.string.get_resource_failure), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override public void onResponse(Call call, Response response) {

                        if (response.code() == 200) {

                            try {
                                searchResponse = new Gson().fromJson(response.body().string(), SearchResponseEntity.class);
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override public void run() {
                                        swipeRefreshLayout.setRefreshing(false);
                                        BaseRecyclerViewAdapter adapter =
                                                new RecyclerViewAdapter<>(getActivity(), searchResponse.getDocs(), R.layout.recyclerview_item,
                                                        new BindViewHolderInterface<SearchResponseEntity.DocsResponse>() {
                                                            @Override public void bindViewHolder(BaseViewHolder holder,
                                                                    SearchResponseEntity.DocsResponse dataEntity) {
                                                                holder.getItemView().setOnClickListener(new View.OnClickListener() {
                                                                    @Override public void onClick(View v) {
                                                                        startActivity(new Intent(getActivity(), ResumeDetailsActivity.class).putExtra(
                                                                                "type", 0)
                                                                                .putExtra("price", dataEntity.getExtra().getPrice())
                                                                                .putExtra("hashId", dataEntity.getHashId()));
                                                                    }
                                                                });
                                                                ((TextView) holder.getView(R.id.tv_applicant_name)).setText(dataEntity.getAccount());
                                                                ((TextView) holder.getView(R.id.tv_applicant_company)).setText(
                                                                        dataEntity.getExtra().getCompany());
                                                                ((TextView) holder.getView(R.id.tv_applicant_work_hours)).setText(
                                                                        dataEntity.getExtra().getWorkours());
                                                                ((TextView) holder.getView(R.id.tv_applicant_work_range)).setText(
                                                                        dataEntity.getExtra().getName());
                                                                ((TextView) holder.getView(R.id.tv_publish_time)).setText(
                                                                        dataEntity.getTimestamp().split("T")[0]);
                                                            }
                                                        });
                                        recyclerView.setAdapter(adapter);
                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }).start();
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
