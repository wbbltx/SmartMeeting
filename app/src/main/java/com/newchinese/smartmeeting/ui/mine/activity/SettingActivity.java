package com.newchinese.smartmeeting.ui.mine.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.base.BaseSimpleActivity;
import com.newchinese.smartmeeting.database.LoginDataDao;
import com.newchinese.smartmeeting.model.bean.LoginData;
import com.newchinese.smartmeeting.ui.login.activity.LoginActivity;
import com.newchinese.smartmeeting.util.GreenDaoUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 设置Activity
 */
public class SettingActivity extends BaseSimpleActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_pen)
    ImageView ivPen;
    @BindView(R.id.iv_header)
    ImageView ivHeader;
    @BindView(R.id.tv_nick_name)
    TextView tvNickName;
    @BindView(R.id.rl_header)
    RelativeLayout rlHeader;
    @BindView(R.id.rl_nick_name)
    RelativeLayout rlNickName;
    @BindView(R.id.rl_change_pwd)
    RelativeLayout rlChangePwd;
    private LoginDataDao loginDataDao;
    private LoginData loginData;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void onViewCreated(Bundle savedInstanceState) {

    }

    @Override
    protected void initStateAndData() {
        ivPen.setVisibility(View.GONE);
        tvTitle.setText("设置");
        loginDataDao = GreenDaoUtil.getInstance().getLoginDataDao();
    }

    @Override
    protected void initListener() {

    }

    @OnClick({R.id.iv_back, R.id.rl_header, R.id.rl_nick_name, R.id.rl_change_pwd, R.id.btn_exit_login})
    public void onViewClicked(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.iv_back: //返回
                finish();
                break;
            case R.id.rl_header: //修改头像

                break;
            case R.id.rl_nick_name: //修改昵称
                intent = new Intent(SettingActivity.this, ChangeNickNameActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_change_pwd: //修改密码
                intent = new Intent(SettingActivity.this, UpdateActivity.class);
                intent.putExtra("type", 1);
                startActivity(intent);
                break;
            case R.id.btn_exit_login: //退出登录
                //清空用户LoginData表
                GreenDaoUtil.getInstance().getLoginDataDao().deleteAll();
                intent = new Intent(SettingActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loginData = loginDataDao.queryBuilder().unique();
        if (loginData != null) {
            Log.e("test_greendao", "" + loginData.toString());
            tvNickName.setText(loginData.getNickname() + "");
            Glide.with(this)
                    .load(loginData.getIcon())
                    .transition(new DrawableTransitionOptions().crossFade(1000)) //淡入淡出1s
                    .into(ivHeader);
        }
    }
}
