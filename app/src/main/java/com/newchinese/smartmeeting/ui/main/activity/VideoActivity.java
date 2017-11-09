package com.newchinese.smartmeeting.ui.main.activity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.base.BaseSimpleActivity;
import com.newchinese.smartmeeting.util.CustomizedToast;

public class VideoActivity extends BaseSimpleActivity implements View.OnClickListener, MediaPlayer.OnCompletionListener {

    private VideoView videoWelcome;
    private TextView tvEnter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video;
    }

    @Override
    protected void onViewCreated(Bundle savedInstanceState) {
        videoWelcome = (VideoView) findViewById(R.id.vv_welcome);
        tvEnter = (TextView) findViewById(R.id.tv_enter);
    }

    @Override
    protected void initStateAndData() {
        videoWelcome.setVideoURI(Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.welcome));
        videoWelcome.start();
    }

    @Override
    protected void initListener() {
        tvEnter.setOnClickListener(this);
        videoWelcome.setOnCompletionListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_enter:
                CustomizedToast.showShort(this,"点击了");
                finish();
                break;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        videoWelcome.start();
    }
}
