package com.newchinese.smartmeeting.ui.mine.activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.base.BaseToolbar;
import com.newchinese.smartmeeting.contract.MineContract;
import com.newchinese.smartmeeting.presenter.mine.UpdatePresenterImpl;
import com.newchinese.smartmeeting.util.CustomizedToast;

public class FBActivity extends AppCompatActivity implements MineContract.UpdateIVIew, BaseToolbar, View.OnClickListener, TextWatcher {

    private EditText mEtContact;
    private EditText mEtContent;
    private TextView mBtnSub;
    //    private String regexp = "(^0{0,1}(13[0-9]|15[7-9]|153|156|18[7-9])[0-9]{8}$)|(^[a-z0-9!#$%&'*+\\/=?^_`{|}~.-]+@[a-z0-9]([a-z0-9-]*[a-z0-9])?(\\.[a-z0-9]([a-z0-9-]*[a-z0-9])?)*$)";
    private String regexp = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8}$";
    private String regemail = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
    private MineContract.UpdateIPresenter mPresenter;
    private ProgressDialog mPd;
    private java.lang.String TAG = "FBActivity";

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
        mEtContent.addTextChangedListener(this);
        mEtContact.addTextChangedListener(this);
    }

    private void initView() {
        mEtContact = (EditText) findViewById(R.id.et_fb_contact);
        mEtContent = (EditText) findViewById(R.id.et_fb_content);
        mBtnSub = (TextView) findViewById(R.id.btn_fb_sub);
        updateBtn(false);
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
            CustomizedToast.showShort(this, getString(R.string.please_fill_fb));
            return;
        }

        String contact = mEtContact.getText().toString().trim();
        if (!contact.matches(regexp) && !contact.matches(regemail)) {
            CustomizedToast.showShort(this, getString(R.string.wrong_connect));
            return;
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
    public void jumpLogin(String type) {
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
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        boolean b = mEtContent.getText().toString().trim().length() > 0 && (mEtContact.getText().toString().trim().matches(regexp) || mEtContact.getText().toString().trim().matches(regemail));
        updateBtn(b);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void updateBtn(boolean enabled){
        mBtnSub.setEnabled(enabled);
//        ((GradientDrawable) mBtnSub.getBackground()).setColor(enabled ? getResources().getColor(R.color.simple_blue) : Color.parseColor("#999999"));
    }
}
