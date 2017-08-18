package com.newchinese.smartmeeting.ui.meeting.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.base.BaseSimpleFragment;

/**
 * Description:   会议页Fragment
 * author         xulei
 * Date           2017/8/17 17:30
 */
public class MeetingFragment extends BaseSimpleFragment {
    private static final String TITLE = "title";

    private String title;

    public MeetingFragment() {
    }

    public static MeetingFragment newInstance(String title) {
        MeetingFragment fragment = new MeetingFragment();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(title);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_meeting;
    }

    @Override
    protected void onFragViewCreated() {

    }

    @Override
    protected void initStateAndData() {

    }

    @Override
    protected void initListener() {

    }
}
