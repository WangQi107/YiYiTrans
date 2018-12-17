package org.yiyi.translater.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import org.yiyi.translater.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class Choose_Dialog extends Dialog {
    private OkOnclickListener okOnclickListener;
    private TextView t;
    private Button ok;
    private List<Map<String, Object>> datas = null;
    private ListView lschoose;
    private String cc;
    private String a;
    private TextView s;
    private String language;
    SimpleAdapter adapter;

    public Choose_Dialog(Context context) {
        super(context, R.style.DialogTheme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_dialog);
        setCanceledOnTouchOutside(false);
        initViews();
        initDatas();
        setAdapter();
        setListeners();
    }

    private void initViews() {
        lschoose = findViewById(R.id.chooselan);
        t = findViewById(R.id.ctitle);
        datas = new ArrayList<>();
        s = findViewById(R.id.ctitle);
        ok=findViewById(R.id.btn_ok);
    }

    public void checkfrom(String c) {
        cc = c;
    }

    public void setTitle(String t) {
        if (cc.equals("two")) {
            a = t;
        }
    }

    private void initDatas() {
        if (cc.equals("one")) {
            getData("自动检测");
            getData("中文");
            getData("英语");
            getData("粤语");
            getData("文言文");
            getData("日语");
            getData("韩语");
            getData("法语");
            getData("西班牙语");
            getData("泰语");
            getData("阿拉伯语");
            getData("俄语");
            getData("葡萄牙语");
            getData("德语");
            getData("意大利语");
            getData("希腊语");
            getData("荷兰语");
            getData("波兰语");
            getData("保加利亚语");
            getData("爱沙尼亚语");
            getData("丹麦语");
            getData("芬兰语");
            getData("捷克语");
            getData("罗马尼亚语");
            getData("斯洛文尼亚语");
            getData("瑞典语");
            getData("匈牙利语");
            getData("繁体中文");
            getData("越南语");
        } else if (cc.equals("two")) {
            if (a != null) {
                s.setText(a);
            }
            getData("中文");
            getData("英语");
            getData("粤语");
            getData("文言文");
            getData("日语");
            getData("韩语");
            getData("法语");
            getData("西班牙语");
            getData("泰语");
            getData("阿拉伯语");
            getData("俄语");
            getData("葡萄牙语");
            getData("德语");
            getData("意大利语");
            getData("希腊语");
            getData("荷兰语");
            getData("波兰语");
            getData("保加利亚语");
            getData("爱沙尼亚语");
            getData("丹麦语");
            getData("芬兰语");
            getData("捷克语");
            getData("罗马尼亚语");
            getData("斯洛文尼亚语");
            getData("瑞典语");
            getData("匈牙利语");
            getData("繁体中文");
            getData("越南语");
        }
    }

    private void getData(String s) {
        Map<String, Object> mp = new HashMap<>();
        mp.put("lan", s);
        datas.add(mp);
    }

    private void setAdapter() {
        adapter = new SimpleAdapter(getContext(), datas, R.layout.citem, new String[]{"lan"}, new int[]{R.id.tv_lan});
        lschoose.setAdapter(adapter);
    }

    private void setListeners() {
        lschoose.setSelector(R.color.hui);
        lschoose.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> map = (HashMap<String, String>) parent.getItemAtPosition(position);
                language = map.get("lan");
                if (cc.equals("one")) {
                    SharedPreferences sp = getContext().getSharedPreferences("Language", MODE_PRIVATE);
                    sp.edit().putString("language",language).commit();
                } else if (cc.equals("two")) {
                    SharedPreferences sp = getContext().getSharedPreferences("Language", MODE_PRIVATE);
                    sp.edit().putString("language",language).commit();
                }
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (okOnclickListener!=null){
                    okOnclickListener.OnOKClick();
                }
            }
        });
    }

    public interface OkOnclickListener {
        public void OnOKClick();
    }

    public void setOkOnclickListener(OkOnclickListener okOnclickListener){
        this.okOnclickListener=okOnclickListener;
    }
}
