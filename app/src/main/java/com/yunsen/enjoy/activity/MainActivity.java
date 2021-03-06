
package com.yunsen.enjoy.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yunsen.enjoy.R;
import com.yunsen.enjoy.common.Constants;
import com.yunsen.enjoy.common.PermissionSetting;
import com.yunsen.enjoy.common.SpConstants;
import com.yunsen.enjoy.common.wsmanager.WsManager;
import com.yunsen.enjoy.fragment.BuyFragment;
import com.yunsen.enjoy.fragment.CarFragment;
import com.yunsen.enjoy.fragment.DiscoverFragment;
import com.yunsen.enjoy.fragment.MainPagerFragment;
import com.yunsen.enjoy.fragment.MineFragment;
import com.yunsen.enjoy.http.HttpCallBack;
import com.yunsen.enjoy.http.HttpProxy;
import com.yunsen.enjoy.http.URLConstants;
import com.yunsen.enjoy.http.down.UpdateApkThread;
import com.yunsen.enjoy.model.ApkVersionInfo;
import com.yunsen.enjoy.model.event.EventConstants;
import com.yunsen.enjoy.model.event.UpCityEvent;
import com.yunsen.enjoy.ui.UIHelper;
import com.yunsen.enjoy.utils.DeviceUtil;
import com.yunsen.enjoy.utils.SharedPreference;
import com.yunsen.enjoy.utils.ToastUtils;
import com.zaaach.citypicker.CityPicker;
import com.zaaach.citypicker.model.LocateState;
import com.zaaach.citypicker.model.LocatedCity;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Request;

public class MainActivity extends BaseFragmentActivity implements AMapLocationListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String CURR_INDEX = "currIndex";
    private int currIndex = 0;

    private RadioGroup group;

    private ArrayList<String> fragmentTags;
    private FragmentManager fragmentManager;
    private long mFirstPressedTime;
    private MineFragment mMineFragment;
    private AlertDialog mAppUpDataDialog;
    private LocationManager mLm;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
