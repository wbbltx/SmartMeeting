package com.newchinese.smartmeeting.ui.main.activity;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.newchinese.coolpensdk.listener.OnPointListener;
import com.newchinese.coolpensdk.manager.DrawingboardAPI;
import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.base.BaseActivity;
import com.newchinese.smartmeeting.base.BaseSimpleFragment;
import com.newchinese.smartmeeting.contract.MainActContract;
import com.newchinese.smartmeeting.entity.event.OnPageIndexChangedEvent;
import com.newchinese.smartmeeting.entity.event.OnPointCatchedEvent;
import com.newchinese.smartmeeting.entity.event.OnStrokeCatchedEvent;
import com.newchinese.smartmeeting.presenter.main.MainPresenter;
import com.newchinese.smartmeeting.ui.meeting.activity.DrawingBoardActivity;
import com.newchinese.smartmeeting.ui.meeting.fragment.MeetingFragment;
import com.newchinese.smartmeeting.ui.mine.fragment.MineFragment;
import com.newchinese.smartmeeting.ui.record.fragment.RecordsFragment;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;

/**
 * Description:   入口Activity，主界面
 * author         xulei
 * Date           2017/8/17 17:05
 */
public class MainActivity extends BaseActivity<MainPresenter, BluetoothDevice> implements MainActContract.View<BluetoothDevice>,
        RadioGroup.OnCheckedChangeListener, OnPointListener {
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
    private DrawingboardAPI drawingboardAPI;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected MainPresenter initPresenter() {
        return new MainPresenter();
    }

    @Override
    protected void initStateAndData() {
        //初始化7张分类记录表
        mPresenter.initNoteRecord();
        //初始化SD卡目录
        mPresenter.createSDCardDirectory();
        //初始化书写SDK
        drawingboardAPI = DrawingboardAPI.getInstance();
        //初始化bar状态
        ivBack.setVisibility(View.GONE);
        ivPen.setVisibility(View.GONE);
        tvTitle.setText("会议");
        //初始化Radio和Fragment状态
        ((RadioButton) rgMain.getChildAt(1)).setChecked(true);
        fragmentManager = getSupportFragmentManager();
        recordsFragment = new RecordsFragment();
        meetingFragemnt = new MeetingFragment();
        mineFragment = new MineFragment();
        fragmentManager.beginTransaction().add(R.id.fl_container, meetingFragemnt).commit();
        nowFragment = meetingFragemnt; //当前添加的为RecordsFragment
    }

    @Override
    protected void initListener() {
        rgMain.setOnCheckedChangeListener(this);
        drawingboardAPI.setOnPointListener(this);
    }

    /**
     * 切换Fragment
     * 防止重复add或replaace
     */
    public void changeFragment(BaseSimpleFragment fragment) {
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

    /**
     * RadioButon切换回调
     */
    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        InputMethodManager imm = (InputMethodManager) findViewById(R.id.rl_draw_base).getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(findViewById(R.id.rl_draw_base).getApplicationWindowToken(), 0);
        }
        ivBack.setVisibility(View.GONE);
        ivPen.setVisibility(View.GONE);
        switch (checkedId) {
            case R.id.rb_records:
                changeFragment(recordsFragment);
                tvTitle.setText("会议记录");
                break;
            case R.id.rb_meeting:
                changeFragment(meetingFragemnt);
                tvTitle.setText("会议");
                break;
            case R.id.rb_mine:
                changeFragment(mineFragment);
                tvTitle.setText("我的");
                break;
        }
    }

    /**
     * 收到点回调
     */
    @Override
    public void onPointCatched(int fromType, com.newchinese.coolpensdk.entity.NotePoint notePoint) {
//        Log.e("test_point", "onPointCatched：" + notePoint.toString());
        //存线点
        mPresenter.saveStrokeAndPoint(notePoint);
        //会议页才可跳页，其他的地方书写则只存数据库
        if (nowFragment == meetingFragemnt) {
            mPresenter.checkjumpDrawingBoard(notePoint); //缓存第一笔并检查当前是否在画板页，不在则jump
            EventBus.getDefault().post(new OnPointCatchedEvent(fromType, notePoint));
        }
    }

    /**
     * 收到线回调8
     */
    @Override
    public void onStrokeCached(int fromType, com.newchinese.coolpensdk.entity.NoteStroke noteStroke) {
//        Log.e("test_point", "onStrokeCached：" + noteStroke.toString());
        EventBus.getDefault().post(new OnStrokeCatchedEvent(fromType, noteStroke));
    }

    /**
     * 收到换页回调
     */
    @Override
    public void onPageIndexChanged(int fromType, com.newchinese.coolpensdk.entity.NotePoint notePoint) {
//        Log.e("test_point", "onPageIndexChanged：" + notePoint.toString());
        //存页
        mPresenter.savePage(notePoint);
        //保存录屏期间翻的页
        mPresenter.saveRecordPage(notePoint.getPageIndex());
        EventBus.getDefault().post(new OnPageIndexChangedEvent(fromType, notePoint));
    }

    /**
     * 跳画板页
     */
    @Override
    public void jumpDrawingBoard() {
        startActivity(new Intent(this, DrawingBoardActivity.class));
    }

    @Override
    public void showToast(String toastMsg) {
        Toast.makeText(mContext, toastMsg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //刷新记录数据
        if (recordsFragment != null && recordsFragment.isAdded()) {
            ((RecordsFragment) recordsFragment).refreshData();
        }
    }
}
