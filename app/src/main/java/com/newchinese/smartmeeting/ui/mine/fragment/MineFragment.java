package com.newchinese.smartmeeting.ui.mine.fragment;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.base.BaseSimpleFragment;
import com.newchinese.smartmeeting.model.bean.LoginData;
import com.newchinese.smartmeeting.util.GreenDaoUtil;

/**
 * Description:   我的页Fragment
 * author         xulei
 * Date           2017/8/17 17:30
 */
public class MineFragment extends BaseSimpleFragment implements View.OnClickListener {
    private static final String TITLE = "title";

    private String title;
    private ConstraintLayout mClMore;
    private ImageView mIvIcon;
    private TextView mTvNick;
    private TextView mTvTel;
    private RelativeLayout mRlPen;
    private TextView mTvFb;
    private TextView mTvAbout;

    public MineFragment() {
    }

    public static MineFragment newInstance(String title) {
        MineFragment fragment = new MineFragment();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(title);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_mine;
    }

    @Override
    protected void onFragViewCreated() {
        mClMore = (ConstraintLayout) mView.findViewById(R.id.cl_mine_more);
        mIvIcon = (ImageView) mView.findViewById(R.id.iv_mine_icon);
        mTvNick = (TextView) mView.findViewById(R.id.tv_mine_nick);
        mTvTel = (TextView) mView.findViewById(R.id.tv_mine_tel);

        mRlPen = (RelativeLayout) mView.findViewById(R.id.rl_mine_pen);
        mTvFb = (TextView) mView.findViewById(R.id.tv_mine_fb);
        mTvAbout = (TextView) mView.findViewById(R.id.tv_mine_about);
    }

    @Override
    protected void initStateAndData() {
        LoginData data = GreenDaoUtil.getInstance().getDaoSession().getLoginDataDao().queryBuilder().unique();
        if (data != null) {
            Glide.with(this).load(data.icon).apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE).centerCrop().placeholder(R.mipmap.mine_icon).error(R.mipmap.mine_icon)).into(mIvIcon);
            mTvNick.setText(data.nickname);
            String tel = data.tel.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
            mTvTel.setText(tel);
        }
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
                break;
            case R.id.rl_mine_pen://笔信息
                break;
            case R.id.tv_mine_fb://问题反馈
                break;
            case R.id.tv_mine_about://关于我们
                break;
        }
    }
}
