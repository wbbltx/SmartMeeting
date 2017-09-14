package com.newchinese.smartmeeting.ui.login.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.base.BaseSimpleActivity;
import com.newchinese.smartmeeting.constant.Constant;
import com.newchinese.smartmeeting.database.LoginDataDao;
import com.newchinese.smartmeeting.entity.bean.LoginData;
import com.newchinese.smartmeeting.ui.login.adapter.GuidePicVpAdapter;
import com.newchinese.smartmeeting.ui.main.activity.MainActivity;
import com.newchinese.smartmeeting.util.GreenDaoUtil;
import com.newchinese.smartmeeting.util.SharedPreUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Description:   引导页
 * author         xulei
 * Date           2017/9/14 11:24
 */
public class GuideActivity extends BaseSimpleActivity {

    @BindView(R.id.vp_guide)
    ViewPager vpGuide;
    @BindView(R.id.btn_feel)
    ImageButton btnFeel;
    private GuidePicVpAdapter adapter;
    private List<View> viewList;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_guide;
    }

    @Override
    protected void onViewCreated(Bundle savedInstanceState) {

    }

    @Override
    protected void initStateAndData() {
        SharedPreUtils.setBoolean("isFirstInstall", false); //首次安装标记置否
        viewList = new ArrayList<>();
        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 0; i < Constant.pics.length; i++) {
            View view = inflater.inflate(R.layout.item_guide_vp, null);
            viewList.add(view);
        }
        adapter = new GuidePicVpAdapter(viewList);
        vpGuide.setAdapter(adapter);
    }

    @Override
    protected void initListener() {
        vpGuide.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == (viewList.size() - 1)) {
                    btnFeel.setVisibility(View.VISIBLE);
                } else {
                    btnFeel.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @OnClick(R.id.btn_feel)
    public void onViewClicked() {
        jumpActivity();
    }


    public void jumpActivity() {
        Intent intent;
        LoginDataDao loginDataDao = GreenDaoUtil.getInstance().getLoginDataDao();
        LoginData loginData = loginDataDao.queryBuilder().unique();
        //判断是否登录过
        if (loginData != null && loginData.getCode() != null && !loginData.getCode().isEmpty()) {
            intent = new Intent(this, MainActivity.class);
        } else {
            intent = new Intent(this, LoginActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
