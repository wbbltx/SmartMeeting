package com.newchinese.smartmeeting.ui.meeting.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.base.BaseActivity;
import com.newchinese.smartmeeting.contract.RecordPlayContracr;
import com.newchinese.smartmeeting.presenter.meeting.RecordPlayPresenter;
import com.xw.repo.BubbleSeekBar;

import butterknife.BindView;

/**
 * 录屏播放activity
 */
public class RecordPlayActivity extends BaseActivity<RecordPlayPresenter,View> implements RecordPlayContracr.View<View> {


//    @BindView(R.id.videoview)
//    VideoView videoView;
//    @BindView(R.id.record_play)
//    ImageView recordPlay;
//    @BindView(R.id.record_play_starttime)
//    TextView recordPlayStarttime;
//    @BindView(R.id.record_play_endtime)
//    TextView recordPlayEndtime;
//    @BindView(R.id.record_play_seekbar)
//    BubbleSeekBar recordPlaySeekbar;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_record_play;
    }

    @Override
    protected void initStateAndData() {

    }

    @Override
    protected void initListener() {

    }

    @Override
    protected RecordPlayPresenter initPresenter() {
        return null;
    }
}