//        WsManager.getInstance().init();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        fragmentManager = getSupportFragmentManager();
        initMainData(savedInstanceState);
        initMainView();

        requestPermission(new String[]{Permission.ACCESS_COARSE_LOCATION, Permission.ACCESS_FINE_LOCATION}, 7);
    }

    @Override
    protected void onRequestPermissionSuccess(int requestCode) {
        super.onRequestPermissionSuccess(requestCode);
        if (requestCode == Constants.CALL_PHONE) {
            UIHelper.showPhoneNumberActivity(this, Constants.PHONE_NUMBER);
        } else if (requestCode == Constants.WRITE_EXTERNAL_STORAGE) {
            UIHelper.showPhotoActivity(this, Constants.PHOTO_ACTIVITY_REQUEST);
        } else {
            //得到系统的位置服务，判断GPS是否激活
            mLm = (LocationManager) getSystemService(LOCATION_SERVICE);
            boolean ok = mLm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (ok) {
                if (mlocationClient != null) {
                    mlocationClient.startLocation();
                } else {
                    initAddress();
                }
                initAddress();
            } else {
                Toast.makeText(this, "系统检测到未开启GPS定位服务", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setAction(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, Constants.LOCATION_ADDRESS_REQUEST);
            }
        }
    }

    private void initAddress() {
        mlocationClient = new AMapLocationClient(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位监听
        mlocationClient.setLocationListener(this);
        mLocationOption.setOnceLocation(true);
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        //启动定位
        mlocationClient.startLocation();
    }


    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            String city = amapLocation.getCity();
            if (city != null) {
                city = city.substring(0, city.length() - 1);
            }
            if (amapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                //定位完成之后更新数据
                String name = city + "市";
                SharedPreference.getInstance().putString(SpConstants.CITY_KEY, name);
                SharedPreference.getInstance().putString(SpConstants.CITY_CODE, amapLocation.getCityCode());
                SharedPreference.getInstance().putString(SpConstants.PROVINCE, amapLocation.getProvince());
                EventBus.getDefault().postSticky(new UpCityEvent(EventConstants.UP_CITY, name, amapLocation.getCityCode()));
            }
        }
    }

    @Override
    protected void initListener() {

    }

    @Override
    public void requestData() {
        HttpProxy.getApkVersion(new HttpCallBack<ApkVersionInfo>() {
            @Override
            public void onSuccess(ApkVersionInfo responseData) {
                String fileVersion = responseData.getFile_version();
                String version = DeviceUtil.getAppVersionName(MainActivity.this);
                String c_version = version.trim().replaceAll("\\.", "");
                float server_version = Float.parseFloat(fileVersion.replaceAll("\\.", ""));//服务器
                float client_version = Float.parseFloat(c_version);//当前
                if (server_version > client_version) {
                    dialog(URLConstants.REALM_URL + responseData.getFile_path(), responseData.getContent());
                }
//                else {
//                    ToastUtils.makeTextShort("当前为最新版本");
//                }
            }

            @Override
            public void onError(Request request, Exception e) {

            }
        });
    }

    // 程序版本更新
    private void dialog(final String downLoadUrl, String content) {
        if (mAppUpDataDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("提示:新版本");
            builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (UpdateApkThread.IsLoading()) {
                        Toast.makeText(MainActivity.this, "正在下载...", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        AndPermission.with(MainActivity.this)
                                .permission(Permission.Group.STORAGE)
                                .onGranted(new Action() {
                                    @Override
                                    public void onAction(List<String> permissions) {
                                        String filePath = Environment.getExternalStorageDirectory() + "/ss";
                                        new UpdateApkThread(downLoadUrl, filePath, "zams.apk", MainActivity.this).start();
                                    }
                                })
                                .onDenied(new Action() {
                                    @Override
                                    public void onAction(List<String> permissions) {
                                        new PermissionSetting(MainActivity.this).showSettingStorage(permissions);
                                    }
                                }).start();
                    }
                }
            });
            builder.setNegativeButton("以后再说",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            mAppUpDataDialog = builder.create();
        }

        if (!mAppUpDataDialog.isShowing()) {
            if (TextUtils.isEmpty(content)) {
                content = "发现新版本!";
            }
            mAppUpDataDialog.setMessage(content);
            mAppUpDataDialog.show();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURR_INDEX, currIndex);
    }

    private void initMainData(Bundle savedInstanceState) {
        fragmentTags = new ArrayList<>(Arrays.asList("HomeFragment", "ImFragment", "InterestFragment", "CarFragment", "MineFragment"));
        currIndex = 0;
        if (savedInstanceState != null) {
            currIndex = savedInstanceState.getInt(CURR_INDEX);
            hideSavedFragment();
        }
    }

    private void hideSavedFragment() {
        Fragment fragment = fragmentManager.findFragmentByTag(fragmentTags.get(currIndex));
        if (fragment != null) {
            fragmentManager.beginTransaction().hide(fragment).commit();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            int index = intent.getIntExtra(Constants.FRAGMENT_TYPE_KEY, -1);
            int indexId = R.id.foot_bar_home;
            if (index != -1) {
                switch (index) {
                    case 0:
                        indexId = R.id.foot_bar_home;
                        break;
                    case 1:
                        indexId = R.id.foot_bar_im;
                        break;
                    case 2:
                        indexId = R.id.foot_bar_interest;
                        break;
                    case 3:
                        indexId = R.id.foot_bar_car;
                        break;
                    case 4:
                        indexId = R.id.main_footbar_user;
                        break;
                }
                group.check(indexId);
            }
        }
    }

    private void initMainView() {
        group = (RadioGroup) findViewById(R.id.group);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.foot_bar_home:
                        currIndex = 0;
                        break;
                    case R.id.foot_bar_im:
                        currIndex = 1;
                        break;
                    case R.id.foot_bar_interest:
                        currIndex = 2;
                        break;
                    case R.id.foot_bar_car:
                        currIndex = 3;
                        break;
                    case R.id.main_footbar_user:
                        currIndex = 4;
                        break;
                    default:
                        break;
                }
                showFragment();
            }
        });
        showFragment();
    }

    private void showFragment() {

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag(fragmentTags.get(currIndex));
        if (fragment == null) {
            fragment = instantFragment(currIndex);
        }
        for (int i = 0; i < fragmentTags.size(); i++) {
            Fragment f = fragmentManager.findFragmentByTag(fragmentTags.get(i));
            if (f != null && f.isAdded()) {
                fragmentTransaction.hide(f);
            }
        }
        if (fragment.isAdded()) {
            fragmentTransaction.show(fragment);
        } else {
            fragmentTransaction.add(R.id.fragment_container, fragment, fragmentTags.get(currIndex));
        }
        fragmentTransaction.commitAllowingStateLoss();
        fragmentManager.executePendingTransactions();
    }

    private Fragment instantFragment(int currIndex) {
        switch (currIndex) {
            case 0:
                return new MainPagerFragment();
            case 1:
                return new BuyFragment();
            case 2:
                return new DiscoverFragment();
            case 3:
                return new CarFragment();
            case 4:
                mMineFragment = new MineFragment();
                return mMineFragment;
            default:
                return null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - mFirstPressedTime < 2000) {
                finish();
            } else {
                Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
                mFirstPressedTime = System.currentTimeMillis();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setCurrIndex(int index) {
        if (index < 0) {
            index = 0;
        }
        if (index > 3) {
            index = 0;
        }
        switch (index) {
            case 0:
                group.check(R.id.foot_bar_home);
                break;
            case 1:
                group.check(R.id.foot_bar_im);
                break;
            case 2:
                group.check(R.id.foot_bar_interest);
                break;
            case 3:
                group.check(R.id.foot_bar_car);
                break;
            case 4:
                group.check(R.id.main_footbar_user);
                break;

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.PHOTO_ACTIVITY_REQUEST) {
                Uri selectedImage = data.getData(); //获取系统返回的照片的Uri
                if (mMineFragment != null) {
                    mMineFragment.loadUserIcon(selectedImage);
                }
            }
        }
        if (requestCode == Constants.ORDER_ACT_REQUEST) {
            if (mMineFragment != null) {
                mMineFragment.onActivityResult(requestCode, resultCode, data);
            }
        } else if (requestCode == Constants.LOCATION_ADDRESS_REQUEST) {
            initAddress();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAppUpDataDialog != null) {
            if (mAppUpDataDialog.isShowing()) {
                mAppUpDataDialog.dismiss();
            }
            mAppUpDataDialog = null;
        }
//        WsManager.getInstance().disconnect();
    }

}
