package org.yiyi.translater.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.yiyi.translater.R;
import org.yiyi.translater.dialog.Choose_Dialog;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView copy;
    private Button from;
    private Button to;
    private EditText ett;
    private ImageView iclean;
    private ImageView iok;
    private TextView tvyi;
    private TextView tvres;
    private JSONObject js;
    private String result = "";
    private boolean net = false;
    private String ff;
    private String tt;
    private static final String TAG = "Log";
    private String date;
    private long exitTime = 0;

    private String url = "http://api.fanyi.baidu.com/api/trans/vip/translate";//请求的网址
    private String q = "";//需要翻译的文字
    private String f = "en";//翻译源语言
    private String t = "zh";//译文语言
    private String aid = "20180516000160623";//APP ID
    private String salt = "1435660288";//随机数
//    private String secretkey = "U_aL8_6aRQSRgOmQYmQA";//密钥
    private String secretkey = "abOOqlPfWcwouNN8hM87";//密钥
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                String r = (String) msg.obj;
                System.out.println(r);
                try {
                    js = new JSONObject(r);
                    JSONArray value = js.getJSONArray("trans_result");
                    JSONObject child = null;
                    for (int i = 0; i < value.length(); i++) {
                        child = value.getJSONObject(i);
                        result = child.getString("dst");
                        tvres.setText(result);
                        tvres.setVisibility(View.VISIBLE);
                        copy.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    try {
                        String errorCode = js.getString("error_code");
                        if ("52001".equals(errorCode)) {
                            Toast.makeText(MainActivity.this, "请求超时，请重试",
                                    Toast.LENGTH_SHORT).show();
                        } else if ("52002".equals(errorCode)) {
                            Toast.makeText(MainActivity.this, "系统错误，请重试",
                                    Toast.LENGTH_SHORT).show();
                        } else if ("52003".equals(errorCode)) {
                            Toast.makeText(MainActivity.this, "未授权用户，请检查您的appid是否正确",
                                    Toast.LENGTH_SHORT).show();
                        } else if ("54000".equals(errorCode)) {
                            Log.i(TAG, "必填参数为空，请检查是否少传参数");
                            Toast.makeText(MainActivity.this, "请输入文字",
                                    Toast.LENGTH_SHORT).show();
                        } else if ("58000".equals(errorCode)) {
                            Toast.makeText(MainActivity.this, "客户端IP非法，请检查您填写的IP地址是否正确可修改您填写的服务器IP地址",
                                    Toast.LENGTH_SHORT).show();
                        } else if ("54001".equals(errorCode)) {
                            Toast.makeText(MainActivity.this, "签名错误，请检查您的签名生成方法",
                                    Toast.LENGTH_SHORT).show();
                        } else if ("54003".equals(errorCode)) {
                            Toast.makeText(MainActivity.this, "访问频率受限，请降低您的调用频率",
                                    Toast.LENGTH_SHORT).show();
                        } else if ("58001".equals(errorCode)) {
                            Toast.makeText(MainActivity.this, "译文语言方向不支持，请检查译文语言是否在语言列表里",
                                    Toast.LENGTH_SHORT).show();
                        } else if ("54004".equals(errorCode)) {
                            Toast.makeText(MainActivity.this, "账户余额不足，请前往管理控制台为账户充值",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        checknet();
        if (net == false) {
            Toast.makeText(this, getString(R.string.neterror), Toast.LENGTH_SHORT).show();
        }
        setListeners();
    }

    private void initViews() {
        from = findViewById(R.id.languagefrom);
        to = findViewById(R.id.languageto);
        ett = findViewById(R.id.et_t);
        iclean = findViewById(R.id.im_clean);
        iok = findViewById(R.id.im_ok);
        tvres = findViewById(R.id.tv_res);
        tvyi = findViewById(R.id.tv_yi);
        copy = findViewById(R.id.im_copy);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.im_ok:
                checknet();
                if (net == true) {
                    q = ett.getText().toString();
                    if (q.equals("") || q == null) {
                        Toast.makeText(this, "请输入文字", Toast.LENGTH_SHORT).show();
                    } else {
                        InputMethodManager imm = (InputMethodManager) this.getSystemService(this.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(this.getWindow().getDecorView().getWindowToken(), 0);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                translate();
                            }
                        }).start();
                    }
                } else {
                    Toast.makeText(this, "网络错误！", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.im_clean:
                ett.setText("");
                tvres.setVisibility(View.INVISIBLE);
                copy.setVisibility(View.INVISIBLE);
                break;
            case R.id.languagefrom:
                final Choose_Dialog d = new Choose_Dialog(this);
                d.checkfrom("one");
                d.setOkOnclickListener(new Choose_Dialog.OkOnclickListener() {
                    @Override
                    public void OnOKClick() {
                        SharedPreferences sp = getSharedPreferences("Language", MODE_PRIVATE);
                        String ll = sp.getString("language", "");
                        if (ll == null || ll.equals("")) {
                            Toast.makeText(MainActivity.this, "请选择源语言", Toast.LENGTH_SHORT).show();
                        } else {
                            from.setText(ll);
                            sp.edit().putString("language","").commit();
                            d.dismiss();
                        }
                    }
                });
                d.show();
                break;
            case R.id.languageto:
                final Choose_Dialog dd = new Choose_Dialog(this);
                dd.checkfrom("two");
                dd.setTitle("目标语言");
                dd.setOkOnclickListener(new Choose_Dialog.OkOnclickListener() {
                    @Override
                    public void OnOKClick() {
                        SharedPreferences sp = getSharedPreferences("Language", MODE_PRIVATE);
                        String ll = sp.getString("language", "");
                        if (ll == null || ll.equals("")) {
                            Toast.makeText(MainActivity.this, "请选择目标语言", Toast.LENGTH_SHORT).show();
                        } else {
                            to.setText(ll);
                            sp.edit().putString("language","").commit();
                            dd.dismiss();
                        }
                    }
                });
                dd.show();
                break;
            case R.id.im_copy:
                ClipboardManager cm = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData mClipData = ClipData.newPlainText("Label", tvres.getText());
                cm.setPrimaryClip(mClipData);
                Toast.makeText(this, "翻译结果已复制到剪贴板", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void setListeners() {
        from.setOnClickListener(this);
        to.setOnClickListener(this);
        iclean.setOnClickListener(this);
        iok.setOnClickListener(this);
        copy.setOnClickListener(this);
    }

    private void checknet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isAvailable()) {
            net = false;
        } else {
            net = true;
        }
    }

    private void translate() {
        ff = from.getText().toString();
        f = lancode(ff);
        tt = to.getText().toString();
        t = lancode(tt);
        RequestParams params = new RequestParams();
        params.addQueryStringParameter("q", q);//需要翻译的文字
        params.addQueryStringParameter("from", f);//翻译源语言
        params.addQueryStringParameter("to", t);//译文语言
        params.addQueryStringParameter("appid", aid);//APP ID
        params.addQueryStringParameter("salt", salt);//随机数
        params.addQueryStringParameter("sign", md5(aid + q + salt + secretkey));//签名
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<Object>() {
            @Override
            public void onSuccess(ResponseInfo<Object> responseInfo) {
                Message msg = new Message();
                msg.what = 1;
                msg.obj = responseInfo.result;
                handler.sendMessage(msg);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Toast.makeText(MainActivity.this, "错误!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static String md5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String lancode(String language) {
        switch (language) {
            case "自动检测":
                language = "auto";
                break;
            case "英语":
                language = "en";
                break;
            case "中文":
                language = "zh";
                break;
            case "粤语":
                language = "yue";
                break;
            case "文言文":
                language = "wyw";
                break;
            case "日语":
                language = "jp";
                break;
            case "韩语":
                language = "kor";
                break;
            case "法语":
                language = "fra";
                break;
            case "西班牙语":
                language = "spa";
                break;
            case "泰语":
                language = "th";
                break;
            case "阿拉伯语":
                language = "ara";
                break;
            case "俄语":
                language = "ru";
                break;
            case "葡萄牙语":
                language = "pt";
                break;
            case "德语":
                language = "de";
                break;
            case "意大利语":
                language = "it";
                break;
            case "希腊语":
                language = "el";
                break;
            case "荷兰语":
                language = "nl";
                break;
            case "波兰语":
                language = "pl";
                break;
            case "保加利亚语":
                language = "bul";
                break;
            case "爱沙尼亚语":
                language = "est";
                break;
            case "丹麦语":
                language = "dan";
                break;
            case "芬兰语":
                language = "fin";
                break;
            case "捷克语":
                language = "cs";
                break;
            case "罗马尼亚语":
                language = "rom";
                break;
            case "斯洛文尼亚语":
                language = "slo";
                break;
            case "瑞典语":
                language = "swe";
                break;
            case "匈牙利语":
                language = "hu";
                break;
            case "繁体中文":
                language = "cht";
                break;
            case "越南语":
                language = "vie";
                break;
        }
        return language;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
