package com.newchinese.smartmeeting.ui.mine.fragment;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.base.BaseSimpleFragment;
import com.newchinese.smartmeeting.entity.bean.LoginData;
import com.newchinese.smartmeeting.ui.mine.activity.AboutActivity;
import com.newchinese.smartmeeting.ui.mine.activity.FBActivity;
import com.newchinese.smartmeeting.ui.mine.activity.SettingActivity;
import com.newchinese.smartmeeting.util.GreenDaoUtil;
import com.newchinese.smartmeeting.util.SharedPreUtils;

/**
 * Description:   我的页Fragment
 * author         xulei
 * Date           2017/8/17 17:30
 */
public class MineFragment extends BaseSimpleFragment implements View.OnClickListener {
    private ConstraintLayout mClMore;
    private ImageView mIvIcon;
    private TextView mTvNick;
    private TextView mTvTel;
    private RelativeLayout mRlPen;
    private TextView mTvFb;
    private TextView mTvAbout;
    private TextView mTvPen;

    public MineFragment() {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void onFragViewCreated() {
        mClMore = (ConstraintLayout) mView.findViewById(R.id.cl_mine_more);
        mIvIcon = (ImageView) mView.findViewById(R.id.iv_mine_icon);
        mTvNick = (TextView) mView.findViewById(R.id.tv_mine_nick);
        mTvTel = (TextView) mView.findViewById(R.id.tv_mine_tel);

        mRlPen = (RelativeLayout) mView.findViewById(R.id.rl_mine_pen);
        mTvPen = (TextView) mView.findViewById(R.id.tv_mine_pen);
        mTvFb = (TextView) mView.findViewById(R.id.tv_mine_fb);
        mTvAbout = (TextView) mView.findViewById(R.id.tv_mine_about);
    }

    @Override
    protected void initStateAndData() {
    }

    @Override
    protected void initListener() {
        mClMore.setOnClickListener(this);
        mRlPen.setOnClickListener(this);
        mTvFb.setOnClickListener(this);
        mTvAbout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cl_mine_more://设置
                startActivity(new Intent(mActivity, SettingActivity.class));
                break;
            case R.id.rl_mine_pen://笔信息
                break;
            case R.id.tv_mine_fb://问题反馈
                startActivity(new Intent(getActivity(), FBActivity.class));
                break;
            case R.id.tv_mine_about://关于我们
                startActivity(new Intent(getActivity(), AboutActivity.class));
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LoginData data = GreenDaoUtil.getInstance().getDaoSession().getLoginDataDao().queryBuilder().unique();
        if (data != null) {
            Glide.with(this).load(data.icon)
                    .apply(new RequestOptions().centerCrop().placeholder(R.mipmap.default_mine)
                            .error(R.mipmap.default_mine))
                    .into(mIvIcon);
            mTvNick.setText(data.nickname);
            if (!TextUtils.isEmpty(data.tel)) {
                String tel = data.tel.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
                mTvTel.setText(tel);
            }
        }
        String penName = SharedPreUtils.getString("connectBluInfo_name");
        mTvPen.setText(TextUtils.isEmpty(penName) ? getString(R.string.not_connect) : penName);
    }
}
