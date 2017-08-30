package com.newchinese.smartmeeting.ui.mine.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.base.IToolbar;
import com.newchinese.smartmeeting.util.CustomizedToast;

public class AboutActivity extends AppCompatActivity implements IToolbar, View.OnClickListener {

    private Button mBtnDeal;
    private Button mBtnUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_about);
        super.onCreate(savedInstanceState);
        initView();
        initListener();
    }

    private void initListener() {
        mBtnDeal.setOnClickListener(this);
        mBtnUpdate.setOnClickListener(this);
    }

    private void initView() {
        mBtnDeal = (Button) findViewById(R.id.btn_deal);
        mBtnUpdate = (Button) findViewById(R.id.btn_update);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_deal:
                startActivity(new Intent(this, DealActivity.class));
                break;
            case R.id.btn_update:
                CustomizedToast.showLong(this, "已经是最新版本");
                break;
        }
    }
}
