package com.newchinese.smartmeeting.ui.meeting.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.base.BaseActivity;
import com.newchinese.smartmeeting.contract.RecordPlayContracr;
import com.newchinese.smartmeeting.presenter.meeting.RecordPlayPresenter;
import com.xw.repo.BubbleSeekBar;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 录屏播放activity
 */
public class RecordPlayActivity extends BaseActivity<RecordPlayPresenter, View> implements RecordPlayContracr.View<View>, MediaPlayer.OnCompletionListener {


    @BindView(R.id.videoview)
    VideoView videoView;
    private String recordPath;
    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.iv_pen)
    ImageView iv_pen;
    //    @BindView(R.id.record_play_seekbar)
//    BubbleSeekBar recordPlaySeekbar;
    private MediaController mediaController;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_record_play;
    }

    @Override
    protected void initStateAndData() {
        tv_title.setText("录屏播放");
        iv_pen.setVisibility(View.GONE);
        Intent intent = getIntent();
        recordPath = intent.getStringExtra("recordPath");

        mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        mediaController.setMediaPlayer(videoView);

        videoView.setVideoPath(recordPath);
        videoView.start();
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected RecordPlayPresenter initPresenter() {
        return null;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @OnClick({R.id.iv_back})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;
        }
    }
}
