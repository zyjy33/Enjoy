package com.yunsen.enjoy.activity.user;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hengyushop.dao.WareDao;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQAuth;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.tauth.Tencent;
import com.yanzhenjie.permission.Permission;
import com.yunsen.enjoy.R;
import com.yunsen.enjoy.activity.BaseFragmentActivity;
import com.yunsen.enjoy.activity.mine.UserForgotPasswordActivity;
import com.yunsen.enjoy.common.Constants;
import com.yunsen.enjoy.common.SpConstants;
import com.yunsen.enjoy.http.AsyncHttp;
import com.yunsen.enjoy.http.URLConstants;
import com.yunsen.enjoy.model.UserRegisterllData;
import com.yunsen.enjoy.model.event.EventConstants;
import com.yunsen.enjoy.model.event.UpUiEvent;
import com.yunsen.enjoy.utils.ToastUtils;
import com.yunsen.enjoy.widget.DialogProgress;
import com.yunsen.enjoy.widget.MyPopupWindowMenu;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class PhoneLoginActivity extends BaseFragmentActivity implements OnClickListener {
    private Button btn_login;
    private EditText et_username, et_pwd, userphone;
    private Button img_title_register;
    private TextView imgbtn_findpwd;
    private MessageDigest md;
    private String status, rnd, name, password, mm, mi, key, hengyucode,
            bossUid;
    public static String id, user_id, login_sign, pwd;

    private String inStr;

    private DialogProgress progress;
    private String strUrlone, strUrltwo;

    public static String user_name;
    private MyPopupWindowMenu popupWindowMenu;
    private SharedPreferences spPreferences;
    private String strUr2 = URLConstants.REALM_NAME_LL + "/get_apk_version?browser=android";
    private String URL;
    private ImageView img_weixin_login, img_qq_login;
    public static boolean isWXLogin = false;
    public static IWXAPI mWxApi;
    boolean zhuangtai = false;
    public static Bitmap bitmap;
    public static boolean panduan = false;
    UserRegisterllData data;


    @Override
    public int getLayout() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        spPreferences = getSharedPreferences("longuserset", MODE_PRIVATE);
        progress = new DialogProgress(PhoneLoginActivity.this);
        popupWindowMenu = new MyPopupWindowMenu(this);
        initdata();
    }

    @Override
    protected void onRequestPermissionSuccess(int requestCode) {
        if (requestCode == Constants.READ_PHONE_STATE) {
            Intent intent = new Intent(PhoneLoginActivity.this, UserRegisterActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        user_name = spPreferences.getString("user", "");
    }

    Handler handler = new Handler() {

        public void dispatchMessage(android.os.Message msg) {

            switch (msg.what) {
                case 0:
                    dialog();
                    break;
                case -1:
                    progress.CloseProgress();
                    break;
                case 6:
                    try {
                        strUrlone = URLConstants.REALM_NAME_LL
                                + "/get_user_model?username=" + name + "";
                        System.out.println("======11=============" + strUrlone);
                        AsyncHttp.get(strUrlone, new AsyncHttpResponseHandler() {
                            public void onSuccess(int arg0, String arg1) {
                                try {
                                    System.out.println("======66输出用户资料=============" + arg1);
                                    JSONObject object = new JSONObject(arg1);
                                    status = object.getString("status");
                                    String datetime = object.getString("datetime");
                                    JSONObject obj = object.getJSONObject("data");
                                    if (status.equals("y")) {
                                        data = new UserRegisterllData();
                                        data.user_name = obj.getString("user_name");
                                        data.id = obj.getString("id");
                                        data.user_code = obj.getString("user_code");
                                        data.agency_id = obj.getInt("agency_id");
                                        data.amount = obj.getString("amount");
                                        data.pension = obj.getString("pension");
                                        data.packet = obj.getString("packet");
                                        data.point = obj.getString("point");
                                        data.group_id = obj.getString("group_id");
                                        data.mobile = obj.getString("mobile");
                                        data.login_sign = obj.getString("login_sign");
                                        data.avatar = obj.getString("avatar");
                                        data.real_name = obj.getString("real_name");
                                        data.company_id = obj.getString("company_id");
                                        data.birthday = obj.getString("birthday");
                                        data.group_name = obj.getString("group_name");
                                        data.sex = obj.getString("sex");

                                        login_sign = data.login_sign;
                                        Editor editor = spPreferences.edit();
                                        editor.putString("login_sign", data.login_sign);
                                        editor.putString("avatar", data.avatar);
                                        editor.putString(SpConstants.MOBILE, data.mobile);
                                        editor.putString("group_id", data.group_id);
                                        editor.putString(SpConstants.USER_NAME, data.user_name);
                                        editor.putString("user_id", data.id);
                                        editor.putString("point", data.point);
                                        editor.putString("real_name", data.real_name);
                                        editor.putString("company_id", data.company_id);
                                        editor.putString("birthday", data.birthday);
                                        editor.putString("sex", data.sex);
                                        editor.putString("datetime", datetime);
                                        editor.putString(SpConstants.GROUP_NAME, data.group_name);
                                        editor.commit();
                                        /**
                                         * 新增手机登录后保存用户信息
                                         */
                                        saveUserInfo(datetime);
                                        EventBus.getDefault().postSticky(new UpUiEvent(EventConstants.APP_LOGIN));//登录成功通知mine更新页面
                                        setResult(RESULT_OK);
                                        finish();
                                        data = null;
                                    } else {

                                    }
                                } catch (JSONException e) {

                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(Throwable arg0, String arg1) {

                                super.onFailure(arg0, arg1);
                                Toast.makeText(PhoneLoginActivity.this, "连接超时", Toast.LENGTH_SHORT).show();
                            }
                        }, PhoneLoginActivity.this);

                    } catch (Exception e) {

                        e.printStackTrace();
                    }
                    break;
                case 10:
                    break;
                case 1:
                    String str = (String) msg.obj;
                    Toast.makeText(PhoneLoginActivity.this, str, Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(PhoneLoginActivity.this, "用户名错误", Toast.LENGTH_SHORT).show();
                    break;
                case 5:
                    try {
                        System.out.println("=================2222222==" + mi);
                        strUrltwo = URLConstants.REALM_URL + "/mi/login.ashx?yth="
                                + processParam(name) + "&pwd=" + mi + "&msisdn=";
                    } catch (UnsupportedEncodingException e) {

                        e.printStackTrace();
                    }

                    System.out.println("strUrltwo   " + strUrltwo);
                    AsyncHttp.get(strUrltwo, new AsyncHttpResponseHandler() {
                        public void onSuccess(int arg0, String arg1) {
                            try {
                                System.out.println("=================4==" + arg1);
                                JSONObject object = new JSONObject(arg1);
                                int s = object.getInt("status");
                                System.out.println("s:  " + s);
                                if (s == 0) {
                                    String str = object.getString("msg");
                                    progress.CloseProgress();
                                    Message message2 = new Message();
                                    message2.what = 1;
                                    message2.obj = str;
                                    handler.sendMessage(message2);
                                } else {
                                    key = object.getString("key");
                                    hengyucode = object.getString("yth");
                                    bossUid = object.getString("bossUid");
                                    handler.sendEmptyMessage(0);
                                }
                            } catch (JSONException e) {

                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Throwable arg0, String arg1) {

                            super.onFailure(arg0, arg1);
                            Toast.makeText(PhoneLoginActivity.this, "连接超时",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }, PhoneLoginActivity.this);

                    break;

                default:
                    break;
            }

        }

        ;

    };

    /**
     * 保存用户信息
     *
     * @param datetime
     */
    private void saveUserInfo(String datetime) {
        SharedPreferences sp = getSharedPreferences(SpConstants.SP_LONG_USER_SET_USER, MODE_PRIVATE);
        Editor edit = sp.edit();
        edit.putString("login_sign", data.login_sign);
        edit.putString("avatar", data.avatar);
        edit.putString(SpConstants.MOBILE, data.mobile);
        edit.putString("group_id", data.group_id);
        edit.putString(SpConstants.USER_NAME, data.user_name);
        edit.putString("user_id", data.id);
        edit.putString("point", data.point);
        edit.putString("real_name", data.real_name);
        edit.putString("company_id", data.company_id);
        edit.putString("birthday", data.birthday);
        edit.putString("sex", data.sex);
        edit.putString("datetime", datetime);
        edit.putString(SpConstants.GROUP_NAME, data.group_name);
        edit.commit();
    }

    private void saveUserInfo2(String user, String pwd, String user_id) {
        SharedPreferences sp = getSharedPreferences(SpConstants.SP_LONG_USER_SET_USER, MODE_PRIVATE);
        Editor edit = sp.edit();
        edit.putBoolean("save", true);
        edit.putString("user", user);
        edit.putString("pwd", pwd);
        edit.putString("user_id", user_id);
        edit.commit();
    }

    // 获取当前程序的版本信息
    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {

            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }

    // private String strUrl = URLConstants.REALM_NAME_LL;
    private String url, version, updatainfo;

    // 解析服务器端的版本信息
    public void xmlparse(String st) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(st
                    .getBytes()));
            NodeList nodeList1 = document.getElementsByTagName("version");
            NodeList nodeList2 = document.getElementsByTagName("url");
            NodeList nodeList3 = document.getElementsByTagName("updateInfo");
            version = nodeList1.item(0).getTextContent();
            url = nodeList2.item(0).getTextContent();
            updatainfo = nodeList3.item(0).getTextContent();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updata() {

        try {
            /**
             * 版本2
             */
            AsyncHttp.get(strUr2, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int arg0, String arg1) {

                    super.onSuccess(arg0, arg1);
                    System.out.println("首页版本==============" + arg1);
                    try {
                        JSONObject jsonObject = new JSONObject(arg1);
                        JSONObject jsob = jsonObject.getJSONObject("data");
                        String file_version = jsob.getString("file_version");
                        String link_url = jsob.getString("link_url");
                        String file_path = jsob.getString("file_path");
                        String id = jsob.getString("id");
                        URL = URLConstants.REALM_NAME_HTTP + file_path;
                        System.out.println("首页版本URL==============" + URL);
                        String c_version = getAppVersionName(
                                getApplicationContext()).trim().replaceAll(
                                "\\.", "");
                        float server_version = Float.parseFloat(file_version
                                .replaceAll("\\.", ""));// 服务器
                        float client_version = Float.parseFloat(c_version);// 当前

                        System.out.println("服务器:" + server_version + "/当前:"
                                + client_version);
                        if (server_version > client_version) {
                            // Toast.makeText(MainFragment.this, "提示更新",
                            // 200).show();
                            Message message = new Message();
                            message.what = 0;
                            handler.sendMessage(message);
                        }
                        // Toast.makeText(MainFragment.this, "没有提示更新",
                        // 200).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, getApplicationContext());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static File getFileFromServer(String path, ProgressDialog pd)
            throws Exception {
        // 如果相等的话表示当前的sdcard挂载在手机上并且是可用的
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            System.out.println("=====0==============");
            java.net.URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            // 获取到文件的大小
            pd.setMax(conn.getContentLength());
            InputStream is = conn.getInputStream();
            File file = new File(Environment.getExternalStorageDirectory(),
                    "updata.apk");
            FileOutputStream fos = new FileOutputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
            byte[] buffer = new byte[1024];
            int len;
            int total = 0;
            System.out.println("=====1==============");
            while ((len = bis.read(buffer)) != -1) {
                System.out.println("=====2==============");
                fos.write(buffer, 0, len);
                total += len;
                // 获取当前下载量
                pd.setProgress(total);
            }
            fos.close();
            bis.close();
            is.close();
            return file;
        } else {
            return null;
        }
    }

    protected void downLoadApk() {
        final ProgressDialog pd; // 进度条对话框
        pd = new ProgressDialog(PhoneLoginActivity.this);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("正在下载更新");
        pd.setCanceledOnTouchOutside(false);
        pd.setProgressNumberFormat(null);
        zhuangtai = true;
        pd.show();
        new Thread() {
            @Override
            public void run() {
                try {
                    System.out.println("=====URL==============" + URL);
                    File file = getFileFromServer(URL, pd);
                    sleep(3000);
                    installApk(file);
                    pd.dismiss(); // 结束掉进度条对话框
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /*
     * Auto-generated method stub this.setResult(0);
     * AppManager.getAppManager().finishActivity(); return true;
     *
     * }
     */
    // 安装apk
    protected void installApk(File file) {
        Intent intent = new Intent();
        // 执行动作
        intent.setAction(Intent.ACTION_VIEW);
        // 执行的数据类型
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");// 编者按：此处Android应为android，否则造成安装不了
        PhoneLoginActivity.this.startActivity(intent);
    }

    // 程序版本更新
    private void dialog() {

        AlertDialog.Builder builder = new Builder(PhoneLoginActivity.this);
        // builder.setMessage(updatainfo);
        builder.setMessage("检查到最新版本，是否要更新！");
        builder.setTitle("提示:新版本");
        builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                downLoadApk();
            }
        });
        builder.setNegativeButton("以后再说",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    private void initdata() {
        userphone = (EditText) findViewById(R.id.et_user_phone);
        LinearLayout ll_buju = (LinearLayout) findViewById(R.id.ll_buju);
        //		ll_buju.setBackgroundResource(R.drawable.denglu_beijing);
        img_weixin_login = (ImageView) findViewById(R.id.img_weixin_login);
        img_qq_login = (ImageView) findViewById(R.id.img_qq_login);
        img_title_register = (Button) findViewById(R.id.img_title_registre);
        btn_login = (Button) findViewById(R.id.btn_login);
        et_username = (EditText) findViewById(R.id.et_user_name);
        et_pwd = (EditText) findViewById(R.id.et_user_pwd);
        imgbtn_findpwd = (TextView) findViewById(R.id.wenhao);
        img_weixin_login.setOnClickListener(this);
        img_qq_login.setOnClickListener(this);
        img_title_register.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        imgbtn_findpwd.setOnClickListener(this);

        TextView img_menu = (TextView) findViewById(R.id.iv_fanhui);
        img_menu.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                finish();
            }
        });
    }

    // 字符串上传服务器 乱码 问题的解决方法
    private String processParam(String temp)
            throws UnsupportedEncodingException {
        return URLEncoder.encode(temp, "UTF-8");
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.img_weixin_login:// 微信登录
                isWXLogin = true;
                SendAuth.Req req = new SendAuth.Req();
                req.scope = "snsapi_userinfo";
                req.state = "wechat_sdk_demo";
                mWxApi.sendReq(req);
                break;
            case R.id.img_qq_login:// QQ登录
                break;
            case R.id.img_title_registre:
                requestPermission(new String[]{Permission.READ_PHONE_STATE, Permission.READ_PHONE_STATE}, Constants.READ_PHONE_STATE);
                break;
            case R.id.btn_login:
                name = userphone.getText().toString().trim();
                password = et_pwd.getText().toString();
                if ("".equals(name)) {
                    Toast.makeText(PhoneLoginActivity.this, "手机号码不能为空", Toast.LENGTH_SHORT).show();
                } else if ("".equals(password)) {
                    Toast.makeText(PhoneLoginActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    progress.CreateProgress();
                    strUrlone = URLConstants.REALM_NAME_LL
                            + "/user_login?site=mobile&username=" + name
                            + "&password=" + password + "&terminal=true";
                    AsyncHttp.get(strUrlone, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int arg0, String arg1) {
                            super.onSuccess(arg0, arg1);
                            try {
                                JSONObject jsonObject = new JSONObject(arg1);
                                status = jsonObject.getString("status");
                                System.out.println("status: " + status);
                                String info = jsonObject.getString("info");
                                if (status.equals("y")) {
                                    JSONObject bjot = jsonObject.getJSONObject("data");
                                    id = bjot.getString("id");
                                    user_id = bjot.getString("id");
                                    user_name = bjot.getString("user_name");
                                    setInStr(password);
                                    init();
                                    mm = compute();
                                    mm = mm.toUpperCase();
                                    String myrnd = rnd;
                                    setInStr(mm + myrnd);
                                    init();
                                    mi = compute();
                                    Editor editor = spPreferences.edit();
                                    editor.putBoolean("save", true);
                                    editor.putString("user", userphone.getText().toString());
                                    editor.putString("pwd", et_pwd.getText().toString());
                                    editor.putString("user_id", user_id);
                                    editor.commit();
                                    /**
                                     * 新增保存手机用户信息
                                     */
                                    saveUserInfo2(userphone.getText().toString(), et_pwd.getText().toString(), user_id);
                                    panduan = true;
                                    ToastUtils.makeTextShort(info);
                                    progress.CloseProgress();
                                    handler.sendEmptyMessage(6);
                                    // TODO: 2018/5/7  关闭某个页面
//                                    UserLoginActivity.handler1.sendEmptyMessage(1);
                                } else if ("n".equals(status)) {
                                    ToastUtils.makeTextShort(info);
                                    progress.CloseProgress();
                                }
                            } catch (JSONException e) {

                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(Throwable throwable, String s) {
                            super.onFailure(throwable, s);
                            Log.e(TAG, "onFailure: " + s);
                        }
                    }, PhoneLoginActivity.this);

                }
                break;
            case R.id.wenhao:// 找回密码
                Intent intent2 = new Intent(PhoneLoginActivity.this,
                        UserForgotPasswordActivity.class);
                intent2.putExtra("type", "1");
                startActivity(intent2);
                break;
            default:
                break;
        }
    }


    private static final String TAG = "PhoneLoginActivity";

    public void MD5() {
        inStr = null;
        md = null;
    }

    public void setInStr(String str) {
        this.inStr = str;
    }

    public void init() {
        try {
            this.md = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public String compute() {

        char[] charArray = this.inStr.toCharArray();
        byte[] byteArray = new byte[charArray.length];
        for (int i = 0; i < charArray.length; i++) {
            byteArray[i] = (byte) charArray[i];
        }
        byte[] mdBytes = this.md.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < mdBytes.length; i++) {
            int val = (mdBytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }

        return hexValue.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("menu");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {

        if (0 == popupWindowMenu.currentState && popupWindowMenu.isShowing()) {
            popupWindowMenu.dismiss(); // 对话框消失
            popupWindowMenu.currentState = 1; // 标记状态，已消失
        } else {
            popupWindowMenu.showAtLocation(findViewById(R.id.layout),
                    Gravity.BOTTOM, 0, 0);
            popupWindowMenu.currentState = 0; // 标记状态，显示中
        }
        return false; // true--显示系统自带菜单；false--不显示。
    }

}
