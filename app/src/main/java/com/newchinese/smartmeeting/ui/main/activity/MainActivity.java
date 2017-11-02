package com.newchinese.smartmeeting.ui.main.activity;

import android.app.ActivityManager;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.newchinese.coolpensdk.listener.OnPointListener;
import com.newchinese.coolpensdk.manager.DrawingboardAPI;
import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.base.BaseActivity;
import com.newchinese.smartmeeting.base.BaseSimpleFragment;
import com.newchinese.smartmeeting.constant.Constant;
import com.newchinese.smartmeeting.contract.MainActContract;
import com.newchinese.smartmeeting.entity.event.OnMaskClicked;
import com.newchinese.smartmeeting.entity.event.OnPageIndexChangedEvent;
import com.newchinese.smartmeeting.entity.event.OnPointCatchedEvent;
import com.newchinese.smartmeeting.entity.event.OnStrokeCatchedEvent;
import com.newchinese.smartmeeting.presenter.main.MainPresenter;
import com.newchinese.smartmeeting.ui.meeting.activity.DrawingBoardActivity;
import com.newchinese.smartmeeting.ui.meeting.fragment.MeetingFragment;
import com.newchinese.smartmeeting.ui.mine.fragment.MineFragment;
import com.newchinese.smartmeeting.ui.record.fragment.RecordsFragment;
import com.newchinese.smartmeeting.util.BluCommonUtils;
import com.newchinese.smartmeeting.util.DataCacheUtil;
import com.newchinese.smartmeeting.util.SharedPreUtils;
import com.newchinese.smartmeeting.util.log.XLog;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;

/**
 * Description:   入口Activity，主界面
 * author         xulei
 * Date           2017/8/17 17:05
 */
public class MainActivity extends BaseActivity<MainPresenter, BluetoothDevice> implements MainActContract.View<BluetoothDevice>,
        RadioGroup.OnCheckedChangeListener, OnPointListener, View.OnClickListener {
    @BindView(R.id.rg_main)
    RadioGroup rgMain;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_pen)
    ImageView ivPen;
    @BindView(R.id.mask_one)
    ImageView ivMaskOne;
    private FragmentManager fragmentManager;
    private BaseSimpleFragment nowFragment, recordsFragment, meetingFragemnt, mineFragment;
    private DrawingboardAPI drawingboardAPI;
    private Integer pageIndex;

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
        tvTitle.setText(getString(R.string.meeting));
        //初始化Radio和Fragment状态
        ((RadioButton) rgMain.getChildAt(1)).setChecked(true);
        fragmentManager = getSupportFragmentManager();
        recordsFragment = new RecordsFragment();
        meetingFragemnt = new MeetingFragment();
        mineFragment = new MineFragment();
        fragmentManager.beginTransaction().add(R.id.fl_container, meetingFragemnt).commit();
        nowFragment = meetingFragemnt; //当前添加的为RecordsFragment

        initMaskView();
    }

    @Override
    protected void initListener() {
        rgMain.setOnCheckedChangeListener(this);
        drawingboardAPI.setOnPointListener(this);
        ivMaskOne.setOnClickListener(this);
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
     * 初始化蒙版
     */
    private void initMaskView(){
        if (SharedPreUtils.getBoolean(BluCommonUtils.IS_FIRST_INSTALL, true)) { //首次安装则显示蒙版引导
            ivMaskOne.setVisibility(View.VISIBLE);
        }else {
            ivMaskOne.setVisibility(View.GONE);
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
                tvTitle.setText(getString(R.string.record));
                break;
            case R.id.rb_meeting:
                changeFragment(meetingFragemnt);
                tvTitle.setText(getString(R.string.meeting));
                break;
            case R.id.rb_mine:
                changeFragment(mineFragment);
                tvTitle.setText(getString(R.string.mine));
                break;
        }
    }

    /**
     * 收到点回调
     */
    @Override
    public void onPointCatched(int fromType, com.newchinese.coolpensdk.entity.NotePoint notePoint) {
//        XLog.d("test_point", "onPointCatched：" + notePoint.toString());
        pageIndex = notePoint.getPageIndex();
        //存线点
        mPresenter.saveStrokeAndPoint(notePoint);
        //会议页才可跳页，其他的地方书写则只存数据库
        if (nowFragment == meetingFragemnt) {
            mPresenter.saveCache(notePoint); //缓存第一笔
            jumpDrawingBoard(); //检查当前是否在画板页，不在则jump
            EventBus.getDefault().post(new OnPointCatchedEvent(fromType, notePoint));
        }
    }

    /**
     * 收到线回调
     */
    @Override
    public void onStrokeCached(int fromType, com.newchinese.coolpensdk.entity.NoteStroke noteStroke) {
//        XLog.d("test_point", "onStrokeCached：" + noteStroke.toString());
        EventBus.getDefault().post(new OnStrokeCatchedEvent(fromType, noteStroke));
    }

    /**
     * 收到换页回调-
     */
    @Override
    public void onPageIndexChanged(int fromType, com.newchinese.coolpensdk.entity.NotePoint notePoint) {
//        XLog.d("test_point", "onPageIndexChanged：" + notePoint.toString());
        mPresenter.setWriteRecord();
        //存页
        mPresenter.savePage(notePoint);
        //保存录屏期间翻的页
        mPresenter.saveRecordPage(notePoint.getPageIndex());
        EventBus.getDefault().post(new OnPageIndexChangedEvent(fromType, notePoint));
    }

    /**
     * 跳画板页
     */
    public void jumpDrawingBoard() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTasks = am.getRunningTasks(1);
        ActivityManager.RunningTaskInfo runningTaskInfo = runningTasks.get(0);
        ComponentName topActivity = runningTaskInfo.topActivity;
        if (!DrawingBoardActivity.class.getName().equals(topActivity.getClassName())) {
            Intent intent = new Intent(this, DrawingBoardActivity.class);
            intent.putExtra("selectPageIndex",pageIndex);
            startActivity(intent);
        }
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
        mPresenter.checkRecord();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mask_one:
                ivMaskOne.setVisibility(View.GONE);
                EventBus.getDefault().post(new OnMaskClicked());
                break;
        }
    }
}
