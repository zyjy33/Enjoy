package com.yunsen.enjoy.http;


import android.util.Log;

import com.yunsen.enjoy.model.AdvertList;
import com.yunsen.enjoy.model.AdvertModel;
import com.yunsen.enjoy.model.BrandResponse;
import com.yunsen.enjoy.model.CarModel;
import com.yunsen.enjoy.model.NoticeModel;
import com.yunsen.enjoy.model.NoticeResponse;
import com.yunsen.enjoy.model.SProviderModel;
import com.yunsen.enjoy.model.ServiceProvideResponse;

import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

/**
 * Created by Administrator on 2018/4/20.
 */

public class HttpProxy {
    private static final String TAG = "HttpProxy";

    /**
     * 获取首页广告
     */
    public static void getHomeAdvertList(final HttpCallBack<List<AdvertModel>> callBack) {
        HttpClient.get(URLConstants.HOME_ADV_URL, null, new HttpResponseHandler<AdvertList>() {
            @Override
            public void onSuccess(AdvertList response) {
                List<AdvertModel> data = response.getData();
                callBack.onSuccess(data);
            }

            @Override
            public void onFailure(Request request, Exception e) {
                callBack.onError(request, e);
            }
        });
    }

    /**
     * 获取车辆广告
     *
     * @param callBack
     */
    public static void getCarList(final HttpCallBack<List<AdvertModel>> callBack) {
        HttpClient.get(URLConstants.CAR_ADV_URL, null, new HttpResponseHandler<AdvertList>() {
            @Override
            public void onSuccess(AdvertList response) {
                List<AdvertModel> data = response.getData();
                callBack.onSuccess(data);
            }

            @Override
            public void onFailure(Request request, Exception e) {
                callBack.onError(request, e);
            }
        });
    }

    public static void getNoticeData1(final HttpCallBack<List<NoticeModel>> callBack) {
        HashMap<String, String> param = new HashMap<>();
        param.put("channel_name", "news");
        param.put("category_id", "6");
        param.put("page_size", "8");
        param.put("page_index", "1");
        param.put("strwhere", "status=0");
        param.put("orderby","");


        HttpClient.get(URLConstants.NOTICE_URL, param, new HttpResponseHandler<NoticeResponse>() {
            @Override
            public void onSuccess(NoticeResponse response) {
                callBack.onSuccess(response.getData());
            }

            @Override
            public void onFailure(Request request, Exception e) {
                super.onFailure(request, e);
            }
        });
    }

    public static void getNoticeData2(final HttpCallBack<List<NoticeModel>> callBack) {
        HashMap<String, String> param = new HashMap<>();
        param.put("channel_name", "news");
        param.put("category_id", "7");
        param.put("page_size", "8");
        param.put("page_index", "1");
        param.put("strwhere", "status=0");
        param.put("orderby","");

        HttpClient.get(URLConstants.NOTICE_URL, param, new HttpResponseHandler<NoticeResponse>() {
            @Override
            public void onSuccess(NoticeResponse response) {
                callBack.onSuccess(response.getData());
            }

            @Override
            public void onFailure(Request request, Exception e) {
                super.onFailure(request, e);
            }
        });
    }

    public static void getBrandData(final HttpCallBack<List<CarModel>> callBack) {
        HashMap<String, String> param = new HashMap<>();
        param.put("top", "4");
        param.put("parent_id", "0");
        param.put("channel_id", "7");
        param.put("orderby", "id desc");
        param.put("flag", "false");

        HttpClient.get(URLConstants.CAR_BRAND_URL, param, new HttpResponseHandler<BrandResponse>() {
            @Override
            public void onSuccess(BrandResponse response) {
                if (response.getData() != null) {
                    List<CarModel> list = response.getData().getList();
                    callBack.onSuccess(list);
                } else {
                    callBack.onError(null, new Exception("date is empty!"));
                }
            }

            @Override
            public void onFailure(Request request, Exception e) {
                super.onFailure(request, e);
                callBack.onError(request, e);
            }
        });

    }

    public static void getServiceProvider(final HttpCallBack<List<SProviderModel>> callBack) {
        HashMap<String, String> param = new HashMap<>();
        param.put("trade_id", "0");
        param.put("page_size", "5");
        param.put("page_index", "1");
        param.put("strwhere", "status=0 and datatype='Supply'");
        param.put("orderby","");


        HttpClient.get(URLConstants.SERVICE_PROVIDE, param, new HttpResponseHandler<ServiceProvideResponse>() {
            @Override
            public void onSuccess(ServiceProvideResponse response) {
                if (response.getData() != null) {
                    List<SProviderModel> list = response.getData();
                    callBack.onSuccess(list);
                } else {
                    callBack.onError(null, new Exception("date is empty!"));
                }
            }

            @Override
            public void onFailure(Request request, Exception e) {
                super.onFailure(request, e);
                callBack.onError(request, e);
            }
        });
    }
}
