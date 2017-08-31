package com.newchinese.smartmeeting.ui.mine.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.base.BaseSimpleActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class DealActivity extends BaseSimpleActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_pen)
    ImageView ivPen;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_deal;
    }

    @Override
    protected void onViewCreated(Bundle savedInstanceState) {

    }

    @Override
    protected void initStateAndData() {
        tvTitle.setText("服务条款与协议");
        ivPen.setVisibility(View.GONE);
    }

    @Override
    protected void initListener() {

    }


    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }
}
