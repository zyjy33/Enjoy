package com.yunsen.enjoy.activity.goods;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yunsen.enjoy.R;
import com.yunsen.enjoy.activity.BaseFragmentActivity;
import com.yunsen.enjoy.common.Constants;
import com.yunsen.enjoy.http.URLConstants;
import com.yunsen.enjoy.utils.ToastUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/6/27.
 */

public class PhotoPreviewActivity extends BaseFragmentActivity {
    @Bind(R.id.action_back)
    ImageView actionBack;
    @Bind(R.id.action_bar_tv_right)
    TextView actionBarTvRight;
    @Bind(R.id.preview_img)
    ImageView previewImg;
    private String mImgUrl;

    @Override
    public int getLayout() {
        return R.layout.activity_photo_preview;
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);

    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        Intent intent = getIntent();
        String imgUrl;
        imgUrl = mImgUrl = intent.getStringExtra(Constants.PHOTO_PREVIEW_IMG);
        if (imgUrl != null && !imgUrl.startsWith("http")) {
            String trim = imgUrl.trim();
            imgUrl = URLConstants.REALM_URL + trim;
        }
        Glide.with(this)
                .load(imgUrl)
                .placeholder(R.mipmap.default_big_img)
                .into(previewImg);
    }

    @Override
    protected void initListener() {

    }


    @OnClick({R.id.action_back, R.id.action_bar_tv_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.action_back:
                finish();
                break;
            case R.id.action_bar_tv_right:
                Intent intent = new Intent();
                intent.putExtra(Constants.IMG_URL, mImgUrl);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }
}