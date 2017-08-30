package com.newchinese.smartmeeting.ui.mine.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.contract.MineContract;
import com.newchinese.smartmeeting.presenter.mine.UpdatePresenterImpl;
import com.newchinese.smartmeeting.util.CustomizedToast;
import com.newchinese.smartmeeting.widget.EditView;

public class UpdateActivity extends AppCompatActivity implements MineContract.UpdateIVIew, View.OnClickListener {

    private EditView mEv1, mEv2, mEv3;
    private Button mBtnSub;
    private ProgressDialog mPd;
    private MineContract.UpdateIPresenter mPresenter;
    private int mType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_update);
        initIntent();
        setTitle("修改密码");
        super.onCreate(savedInstanceState);
        initPresenter();
        initView();
        initListener();
    }

    private void initIntent() {
        mType = getIntent().getIntExtra("type", 0);
    }

    private void initPresenter() {
        mPresenter = new UpdatePresenterImpl().attach(this);
    }

    private void initListener() {
        mBtnSub.setOnClickListener(this);
    }

    private void initView() {
        mEv1 = (EditView) findViewById(R.id.ev_update_1);
        mEv2 = (EditView) findViewById(R.id.ev_update_2);
        mEv3 = (EditView) findViewById(R.id.ev_update_3);
        mBtnSub = (Button) findViewById(R.id.btn_update_sub);

        mEv2.setVisibility(mType == 0 ? View.GONE : View.VISIBLE);
        mEv3.setVisibility(mType == 0 ? View.GONE : View.VISIBLE);

        mEv1.configure(mType == 0 ? "最长包含9个汉字字符" : "原密码", null);
        mEv2.configure(mType == 0 ? "" : "新密码", null);
        mEv3.configure(mType == 0 ? "" : "再次输入密码", null);

        mEv1.setEditType(mType == 0 ? -1 : EditView.EDIT_TYPE_PASS);
        mEv2.setEditType(EditView.EDIT_TYPE_PASS);
        mEv3.setEditType(EditView.EDIT_TYPE_PASS);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_update_sub:
                if (mPresenter != null) {
                    if (mType == 0) {
                        if (mEv1.mMatching) {
                            mPresenter.updateNick(mEv1.getText());
                        } else {
                            CustomizedToast.showShort(this, "昵称不能为空");
                        }
                    } else {
                        if (!mEv1.mMatching || !mEv2.mMatching || !mEv2.getText().equals(mEv3.getText())) {
                            CustomizedToast.showShort(this, "密码格式错误");
                            break;
                        }
                        mPresenter.updatePass(mEv1.getText(), mEv2.getText());
                    }
                }
                break;
        }
    }

    @Override
    public void showLoading(String msg) {
        mPd = mPd == null ? new ProgressDialog(this) : mPd;
        if (!TextUtils.isEmpty(msg)) {
            mPd.setMessage(msg);
            mPd.show();
        } else {
            if (mPd.isShowing()) {
                mPd.dismiss();
            }
        }
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
}
