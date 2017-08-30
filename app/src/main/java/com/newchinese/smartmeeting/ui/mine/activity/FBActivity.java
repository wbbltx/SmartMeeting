package com.newchinese.smartmeeting.ui.mine.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.base.IToolbar;
import com.newchinese.smartmeeting.contract.MineContract;
import com.newchinese.smartmeeting.presenter.mine.UpdatePresenterImpl;
import com.newchinese.smartmeeting.util.CustomizedToast;

public class FBActivity extends AppCompatActivity implements MineContract.UpdateIVIew, IToolbar, View.OnClickListener {

    private EditText mEtContact;
    private EditText mEtContent;
    private Button mBtnSub;
    private String regexp = "(^0{0,1}(13[0-9]|15[7-9]|153|156|18[7-9])[0-9]{8}$)|(^[a-z0-9!#$%&'*+\\/=?^_`{|}~.-]+@[a-z0-9]([a-z0-9-]*[a-z0-9])?(\\.[a-z0-9]([a-z0-9-]*[a-z0-9])?)*$)";
    private MineContract.UpdateIPresenter mPresenter;
    private ProgressDialog mPd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_fb);
        super.onCreate(savedInstanceState);
        initView();
        initListener();
        initPresenter();
    }

    private void initPresenter() {
        mPresenter = new UpdatePresenterImpl().attach(this);
    }

    private void initListener() {
        mBtnSub.setOnClickListener(this);
    }

    private void initView() {
        mEtContact = (EditText) findViewById(R.id.et_fb_contact);
        mEtContent = (EditText) findViewById(R.id.et_fb_content);
        mBtnSub = (Button) findViewById(R.id.btn_fb_sub);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_fb_sub:
                submit();
                break;
        }
    }

    private void submit() {
        String content = mEtContent.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            CustomizedToast.showShort(this, "请填写反馈内容");
            return ;
        }

        String contact = mEtContact.getText().toString().trim();
        if (!contact.matches(regexp)) {
            CustomizedToast.showShort(this, "联系方式不正确");
            return ;
        }

        mPresenter.feedBack(content, contact);
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
