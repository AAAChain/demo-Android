package org.aaa.chain.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.squareup.leakcanary.RefWatcher;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
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

public class SearchResourceFragment extends BaseFragment implements BindViewHolderInterface<SearchResponseEntity.DocsResponse> {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SearchResponseEntity searchResponse;
    private int page = 1;

    private RelativeLayout rlSearch;
    private ImageView ivSearch;
    private EditText etSearch;
    private SimpleDateFormat simpleDateFormat;
    private BaseRecyclerViewAdapter adapter;

    private int lastVisibleItem = 0;
    private LinearLayoutManager manager;

    private List<SearchResponseEntity.DocsResponse> list = new ArrayList<>();

    @Override public int initLayout() {
        return R.layout.fragment_search_trans_item;
    }

    @Override public void getViewById() {
        simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        rlSearch = $(R.id.rl_search);
        rlSearch.setVisibility(View.VISIBLE);
        ivSearch = $(R.id.iv_search_icon);
        etSearch = $(R.id.et_search);
        ivSearch.setOnClickListener(this);
        recyclerView = $(R.id.recycleView);
        swipeRefreshLayout = $(R.id.swiperefreshlayout);
        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
        manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);

        adapter = new RecyclerViewAdapter<>(getActivity(), list, R.layout.recyclerview_item, this);
        recyclerView.setAdapter(adapter);

        initRecyclerView(true, null);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {
                initRecyclerView(true, null);
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                adapter = (BaseRecyclerViewAdapter) recyclerView.getAdapter();
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (adapter.isFadeTips() && lastVisibleItem + 1 == adapter.getItemCount()) {
                        initRecyclerView(false, null);
                    }
                }
            }

            @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = manager.findLastVisibleItemPosition();
            }
        });
    }

    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_search_icon:
                initRecyclerView(true, etSearch.getText().toString());
                break;
        }
    }

    private void initRecyclerView(boolean isFirst, String content) {
        if (isFirst) {
            page = 1;
            list.clear();
        }

        HttpUtils.getInstance().searchResource(page, content, new HttpUtils.ServerCallBack() {
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
                        list.addAll(searchResponse.getDocs());
                        getActivity().runOnUiThread(new Runnable() {
                            @Override public void run() {
                                if (page > 1 && page <= searchResponse.getPages()) {
                                    adapter.updateList(true);
                                } else if (page > searchResponse.getPages() || 1 == searchResponse.getPages()) {
                                    page = 1;
                                    adapter.updateList(false);
                                } else if (page == 1 && page < searchResponse.getPages()) {
                                    adapter.updateList(true);
                                }
                                page++;
                                swipeRefreshLayout.setRefreshing(false);
                                adapter.notifyDataSetChanged();
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override public void onResume() {
        super.onResume();
    }

    @Override public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = ChainApplication.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }

    @Override public void bindViewHolder(BaseViewHolder holder, SearchResponseEntity.DocsResponse dataEntity) {
        holder.getItemView().setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                startActivity(new Intent(getActivity(), ResumeDetailsActivity.class).putExtra("type", 0)
                        .putExtra("price", dataEntity.getExtra().getPrice())
                        .putExtra("name", dataEntity.getAccount())
                        .putExtra("hashId", dataEntity.getHashId()));
            }
        });
        ((TextView) holder.getView(R.id.tv_applicant_name)).setText(dataEntity.getAccount());
        ((TextView) holder.getView(R.id.tv_applicant_company)).setText(dataEntity.getExtra().getCompany());
        if (!TextUtils.isEmpty(dataEntity.getExtra().getStartTime())) {

            try {
                long startTime = simpleDateFormat.parse(dataEntity.getExtra().getStartTime()).getTime();
                long result = System.currentTimeMillis() - startTime;
                ((TextView) holder.getView(R.id.tv_applicant_work_hours)).setText(
                        String.format(getResources().getString(R.string.year), String.valueOf(result / 1000 / 3600 / 24 / 365)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            ((TextView) holder.getView(R.id.tv_applicant_work_hours)).setText(String.format(getResources().getString(R.string.year), "0"));
        }
        ((TextView) holder.getView(R.id.tv_applicant_position)).setText(dataEntity.getExtra().getJobType());
        ((TextView) holder.getView(R.id.tv_applicant_resume_attachment)).setText(dataEntity.getExtra().getName());
        ((TextView) holder.getView(R.id.tv_publish_time)).setText(dataEntity.getTimestamp().split("T")[0]);
    }
}
