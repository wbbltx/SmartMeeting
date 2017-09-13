package com.newchinese.smartmeeting.entity.listener;


import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.base.BaseToolbar;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by Administrator on 2017-08-24.
 */

public class ActivityLife implements Application.ActivityLifecycleCallbacks {
    @Override
    public void onActivityCreated(final Activity activity, Bundle savedInstanceState) {
        if (BaseToolbar.class.isAssignableFrom(activity.getClass())) {
            Toolbar toolBar = (Toolbar) activity.findViewById(R.id.toolbar);
            TextView tvTitle = (TextView) activity.findViewById(R.id.tv_bar_title);
            tvTitle.setText(activity.getTitle());
            ((AppCompatActivity) activity).setSupportActionBar(toolBar);
            ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayShowTitleEnabled(false);
            }
            if (toolBar != null) {
                toolBar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.finish();
                    }
                });
            }
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        MobclickAgent.onResume(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        MobclickAgent.onPause(activity);
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
