package org.aaa.chain.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import okhttp3.Call;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.aaa.chain.Constant;
import org.aaa.chain.R;
import org.aaa.chain.adapter.BaseRecyclerViewAdapter;
import org.aaa.chain.adapter.BaseViewHolder;
import org.aaa.chain.adapter.BindViewHolderInterface;
import org.aaa.chain.adapter.RecyclerViewAdapter;
import org.aaa.chain.entities.OrderResponseEntity;
import org.aaa.chain.utils.HttpUtils;

public class MsgNotificationFragment extends BaseFragment {

    private RecyclerView recyclerView;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override public int initLayout() {
        return R.layout.fragment_tablayout_content;
    }

    @Override public void getViewById() {
        recyclerView = $(R.id.recycleView);
        swipeRefreshLayout = $(R.id.swiperefreshlayout);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);

        initRecyclerView();

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
                            List<OrderResponseEntity.OrderDataEntity> orderDataEntityList = new ArrayList<>();
                            for (OrderResponseEntity.OrderDataEntity orderDataEntity : orderResponseEntity.getData()) {
                                if (orderDataEntity.getSeller().equals(Constant.getAccount()) || orderDataEntity.getBuyer()
                                        .equals(Constant.getAccount())) {
                                    orderDataEntityList.add(orderDataEntity);
                                }
                            }
                            BaseRecyclerViewAdapter adapter =
                                    new RecyclerViewAdapter<>(getActivity(), orderDataEntityList, R.layout.recyclerview_msg_item,
                                            new BindViewHolderInterface<OrderResponseEntity.OrderDataEntity>() {
                                                @Override
                                                public void bindViewHolder(BaseViewHolder holder, OrderResponseEntity.OrderDataEntity dataEntity) {
                                                    if (dataEntity.getBuyer().equals(Constant.getAccount())) {
                                                        ((TextView) holder.getView(R.id.tv_company_notice)).setText(
                                                                "等待" + dataEntity.getSeller() + "对简历授权");
                                                    } else {
                                                        ((TextView) holder.getView(R.id.tv_company_notice)).setText(
                                                                "需要您(" + dataEntity.getSeller() + ")对简历授权");
                                                    }

                                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                                    Date date = new Date(dataEntity.getCreateTime());
                                                    ((TextView) holder.getView(R.id.tv_notice_time)).setText(sdf.format(date));
                                                    ((TextView) holder.getView(R.id.tv_resume_price)).setText(
                                                            "price:" + dataEntity.getPrice() + "(AAA)");
                                                    if (dataEntity.getStatus() == 0) {
                                                        ((TextView) holder.getView(R.id.tv_resume_status)).setText("buyer created");
                                                    } else if (dataEntity.getStatus() == 1) {
                                                        if (dataEntity.getBuyer().equals(Constant.getAccount())) {
                                                            ((TextView) holder.getView(R.id.tv_resume_status)).setText(
                                                                    getResources().getString(R.string.waiting_seller_authorization));
                                                        } else {
                                                            ((TextView) holder.getView(R.id.tv_resume_status)).setText(
                                                                    getResources().getString(R.string.waiting_buyer_authorization));
                                                        }
                                                    } else if (dataEntity.getStatus() == 2) {
                                                        if (dataEntity.getBuyer().equals(Constant.getAccount())) {
                                                            ((TextView) holder.getView(R.id.tv_resume_status)).setText(
                                                                    getResources().getString(R.string.waiting_receive));
                                                            ((TextView) holder.getView(R.id.tv_resume_status)).setOnClickListener(
                                                                    new View.OnClickListener() {
                                                                        @Override public void onClick(View v) {
                                                                            //1.下载文件
                                                                            HttpUtils.getInstance()
                                                                                    .downlaodFile(dataEntity.getGoodId(),
                                                                                            new HttpUtils.ServerCallBack() {
                                                                                                @Override
                                                                                                public void onFailure(Call call, IOException e) {
                                                                                                    getActivity().runOnUiThread(new Runnable() {
                                                                                                        @Override public void run() {
                                                                                                            Toast.makeText(getActivity(),
                                                                                                                    getResources().getString(
                                                                                                                            R.string.download_failure)
                                                                                                                            + e.toString(),
                                                                                                                    Toast.LENGTH_SHORT).show();
                                                                                                        }
                                                                                                    });
                                                                                                }

                                                                                                @Override
                                                                                                public void onResponse(Call call, Response response) {
                                                                                                    if (response.code() == 200) {

                                                                                                        ResponseBody body = response.body();
                                                                                                        String tempPath =
                                                                                                                getActivity().getExternalCacheDir()
                                                                                                                        .getAbsolutePath()
                                                                                                                        + "/cyq.txt";
                                                                                                        File tempFile = new File(tempPath);
                                                                                                        try {
                                                                                                            OutputStream out =
                                                                                                                    new FileOutputStream(tempFile);
                                                                                                            out.write(body.bytes(), 0,
                                                                                                                    body.bytes().length);
                                                                                                        } catch (FileNotFoundException e) {
                                                                                                            e.printStackTrace();
                                                                                                        } catch (IOException e) {
                                                                                                            e.printStackTrace();
                                                                                                        }

                                                                                                        try {
                                                                                                            FileInputStream in =
                                                                                                                    new FileInputStream(tempFile);
                                                                                                            InputStreamReader inputStreamReader =
                                                                                                                    new InputStreamReader(in);
                                                                                                            int i = 0;
                                                                                                            while ((i = inputStreamReader.read())
                                                                                                                    != -1) {
                                                                                                                Log.i("info",
                                                                                                                        "file content:" + (char) i);
                                                                                                            }
                                                                                                        } catch (FileNotFoundException e) {
                                                                                                            e.printStackTrace();
                                                                                                        } catch (IOException e) {
                                                                                                            e.printStackTrace();
                                                                                                        }
                                                                                                        //2.交易完成
                                                                                                        HttpUtils.getInstance()
                                                                                                                .updateOrderStatus(dataEntity.getId(),
                                                                                                                        getResources().getString(
                                                                                                                                R.string.transaction_done),
                                                                                                                        HttpUtils.FINAL_PAYED,
                                                                                                                        new HttpUtils.ServerCallBack() {
                                                                                                                            @Override
                                                                                                                            public void onFailure(
                                                                                                                                    Call call,
                                                                                                                                    IOException e) {
                                                                                                                                getActivity().runOnUiThread(
                                                                                                                                        new Runnable() {
                                                                                                                                            @Override
                                                                                                                                            public void run() {
                                                                                                                                                Toast.makeText(
                                                                                                                                                        getActivity(),
                                                                                                                                                        getResources()
                                                                                                                                                                .getString(
                                                                                                                                                                        R.string.authorization_failure),
                                                                                                                                                        Toast.LENGTH_SHORT)
                                                                                                                                                        .show();
                                                                                                                                            }
                                                                                                                                        });
                                                                                                                            }

                                                                                                                            @Override
                                                                                                                            public void onResponse(
                                                                                                                                    Call call,
                                                                                                                                    Response response) {
                                                                                                                                if (response.code()
                                                                                                                                        == 200) {
                                                                                                                                    getActivity().runOnUiThread(
                                                                                                                                            new Runnable() {
                                                                                                                                                @Override
                                                                                                                                                public void run() {
                                                                                                                                                    Toast.makeText(
                                                                                                                                                            getActivity(),
                                                                                                                                                            getResources()
                                                                                                                                                                    .getString(
                                                                                                                                                                            R.string.transaction_done),
                                                                                                                                                            Toast.LENGTH_SHORT)
                                                                                                                                                            .show();
                                                                                                                                                }
                                                                                                                                            });
                                                                                                                                }
                                                                                                                            }
                                                                                                                        });
                                                                                                    }
                                                                                                }
                                                                                            });
                                                                        }
                                                                    });
                                                        } else {
                                                            ((TextView) holder.getView(R.id.tv_resume_status)).setText(
                                                                    getResources().getString(R.string.authorization_successful));
                                                        }
                                                    } else {
                                                        if (dataEntity.getBuyer().equals(Constant.getAccount())) {
                                                            ((TextView) holder.getView(R.id.tv_resume_status)).setText(
                                                                    getResources().getString(R.string.resume_received));
                                                        } else {
                                                            ((TextView) holder.getView(R.id.tv_resume_status)).setText(
                                                                    getResources().getString(R.string.transaction_done));
                                                        }
                                                    }

                                                    holder.getItemView().setOnClickListener(new View.OnClickListener() {
                                                        @Override public void onClick(View v) {
                                                            startActivity(new Intent(getActivity(), ResumeDetailsActivity.class).putExtra("type", 1)
                                                                    .putExtra("id", dataEntity.getId())
                                                                    .putExtra("status", dataEntity.getStatus())
                                                                    .putExtra("buyer",dataEntity.getBuyer())
                                                                    .putExtra("goodId", dataEntity.getGoodId()));
                                                        }
                                                    });
                                                }
                                            });
                            recyclerView.setAdapter(adapter);
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
}
