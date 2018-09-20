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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import okhttp3.Call;
import okhttp3.Response;
import org.aaa.chain.Constant;
import org.aaa.chain.R;
import org.aaa.chain.adapter.BaseRecyclerViewAdapter;
import org.aaa.chain.adapter.BaseViewHolder;
import org.aaa.chain.adapter.BindViewHolderInterface;
import org.aaa.chain.adapter.RecyclerViewAdapter;
import org.aaa.chain.entities.OrderDataEntity;
import org.aaa.chain.entities.OrderResponseEntity;
import org.aaa.chain.utils.HttpUtils;

public class TransactionResourceFragment extends BaseFragment implements BindViewHolderInterface<OrderDataEntity> {

    private RecyclerView recyclerView;

    private SwipeRefreshLayout swipeRefreshLayout;

    private BaseRecyclerViewAdapter adapter;
    private List<OrderDataEntity> list = new ArrayList<>();

    @Override public int initLayout() {
        return R.layout.fragment_search_trans_item;
    }

    @Override public void getViewById() {
        $(R.id.iv_search_icon).setVisibility(View.GONE);
        recyclerView = $(R.id.recycleView);
        swipeRefreshLayout = $(R.id.swiperefreshlayout);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);

        initRecyclerView();

        adapter = new RecyclerViewAdapter<>(getActivity(), list, R.layout.recyclerview_msg_item, this);
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {

                initRecyclerView();
            }
        });
    }

    private void initRecyclerView() {

        HttpUtils.getInstance().getOrderList(new HttpUtils.ServerCallBack() {
            @Override public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), "order refresh onFailure:" + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            OrderResponseEntity orderResponseEntity = null;

            @Override public void onResponse(Call call, Response response) {

                if (response.code() == 200) {
                    try {
                        orderResponseEntity = new Gson().fromJson(response.body().string(), OrderResponseEntity.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(getActivity(), "order refresh successful:" + response.code(), Toast.LENGTH_SHORT).show();
                            for (OrderDataEntity orderDataEntity : orderResponseEntity.getData()) {
                                if (orderDataEntity.getSeller().equals(Constant.getAccount()) || orderDataEntity.getBuyer()
                                        .equals(Constant.getAccount())) {
                                    list.add(orderDataEntity);
                                }
                            }
                            adapter.updateList(false);
                            adapter.notifyDataSetChanged();
                        }
                    });
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override public void run() {
                            Toast.makeText(getActivity(), "order refresh failure:" + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    @Override public void onClick(View v) {

    }

    @Override public void bindViewHolder(BaseViewHolder holder, OrderDataEntity dataEntity) {
        if (dataEntity.getBuyer().equals(Constant.getAccount())) {
            ((TextView) holder.getView(R.id.tv_company_notice)).setText("waiting(" + dataEntity.getSeller() + ")authorization");
        } else {
            ((TextView) holder.getView(R.id.tv_company_notice)).setText("need(" + dataEntity.getSeller() + ")authorization");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(dataEntity.getCreateTime());
        ((TextView) holder.getView(R.id.tv_notice_time)).setText(sdf.format(date));
        ((TextView) holder.getView(R.id.tv_resume_price)).setText("price:" + dataEntity.getPrice() + "(AAA)");
        if (dataEntity.getStatus() == 0) {
            ((TextView) holder.getView(R.id.tv_resume_status)).setText("buyer created");
        } else if (dataEntity.getStatus() == 1) {
            if (dataEntity.getBuyer().equals(Constant.getAccount())) {
                ((TextView) holder.getView(R.id.tv_resume_status)).setText(getResources().getString(R.string.waiting_seller_authorization));
            } else {
                ((TextView) holder.getView(R.id.tv_resume_status)).setText(getResources().getString(R.string.waiting_buyer_authorization));
            }
        } else if (dataEntity.getStatus() == 2) {
            if (dataEntity.getBuyer().equals(Constant.getAccount())) {
                ((TextView) holder.getView(R.id.tv_resume_status)).setText(getResources().getString(R.string.waiting_receive));
            } else {
                ((TextView) holder.getView(R.id.tv_resume_status)).setText(getResources().getString(R.string.authorization_successful));
            }
        } else {
            if (dataEntity.getBuyer().equals(Constant.getAccount())) {
                ((TextView) holder.getView(R.id.tv_resume_status)).setText(getResources().getString(R.string.resume_received));
            } else {
                ((TextView) holder.getView(R.id.tv_resume_status)).setText(getResources().getString(R.string.transaction_done));
            }
        }

        holder.getItemView().setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                startActivity(new Intent(getActivity(), ResumeDetailsActivity.class).putExtra("type", 1).putExtra("dataEntity", dataEntity));
            }
        });
    }
}
