package com.newchinese.smartmeeting.widget;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.util.CustomizedToast;
import com.newchinese.smartmeeting.util.log.XLog;

/**
 * Created by Administrator on 2017-08-24.
 */

public class EditView extends RelativeLayout implements TextWatcher, View.OnClickListener {

    private final String REGEX_PHONE = "1[3,4,5,7,8]\\d[\\s,-]?\\d{4}[\\s,-]?\\d{4}";
    private final String REGEX_CODE = "^\\S{6}$";
    private final String REGEX_PASS = "^\\S{5,15}$";
    private TextInputLayout mTil;
    private EditText mEt;
    private TextView mTv;
    private OnEditViewListener mListener;
    private boolean isVisible = false;

    public static final int EDIT_TYPE_PHONE = 1;
    public static final int EDIT_TYPE_PASS = 2;
    public boolean mMatching = false;
    private ImageView mIv;
    private boolean isEyeMode = false;

    public EditView(Context context) {
        this(context, null);
    }

    public EditView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        initListener();
    }

    private void initListener() {
        mEt.addTextChangedListener(this);
        mTv.setOnClickListener(this);
        mIv.setOnClickListener(this);
    }

    private void initView(Context context) {

        View.inflate(context, R.layout.login_layout_edit, this);

        mTil = (TextInputLayout) findViewById(R.id.til_edit_content);
        mEt = (EditText) findViewById(R.id.et_edit_content);
        mTv = (TextView) findViewById(R.id.tv_edit_end);
        mIv = (ImageView) findViewById(R.id.iv_edit_end);
    }

    public EditView setMax(int length){
        mEt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(length)});
        return this;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {//分别是手机号码的匹配规则 密码的匹配规则(可见和不可见两种) 验证码的匹配规则
        mMatching = mEt.getInputType() == InputType.TYPE_CLASS_PHONE && s.toString().trim().matches(REGEX_PHONE)
                || mEt.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD) && s.toString().trim().matches(REGEX_PASS)
                || mEt.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD && s.toString().trim().matches(REGEX_PASS)
                || mEt.getInputType() != InputType.TYPE_CLASS_PHONE && mEt.getInputType() != (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD) && s.toString().trim().matches(REGEX_CODE);
//                || mEt.getInputType() != InputType.TYPE_CLASS_PHONE && mEt.getInputType() != (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD) && !TextUtils.isEmpty(mEt.getText().toString().trim());
        if (mListener != null) {
            mListener.onMatch(this, mMatching);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (isEyeMode){
            if (0 == s.length()) {  //输入框为空的时候 显示忘记密码
                mIv.setVisibility(GONE);
                mTv.setVisibility(VISIBLE);
            } else {              //不为空显示小眼睛
                mIv.setVisibility(VISIBLE);
                mTv.setVisibility(GONE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_edit_end:
                if (mListener != null) {
                    mListener.onEndClick(this);
                }
                break;

            case R.id.iv_edit_end:
                setStyle();
                break;
        }
    }

    private void setStyle() {
        isVisible = !isVisible;
        if (isVisible) {
            mEt.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
//            setEditType(EditView.EDIT_TYPE_PASS);
            mIv.setImageResource(R.mipmap.password_show);
        } else {
            setEditType(EditView.EDIT_TYPE_PASS);
            mIv.setImageResource(R.mipmap.password_hide);
        }
    }

    public EditView setImage(int srcId) {
        mIv.setImageResource(srcId);
        return this;
    }

    public EditView setEyeMode(boolean isEyeMode){
        this.isEyeMode = isEyeMode;
        return this;
    }

    public EditView setText(String txt) {
        mEt.setText(txt);
        return this;
    }

    public String getText() {
        return mEt.getText().toString().trim();
    }

    public EditView configure(String hint, String end) {
        mEt.setHint(hint);
        mTv.setText(end);
        mTv.setVisibility(end != null && !TextUtils.isEmpty(end) ? VISIBLE : GONE);
        return this;
    }

    public EditView setEnd(String end, boolean click) {
        mTv.setText(end);
        mTv.setVisibility(end != null && !TextUtils.isEmpty(end) ? VISIBLE : GONE);
        mTv.setClickable(click);
        return this;
    }

    public EditView setOnEditViewListener(OnEditViewListener listener) {
        mListener = listener;
        return this;
    }

    public EditView setEditType(int type) {
        if (type == EDIT_TYPE_PHONE) {
            mEt.setInputType(InputType.TYPE_CLASS_PHONE);
        } else if (type == EDIT_TYPE_PASS) {
            mEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        return this;
    }

    public EditView setBackColor(int color){
        mEt.setBackgroundColor(color);
        return this;
    }

    public interface OnEditViewListener {
        void onMatch(EditView view, boolean matching);

        void onEndClick(View view);

        void onEditError(String err);
    }
}
