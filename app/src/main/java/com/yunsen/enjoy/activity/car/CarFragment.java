package com.yunsen.enjoy.activity.car;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yunsen.enjoy.R;
import com.yunsen.enjoy.activity.car.adapter.DShoppingCarAdapter;
import com.yunsen.enjoy.activity.mine.MyOrderConfrimActivity;
import com.yunsen.enjoy.common.SpConstants;
import com.yunsen.enjoy.fragment.BaseFragment;
import com.yunsen.enjoy.http.HttpCallBack;
import com.yunsen.enjoy.http.HttpProxy;
import com.yunsen.enjoy.model.GoodsCarInfo;
import com.yunsen.enjoy.model.OrderInfo;
import com.yunsen.enjoy.model.ShopCarCount;
import com.yunsen.enjoy.ui.UIHelper;
import com.yunsen.enjoy.ui.recyclerview.HeaderAndFooterRecyclerViewAdapter;
import com.yunsen.enjoy.utils.StringUtils;
import com.yunsen.enjoy.utils.ToastUtils;
import com.yunsen.enjoy.widget.recyclerview.MultiItemTypeAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Request;

/**
 * Created by Administrator on 2018/5/17.
 */

public class CarFragment extends BaseFragment implements MultiItemTypeAdapter.OnItemClickListener, DShoppingCarAdapter.GoodsSumInterface, CompoundButton.OnCheckedChangeListener {

    @Bind(R.id.action_back)
    ImageView actionBack;
    @Bind(R.id.action_bar_title)
    TextView actionBarTitle;
    @Bind(R.id.action_bar_right)
    ImageView actionBarRight;
    @Bind(R.id.shop_car_recycler)
    RecyclerView shopCarRecycler;
    @Bind(R.id.goods_all_size)
    TextView goodsAllSize;
    @Bind(R.id.goods_all_price)
    TextView goodsAllPrice;
    @Bind(R.id.change_goods_btn)
    Button changeGoodsBtn;
    @Bind(R.id.no_goods_layout)
    LinearLayout noGoodsLayout;
    @Bind(R.id.has_goods_layout)
    FrameLayout hasGoodsLayout;
    private ArrayList<GoodsCarInfo> mDatas;
    private DShoppingCarAdapter mAdapter;
    private CheckBox checkAllGoods;
    private int mPageIndex = 1;
    private String mUserId;
    private boolean mFlagLoad = true;//第一次打开不走onResumen 刷新界面

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_car;
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this, rootView);
        actionBarTitle.setText("换物车");
        actionBack.setVisibility(View.GONE);
        shopCarRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    protected void initData() {
        SharedPreferences sp = getActivity().getSharedPreferences(SpConstants.SP_LONG_USER_SET_USER, Context.MODE_PRIVATE);
        mUserId = sp.getString(SpConstants.USER_ID, "");
        mDatas = new ArrayList<>();
        mAdapter = new DShoppingCarAdapter(getActivity(), R.layout.car_goods_item, mDatas, mUserId);
        HeaderAndFooterRecyclerViewAdapter headerAndFooterRecyclerViewAdapter = new HeaderAndFooterRecyclerViewAdapter(mAdapter);
        shopCarRecycler.setAdapter(headerAndFooterRecyclerViewAdapter);
        View headView = getLayoutInflater().inflate(R.layout.shop_car_head_view, null);
        checkAllGoods = ((CheckBox) headView.findViewById(R.id.select_all_goods_cb));
        headerAndFooterRecyclerViewAdapter.addHeaderView(headView);
    }

    @Override
    protected void initListener() {
        mAdapter.setOnItemClickListener(this);
        mAdapter.setGoodsSumCall(this);
        checkAllGoods.setOnCheckedChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mFlagLoad) {
            mFlagLoad = false;
        } else {
            requestData();
        }
    }

    @Override
    protected void requestData() {
        HttpProxy.getMyShoppingCart(mUserId, String.valueOf(mPageIndex), new HttpCallBack<List<GoodsCarInfo>>() {
            @Override
            public void onSuccess(List<GoodsCarInfo> responseData) {
                mAdapter.upData(responseData);
                isShowEmptyView(mDatas.size() == 0);
            }

            @Override
            public void onError(Request request, Exception e) {

            }
        });
    }


    @OnClick(R.id.change_goods_btn)
    public void onViewClicked() {
        if (mAdapter == null) {
            return;
        }
        if (mAdapter.getmGoodsCount() == 0) {
            Toast.makeText(getActivity(), "请选择要支付的商品", Toast.LENGTH_SHORT).show();
        } else {
            submitBuyGoods();
        }
    }

    private void submitBuyGoods() {
        String[] requestDatas = mAdapter.getSubmitRequestDatas();
        HttpProxy.submitShoppingGoods(requestDatas[0], requestDatas[1], requestDatas[2], new HttpCallBack<OrderInfo>() {
            @Override
            public void onSuccess(OrderInfo responseData) {
                String buyNo = responseData.getBuy_no();
                UIHelper.showMyOrderConfrimActivity(getActivity(), buyNo);
            }

            @Override
            public void onError(Request request, Exception e) {
                ToastUtils.makeTextShort(e.getMessage());
            }
        });

    }

    @Override
    public void onItemClick(View view, RecyclerView.Adapter adapter, RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public boolean onItemLongClick(View view, RecyclerView.Adapter adapter, RecyclerView.ViewHolder holder, int position) {
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    @Override
    public void GoodsSumCallBack(int goodsSum, double goodsPrices) {
        goodsAllSize.setText(goodsSum + "件");
        goodsAllPrice.setText("￥" + StringUtils.changeToMoney(goodsPrices));
        isShowEmptyView(mDatas.size() == 0);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (mAdapter != null) {
            mAdapter.setCheckAllOrNo(isChecked);
            double goodsSumPrice = mAdapter.getGoodsSumPrice();
            int goodsCount = mAdapter.getmGoodsCount();
            goodsAllSize.setText(goodsCount + "件");
            goodsAllPrice.setText("￥" + StringUtils.changeToMoney(goodsSumPrice));
        }
    }

    private void isShowEmptyView(boolean isEmptyData) {
        if (isEmptyData) {
            noGoodsLayout.setVisibility(View.VISIBLE);
            hasGoodsLayout.setVisibility(View.GONE);
        } else {
            noGoodsLayout.setVisibility(View.GONE);
            hasGoodsLayout.setVisibility(View.VISIBLE);
        }
    }
}