package com.yunsen.enjoy.activity.user;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;


import com.loopj.android.http.AsyncHttpResponseHandler;
import com.yunsen.enjoy.R;
import com.yunsen.enjoy.common.SpConstants;
import com.yunsen.enjoy.http.AsyncHttp;
import com.yunsen.enjoy.http.URLConstants;
import com.yunsen.enjoy.model.UserRegisterllData;
import com.yunsen.enjoy.model.event.EventConstants;
import com.yunsen.enjoy.model.event.UpUiEvent;
import com.yunsen.enjoy.widget.DialogProgress;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 提示绑定手机号
 *
 * @author
 */
public class TishiWxBangDingActivity extends AppCompatActivity implements OnClickListener {
    private TextView btnConfirm;//
    private TextView btnCancle, tv_yue;//
    private Intent intent;
    public Activity mContext;
    public static Handler handler;
    private SharedPreferences spPreferences_login;
    String user_name, user_id, headimgurl, access_token, sex, unionid, area, real_name;
    String province = "";
    String city = "";
    String country = "";
    String nickname = "";
    String oauth_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tishi_weixin);
        initUI();
    }


    protected void initUI() {
        btnConfirm = (TextView) findViewById(R.id.btnConfirm);//
        btnConfirm.setOnClickListener(this);//
        btnCancle = (TextView) findViewById(R.id.btnCancle);//
        btnCancle.setOnClickListener(this);//

        handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 8:

                }
            }
        };
    }


    /**
     * 点击触发事件
     */
    @Override
    public void onClick(View v) {
        intent = new Intent();
        switch (v.getId()) {
            case R.id.btnConfirm://取消
                finish();
                break;
            case R.id.btnCancle://
                userlogin();
                break;
            default:
                break;
        }
    }

    public void userlogin() {
        try {
            spPreferences_login = getSharedPreferences("longuserset_login", MODE_PRIVATE);
            nickname = spPreferences_login.getString(SpConstants.NICK_NAME, "");
            headimgurl = spPreferences_login.getString("headimgurl", "");
            unionid = spPreferences_login.getString("unionid", "");
            access_token = spPreferences_login.getString("access_token", "");
            sex = spPreferences_login.getString("sex", "");
            String oauth_openid = spPreferences_login.getString("oauth_openid", "");
            province = getIntent().getStringExtra("province");
            city = getIntent().getStringExtra("city");

            if (province == null) {
                province = "";
            }
            if (city == null) {
                city = "";
            }

            System.out.println("UserLoginActivity.oauth_name=====================" + UserLoginActivity.oauth_name);
            //				if (!UserLoginActivity.oauth_name.equals("")) {
            System.out.println("UserLoginActivity=====================" + UserLoginActivity.oauth_name);
            System.out.println("UserLoginWayActivity=====================" + UserLoginWayActivity.oauth_name);

            if (UserLoginActivity.oauth_name.equals("weixin")) {
                oauth_name = "weixin";
            } else if (UserLoginWayActivity.oauth_name.equals("weixin")) {
                oauth_name = "qq";
            }

            //				}
            System.out.println("nickname-----1-----" + nickname);
            String nick_name = nickname.replaceAll("\\s*", "");
            System.out.println("nick_name-----2-----" + nick_name);
            String strUrlone = URLConstants.REALM_NAME_LL + "/user_oauth_register_0217?nick_name=" + nick_name + "&sex=" + sex + "&avatar=" + headimgurl + "" +
                    "&province=" + province + "&city=" + city + "&country=" + country + "&oauth_name=" + oauth_name + "&oauth_unionid=" + unionid + "" +
                    "&oauth_openid=" + oauth_openid + "";
            System.out.println("======11=============" + strUrlone);
            AsyncHttp.get(strUrlone, new AsyncHttpResponseHandler() {
                public void onSuccess(int arg0, String arg1) {
                    System.out.println("======输出=============" + arg1);
                    //						Toast.makeText(UserLoginActivity.this, "数据为+"+arg1, 400).show();
                    try {
                        JSONObject object = new JSONObject(arg1);
                        String status = object.getString("status");
                        String info = object.getString("info");
                        String datall = object.getString("data");
                        if (datall.equals("null")) {
                            Intent intent = new Intent(TishiWxBangDingActivity.this, MobilePhoneActivity.class);
                            intent.putExtra("nickname", nickname);
                            intent.putExtra("access_token", access_token);
                            intent.putExtra("unionid", unionid);
                            intent.putExtra("sex", sex);
                            intent.putExtra("headimgurl", headimgurl);
                            startActivity(intent);
                            finish();

                        } else {
                            JSONObject obj = object.getJSONObject("data");
                            UserRegisterllData data = new UserRegisterllData();
                            data.id = obj.getString("id");
                            data.user_name = obj.getString("user_name");
                            user_id = data.id;
                            SharedPreferences spPreferences = getSharedPreferences("longuserset", MODE_PRIVATE);
                            String user = spPreferences.getString("user", "");
                            System.out.println("---1-------------------" + user);
                            Editor editor = spPreferences.edit();
                            editor.putString("user", data.user_name);
                            editor.putString("user_id", data.id);
                            editor.commit();
                            /**
                             * 乐享保存绑定用户的信息
                             */
                            SharedPreferences sp = getSharedPreferences(SpConstants.SP_LONG_USER_SET_USER, MODE_PRIVATE);
                            Editor edit = sp.edit();
                            edit.putString("user", data.user_name);
                            edit.putString("user_id", data.id);
                            edit.commit();
                            String user_name = spPreferences.getString("user", "");
                            System.out.println("---2-------------------" + user_name);
                            EventBus.getDefault().postSticky(new UpUiEvent(EventConstants.APP_LOGIN));
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                ;
            }, TishiWxBangDingActivity.this);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }


}