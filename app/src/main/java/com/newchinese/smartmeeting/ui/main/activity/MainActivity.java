package com.newchinese.smartmeeting.ui.main.activity;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.base.BaseSimpleActivity;
import com.newchinese.smartmeeting.base.BaseSimpleFragment;
import com.newchinese.smartmeeting.ui.meeting.fragment.MeetingFragment;
import com.newchinese.smartmeeting.ui.mine.fragment.MineFragment;
import com.newchinese.smartmeeting.ui.record.fragment.RecordsFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Description:   入口Activity，主界面
 * author         xulei
 * Date           2017/8/17 17:05
 */
public class MainActivity extends BaseSimpleActivity {
    @BindView(R.id.fl_container)
    FrameLayout flContainer;
    @BindView(R.id.rg_main)
    RadioGroup rgMain;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_pen)
    ImageView ivPen;

    private FragmentManager fragmentManager;
    private BaseSimpleFragment nowFragment, recordsFragment, meetingFragemnt, mineFragment;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onViewCreated(Bundle savedInstanceState) {
    }

    @Override
    protected void initStateAndData() {
        ((RadioButton) rgMain.getChildAt(1)).setChecked(true);
        fragmentManager = getSupportFragmentManager();
        recordsFragment = RecordsFragment.newInstance("记录");
        meetingFragemnt = new MeetingFragment();
        mineFragment = MineFragment.newInstance("我的");
        fragmentManager.beginTransaction().add(R.id.fl_container, meetingFragemnt).commit();
        nowFragment = meetingFragemnt; //当前添加的为RecordsFragment
        ivBack.setVisibility(View.GONE);
        ivPen.setVisibility(View.GONE);
        tvTitle.setText("会议");
    }

    @Override
    protected void initListener() {
        rgMain.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                ivBack.setVisibility(View.GONE);
                ivPen.setVisibility(View.GONE);
                switch (checkedId) {
                    case R.id.rb_records:
                        showFragment(recordsFragment);
                        tvTitle.setText("会议记录");
                        break;
                    case R.id.rb_meeting:
                        showFragment(meetingFragemnt);
                        tvTitle.setText("会议");
                        break;
                    case R.id.rb_mine:
                        showFragment(mineFragment);
                        tvTitle.setText("我");
                        break;
                }
            }
        });
    }

    /**
     * 切换Fragment
     * 防止重复add或replaace
     */
    public void showFragment(BaseSimpleFragment fragment) {
        //判断要切换的Fragment当前是否已经显示
        if (nowFragment != fragment) {
            //判断切换的Fragment是否已经添加过
            if (!fragment.isAdded()) {
                //未添加过则隐藏当前Fragment，添加切换Fragment
                fragmentManager.beginTransaction().hide(nowFragment)
                        .add(R.id.fl_container, fragment).commit();
            } else {
                //添加过则隐藏当前Fragment，显示切换Fragment
                fragmentManager.beginTransaction().hide(nowFragment).show(fragment).commit();
            }
            //更换当前Fragment
            nowFragment = fragment;
        }
    }
}
