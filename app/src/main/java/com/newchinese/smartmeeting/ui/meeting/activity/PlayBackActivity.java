package com.newchinese.smartmeeting.ui.meeting.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.newchinese.coolpensdk.entity.NotePoint;
import com.newchinese.coolpensdk.manager.DrawingBoardView;
import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.base.BaseActivity;
import com.newchinese.smartmeeting.contract.PlayBackContract;
import com.newchinese.smartmeeting.presenter.meeting.PlayBackPresenter;
import com.newchinese.smartmeeting.util.DataCacheUtil;
import com.newchinese.smartmeeting.util.DateUtils;
import com.newchinese.smartmeeting.util.PlayBackUtil;
import com.xw.repo.BubbleSeekBar;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;

import static com.newchinese.smartmeeting.ui.meeting.activity.DrawingBoardActivity.TAG_PAGE_INDEX;

public class PlayBackActivity extends BaseActivity<PlayBackPresenter, View> implements PlayBackContract.View<View> {


    @BindView(R.id.play_back_drawview)//画板
            DrawingBoardView playBackDrawview;
    @BindView(R.id.play_back_start_time)//左边时间
            TextView playBackStartTime;
    @BindView(R.id.play_back_seekbar)//进度条
            ProgressBar playBackSeekbar;
    @BindView(R.id.play_back_end_time)//右边时间
            TextView playBackEndTime;
    @BindView(R.id.play_back_start)//开始按钮
            ImageView playBackStart;
    @BindView(R.id.tv_title)//标题
            TextView tvTitle;
    private int selectPageIndex;
    private DataCacheUtil dataCacheUtil;
    private int playStatus = 0;//0未播放，1播放中, 2暂停
    private MyTimerTask timerTask;
    private Timer timer;
    private PlayBackUtil netUtil2;
    private Handler handler = new Handler();
    private int progress;
    private ArrayList<NotePoint> drawingPointList;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_play_back;
    }

    @Override
    protected void initStateAndData() {
        dataCacheUtil = DataCacheUtil.getInstance();
        Intent intent = getIntent();
        if (intent.hasExtra(TAG_PAGE_INDEX)) {
            selectPageIndex = intent.getIntExtra("selectPageIndex", 0);
            setTitleText(selectPageIndex); //设置当前页数
            //延时一会儿再加载数据库，防止View还未初始化完毕
            Flowable.timer(500, TimeUnit.MILLISECONDS).subscribe(new Consumer<Long>() {
                @Override
                public void accept(Long aLong) throws Exception {
                    mPresenter.readData(playBackDrawview,selectPageIndex);
                }
            });
        }
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected PlayBackPresenter initPresenter() {
        return new PlayBackPresenter();
    }

    @Override
    public void clearCanvars() {
        playBackDrawview.clearCanvars();
    }

    @Override
    public void setTitleText(int pageIndex) {
        tvTitle.setText("录屏，第"+pageIndex+"页");
    }

    @OnClick({R.id.play_back_start,R.id.iv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back: //返回键
                finish();
                break;
            case R.id.play_back_start: //开始按钮
                playBack();
                break;
        }
    }

    private void playBack() {
        switch (playStatus){
            case 0://未播放
                clearCanvars();
                drawingPointList = new ArrayList<>();
                drawingPointList.addAll(dataCacheUtil.getPlayBackList());
                PlayBackUtil.getInstance().addAllNewsBrief(null, drawingPointList, playBackDrawview);

                playStatus = 1;
                playBackStart.setImageResource(R.mipmap.record_pause);
                playBackSeekbar.setMax(dataCacheUtil.getProgressMax());
                playBackSeekbar.setProgress(0);
                playBackEndTime.setText(DateUtils.getCheckTimeBySeconds(dataCacheUtil.getProgressMax() * 10 / 1000, "0:00:00"));

                timerTask = new MyTimerTask();
                timer = new Timer(true);
                timer.schedule(timerTask, 1000, 10);
                break;

            case 1://播放中
                playStatus = 2;
                timer.cancel();
                playBackStart.setImageResource(R.mipmap.record_play);
                break;

            case 2://暂停
                playStatus = 1;
                playBackStart.setImageResource(R.mipmap.record_pause);
                timerTask = new MyTimerTask();
                timer = new Timer(true);
                timer.schedule(timerTask, 0, 10);
                break;
        }
    }

    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            progress++;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    playBackSeekbar.setProgress(progress);
                    playBackStartTime.setText(DateUtils.getCheckTimeBySeconds(progress * 10 / 1000, "0:00:00"));
                    if (progress >= dataCacheUtil.getProgressMax()) {
                        playBackStart.setImageResource(R.mipmap.record_play);
                        progress = 0;
                        playBackStartTime.setText(DateUtils.getCheckTimeBySeconds(progress * 10 / 1000, "0:00:00"));
                        playStatus = 0;
                        timer.cancel();
                    }
                }
            });
            handler.postDelayed(PlayBackUtil.getInstance(), 1);
        }
    }

    @Override
    protected void onDestroy() {
        timer.cancel();
        timerTask.cancel();
        super.onDestroy();
    }
}
