package com.yunsen.enjoy.fragment.home;

import android.content.Context;
import android.graphics.Paint;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yunsen.enjoy.R;
import com.yunsen.enjoy.model.GoodsData;
import com.yunsen.enjoy.widget.recyclerview.CommonAdapter;
import com.yunsen.enjoy.widget.recyclerview.base.ViewHolder;

import java.util.List;

/**
 * Created by Administrator on 2018/5/23.
 */

public class GoodsPartsAdapter extends CommonAdapter<GoodsData> {
    public GoodsPartsAdapter(Context context, int layoutId, List<GoodsData> datas) {
        super(context, layoutId, datas);
    }

    @Override
    protected void convert(ViewHolder holder, GoodsData goodsData, int position) {
        ImageView imageView = (ImageView) holder.getView(R.id.goods_parts_img);
        TextView marketTv = (TextView) holder.getView(R.id.goods_parts_market_price);
        marketTv.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        Glide.with(mContext)
                .load(goodsData.getImg_url())
                .into(imageView);
        holder.setText(R.id.goods_parts_title, goodsData.getTitle());
        holder.setText(R.id.goods_parts_sell_price, "￥" + goodsData.getSell_price());
        holder.setText(R.id.goods_parts_market_price, "￥" + goodsData.getMarket_price());
    }

    public void upData(List<GoodsData> datas) {
        mDatas.clear();
        if (datas != null) {
            mDatas.addAll(datas);
        }
        this.notifyDataSetChanged();
    }
}