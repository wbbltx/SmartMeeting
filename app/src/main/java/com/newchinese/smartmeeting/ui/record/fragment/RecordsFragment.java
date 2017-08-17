package com.newchinese.smartmeeting.ui.record.fragment;

import android.os.Bundle;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.base.BaseSimpleFragment;

/**
 * Description:   记录页Fragment
 * author         xulei
 * Date           2017/8/17 17:30
 */
public class RecordsFragment extends BaseSimpleFragment {
    private static final String TITLE = "title";

    private String title;

    public RecordsFragment() {
    }

    public static RecordsFragment newInstance(String title) {
        RecordsFragment fragment = new RecordsFragment();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(TITLE);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_records;
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
