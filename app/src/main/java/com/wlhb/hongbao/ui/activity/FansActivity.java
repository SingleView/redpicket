package com.wlhb.hongbao.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.wlhb.administrator.hongbao.R;
import com.wlhb.hongbao.app.App;
import com.wlhb.hongbao.base.BaseActivity;
import com.wlhb.hongbao.bean.LikeLists;
import com.wlhb.hongbao.bean.Myblack;
import com.wlhb.hongbao.http.BaseCallback;
import com.wlhb.hongbao.http.BaseResponse;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Administrator on 2018/4/4/004.
 */

public class FansActivity extends BaseActivity {
    @BindView(R.id.rv_fans)
    RecyclerView rvFensi;
    @BindView(R.id.rl_nomore)
    RelativeLayout rlNomore;
    private BaseQuickAdapter<LikeLists.DataListBean, BaseViewHolder> mAdapter;
    private Button guanzhu;

    @Override
    public View loadView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.activity_fans, container, false);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setTitle("我的粉丝");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
        initAdapter();
        init();
    }

    private void init() {
        rvFensi.setLayoutManager(new LinearLayoutManager(this));
        //获取粉丝列表
        Call<BaseResponse<LikeLists>> calltradingdetails = service.likeLists(App.token, 1);
        calltradingdetails.enqueue(new BaseCallback<BaseResponse<LikeLists>>(calltradingdetails, this) {
            @Override
            public void onResponse(Response<BaseResponse<LikeLists>> response) {
                BaseResponse<LikeLists> body = response.body();
                if (body.isOK()) {
                    if (body.data.dataList != null && !body.data.dataList.isEmpty()) {
                        rlNomore.setVisibility(View.GONE);
                        rvFensi.setVisibility(View.VISIBLE);
                    } else {
                        rlNomore.setVisibility(View.VISIBLE);
                        rvFensi.setVisibility(View.GONE);
                    }
                    mAdapter.setNewData(body.data.dataList);
                    rvFensi.setAdapter(mAdapter);

                } else {
                    showToast(body.message);
                }
            }
        });

    }

    private void initAdapter() {
        //设置我的粉丝适配器
        mAdapter = new BaseQuickAdapter<LikeLists.DataListBean, BaseViewHolder>(R.layout.item_wodefensi) {

            @Override
            protected void convert(BaseViewHolder helper, final LikeLists.DataListBean item) {
                CircleImageView ivTouxiang = helper.getView(R.id.iv_touxiang);
                showImageTx(item.image, ivTouxiang);
                helper.setText(R.id.tv_neirong, item.personalMark + "");
                helper.setText(R.id.tv_name, item.username + "");
                helper.getView(R.id.bt_guanzhu).setOnClickListener(new View.OnClickListener() {
                    @Override
                    //点击关注粉丝
                    public void onClick(View view) {
                        int id = item.id;
                        Call<BaseResponse<JSON>> callmemberlike = service.memberlike(App.token,id);
                        callmemberlike.enqueue(new BaseCallback<BaseResponse<JSON>>(callmemberlike, FansActivity.this) {
                            @Override
                            public void onResponse(Response<BaseResponse<JSON>> response) {
                                BaseResponse<JSON> body = response.body();
                                if (body.isOK()) {
                                    init();
                                    showToast(body.message);
                                }
                            }
                        });
                    }
                });

            }
        };
    }
}
