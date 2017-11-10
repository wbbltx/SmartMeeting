package com.newchinese.smartmeeting.ui.mine.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.base.BaseSimpleActivity;
import com.newchinese.smartmeeting.contract.AboutContract;
import com.newchinese.smartmeeting.presenter.mine.AboutPresenterImpl;
import com.newchinese.smartmeeting.util.CustomizedToast;
import com.newchinese.smartmeeting.util.DeviceUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class AboutActivity extends BaseSimpleActivity implements AboutContract.AboutIView {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_pen)
    ImageView ivPen;
    @BindView(R.id.iv_dot)
    ImageView ivDot;
    @BindView(R.id.version_name)
    TextView vName;
    private AboutContract.AboutIPresenter mPresenter;
    private String versionName;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    protected void onViewCreated(Bundle savedInstanceState) {
        versionName = DeviceUtils.getVersionName(this);
        initPresenter();
    }

    @Override
    protected void initStateAndData() {
        tvTitle.setText(getString(R.string.au_title));
        vName.setText(versionName);
        ivPen.setVisibility(View.GONE);
    }

    @Override
    protected void initListener() {

    }

    private void initPresenter(){
        mPresenter = new AboutPresenterImpl(this).attach(this);
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
                CustomizedToast.showLong(this, getString(R.string.already_new));
                mPresenter.checkVersion();
                break;
        }
    }
}
