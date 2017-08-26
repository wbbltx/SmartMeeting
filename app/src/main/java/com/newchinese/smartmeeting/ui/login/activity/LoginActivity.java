package com.newchinese.smartmeeting.ui.login.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.base.BaseSimpleActivity;

import butterknife.BindView;

public class LoginActivity extends BaseSimpleActivity {

    @BindView(R.id.tv_title)
    TextView tv_title;

    @BindView(R.id.iv_back)
    ImageView iv_back;

    @BindView(R.id.iv_pen)
    ImageView iv_pen;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void onViewCreated(Bundle savedInstanceState) {

    }

    @Override
    protected void initStateAndData() {
        tv_title.setText("登录");
//        iv_back.setImageResource(0);
        iv_pen.setVisibility(View.GONE);
    }

    @Override
    protected void initListener() {

    }
}
