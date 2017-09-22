package com.newchinese.smartmeeting.ui.login.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.app.App;
import com.newchinese.smartmeeting.contract.LoginContract;
import com.newchinese.smartmeeting.util.CustomizedToast;
import com.newchinese.smartmeeting.widget.EditView;

/**
 * Created by Administrator on 2017-08-24.
 */

public class LoginPageAdapter extends PagerAdapter implements EditView.OnEditViewListener, View.OnClickListener {
    private Context context;
    private String[] titles;
    private EditView[] mEvPhone = new EditView[2], mEv3 = new EditView[2], mEvPass = new EditView[2], mEv4 = new EditView[2];
    private Button[] mBtnReg = new Button[2];
    private OnPageInnerClickListener mListener;
    private TextView[] mTvSkip = new TextView[2];
    private LoginContract.LoginIPresenter mPresenter;

    public LoginPageAdapter(Context context) {
        this.context = context;
        titles = new String[]{context.getString(R.string.quick_login), context.getString(R.string.simple_login)};
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = View.inflate(container.getContext(), R.layout.regist_layout_main, null);
        initView(position, view);
        initListener(position);
        container.addView(view);
        return view;
    }

    private void initView(int position, View view) {
        mEvPhone[position] = (EditView) view.findViewById(R.id.ev_regist_1);
        mEvPass[position] = (EditView) view.findViewById(R.id.ev_regist_2);
        mEv3[position] = (EditView) view.findViewById(R.id.ev_regist_3);
        mEv4[position] = (EditView) view.findViewById(R.id.ev_regist_4);
        mBtnReg[position] = (Button) view.findViewById(R.id.btn_regist_sub);
        mTvSkip[position] = (TextView) view.findViewById(R.id.tv_regist_bottom);

        mEv3[position].setVisibility(View.GONE);
        mEv4[position].setVisibility(View.GONE);

        mEvPhone[position].setEditType(EditView.EDIT_TYPE_PHONE);
        mEvPass[position].setEditType(EditView.EDIT_TYPE_PASS);

        mEvPhone[position].configure(context.getString(R.string.tel_number), "");
        mEvPass[position].configure(position == 0 ? context.getString(R.string.active_password) :
                context.getString(R.string.password), position == 0 ? context.getString(R.string.get_active_password)
                : context.getString(R.string.forget_password));

        mEvPhone[position].setTag(position);
        mEvPass[position].setTag(position);
        mBtnReg[position].setTag(position);

        mBtnReg[position].setText(context.getString(R.string.login_space));

        if (mPresenter != null) {
            mPresenter.getSpan(mTvSkip[position], position == 1 ? context.getString(R.string.no_account_to_regist) : "");
        }

        updateBtn(position);
    }

    private void initListener(int position) {
        mBtnReg[position].setOnClickListener(this);
        mEvPhone[position].setOnEditViewListener(this);
        mEvPass[position].setOnEditViewListener(this);
    }

    @Override
    public void onMatch(EditView view, boolean matching) {
        updateBtn((Integer) view.getTag());
    }

    @Override
    public void onEndClick(View v) {
        switch (v.getId()) {
            case R.id.ev_regist_2://密码相关事件
                if (mListener != null) {
                    int position = (int) v.getTag();
                    if (position == 0 && !mEvPhone[position].mMatching) {
                        CustomizedToast.showShort(App.getAppliction(), context.getString(R.string.wrong_tel));
                        break;
                    }
                    v.setTag(R.id.ev_regist_1, mEvPhone[position].getText());
                    mListener.onInnerClick(v, position);
                }
                break;
        }
    }

    @Override
    public void onEditError(String err) {
        CustomizedToast.showShort(App.getAppliction(), err);
    }

    private void updateBtn(int position) {
        boolean enabled = mEvPhone[position].mMatching && mEvPass[position].mMatching;
        mBtnReg[position].setEnabled(enabled);
        ((GradientDrawable) mBtnReg[position].getBackground()).setColor(enabled ? Color.parseColor("#3D82E0") : Color.parseColor("#999999"));
    }

    public LoginPageAdapter setOnPageInnerClickListener(OnPageInnerClickListener listener) {
        mListener = listener;
        return this;
    }

    public LoginPageAdapter setPresenter(LoginContract.LoginIPresenter presenter) {
        mPresenter = presenter;
        return this;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_regist_sub:
                int tag = (int) v.getTag();
                if (mListener != null) {
                    mListener.onLogin(mEvPhone[tag].getText(), mEvPass[tag].getText(), tag);
                }
                break;
        }
    }

    public interface OnPageInnerClickListener {

        /**
         * position 0: 快捷登录；1：普通登录；
         *
         * @param v
         * @param position
         */
        void onInnerClick(View v, int position);

        void onLogin(String phone, String pass, int position);
    }
}
