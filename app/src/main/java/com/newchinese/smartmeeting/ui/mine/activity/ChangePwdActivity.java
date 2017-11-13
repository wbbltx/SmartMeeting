package com.newchinese.smartmeeting.ui.mine.activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.contract.MineContract;
import com.newchinese.smartmeeting.entity.bean.LoginData;
import com.newchinese.smartmeeting.presenter.mine.UpdatePresenterImpl;
import com.newchinese.smartmeeting.util.CustomizedToast;
import com.newchinese.smartmeeting.util.GreenDaoUtil;
import com.newchinese.smartmeeting.widget.EditView;

/**
 * Description:   修改或设置密码
 * author         xulei
 * Date           2017/9/26 11:19
 */
public class ChangePwdActivity extends AppCompatActivity implements MineContract.UpdateIVIew, View.OnClickListener, EditView.OnEditViewListener {
    private TextView tvTitle;
    private EditView mEv1, mEv2, mEv3;
    private Button mBtnSub;
    private ProgressDialog mPd;
    private MineContract.UpdateIPresenter mPresenter;
    private boolean hasPassword;
    private TextView tvRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pwd);
        initIntent();
        initPresenter();
        initView();
        initListener();
    }

    private void initIntent() {
        hasPassword = getIntent().getBooleanExtra("has_password", true);
    }

    private void initPresenter() {
        mPresenter = new UpdatePresenterImpl().attach(this);
    }

    private void initView() {
        findViewById(R.id.iv_pen).setVisibility(View.GONE);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        mEv1 = (EditView) findViewById(R.id.ev_update_1);
        mEv2 = (EditView) findViewById(R.id.ev_update_2);
        mEv3 = (EditView) findViewById(R.id.ev_update_3);
        tvRight = (TextView) findViewById(R.id.tv_right);

        mEv1.configure(getString(R.string.old_password), null).setEyeMode(true);
        mEv2.configure(getString(R.string.new_password), null).setEyeMode(true);
        mEv3.configure(getString(R.string.fill_password_again), null).setEyeMode(true);

        mEv1.setEditType(EditView.EDIT_TYPE_PASS);
        mEv2.setEditType(EditView.EDIT_TYPE_PASS);
        mEv3.setEditType(EditView.EDIT_TYPE_PASS);

        mEv1.setBackColor(getResources().getColor(R.color.colorWhite));
        mEv2.setBackColor(getResources().getColor(R.color.colorWhite));
        mEv3.setBackColor(getResources().getColor(R.color.colorWhite));

        tvRight.setVisibility(View.VISIBLE);
        tvRight.setTextColor(getResources().getColor(R.color.gray9));
        tvRight.setText(getString(R.string.save));
        if (hasPassword) {
            tvTitle.setText(getString(R.string.change_password));
            mEv1.setVisibility(View.VISIBLE);
        } else {
            tvTitle.setText(getString(R.string.set_password));
            mEv1.setVisibility(View.GONE);
        }
    }

    private void initListener() {
        tvRight.setOnClickListener(this);
        mEv1.setOnEditViewListener(this);
        mEv2.setOnEditViewListener(this);
        mEv3.setOnEditViewListener(this);
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_right:
                if (mPresenter != null) {
                    if (hasPassword) { //修改密码
                        if (!mEv1.mMatching || !mEv2.mMatching) {
                            CustomizedToast.showShort(this, getString(R.string.wrong_password));
                            break;
                        } else if (!mEv2.getText().equals(mEv3.getText())) {
                            CustomizedToast.showShort(this, getString(R.string.wrong_password1));
                            break;
                        }
                        mPresenter.updatePass(mEv1.getText(), mEv2.getText());
                    } else { //设置密码
                        if (!mEv2.mMatching) {
                            CustomizedToast.showShort(this, getString(R.string.wrong_password));
                            break;
                        } else if (!mEv2.getText().equals(mEv3.getText())) {
                            CustomizedToast.showShort(this, getString(R.string.wrong_password1));
                            break;
                        }
                        LoginData data = GreenDaoUtil.getInstance().getDaoSession().getLoginDataDao().queryBuilder().unique();
                        mPresenter.setPass(data.getTel(), mEv2.getText());
                    }
                }
                break;
        }
    }

    @Override
    public void showLoading(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isFinishing()) {
                    mPd = mPd == null ? new ProgressDialog(ChangePwdActivity.this) : mPd;
                    if (!TextUtils.isEmpty(msg)) {
                        mPd.setMessage(msg);
                        mPd.show();
                    } else {
                        if (mPd.isShowing()) {
                            mPd.dismiss();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void jumpLogin(String type) {
        if ("set".equals(type)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ChangePwdActivity.this, getString(R.string.set_success), Toast.LENGTH_SHORT).show();
                }
            });
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPd != null && mPd.isShowing()) {
            mPd.dismiss();
        }
        if (mPresenter != null) {
            mPresenter.detach();
        }
    }

    @Override
    public void onMatch(EditView view, boolean matching) {
        updateBtn();
    }

    @Override
    public void onEndClick(View view) {

    }

    @Override
    public void onEditError(String err) {

    }

    private void updateBtn() {
        boolean enabled;
        if (hasPassword){
            enabled = (mEv1.mMatching && mEv2.mMatching && mEv3.mMatching);
        } else {
            enabled = (mEv2.mMatching && mEv3.mMatching);
        }
        tvRight.setEnabled(enabled);
//        ((GradientDrawable) mBtnReg.getBackground()).setColor(enabled ? getResources().getColor(R.color.simple_blue) : Color.parseColor("#999999"));
        tvRight.setTextColor(enabled ? getResources().getColor(R.color.colorWhite) : Color.parseColor("#999999"));
    }

}
