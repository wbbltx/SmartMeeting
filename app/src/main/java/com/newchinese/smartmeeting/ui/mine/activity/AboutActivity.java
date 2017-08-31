package com.newchinese.smartmeeting.ui.mine.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.base.BaseSimpleActivity;
import com.newchinese.smartmeeting.util.CustomizedToast;

import butterknife.BindView;
import butterknife.OnClick;

public class AboutActivity extends BaseSimpleActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_pen)
    ImageView ivPen;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    protected void onViewCreated(Bundle savedInstanceState) {

    }

    @Override
    protected void initStateAndData() {
        tvTitle.setText("关于我们");
        ivPen.setVisibility(View.GONE);
    }

    @Override
    protected void initListener() {

    }

    @OnClick({R.id.iv_back, R.id.btn_deal, R.id.btn_update})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_deal:
                startActivity(new Intent(this, DealActivity.class));
                break;
            case R.id.btn_update:
                CustomizedToast.showLong(this, "已经是最新版本");
                break;
        }
    }
}
