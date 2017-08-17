package com.newchinese.smartmeeting.ui.mine.fragment;

import android.os.Bundle;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.base.BaseSimpleFragment;

/**
 * Description:   我的页Fragment
 * author         xulei
 * Date           2017/8/17 17:30
 */
public class MineFragment extends BaseSimpleFragment {
    private static final String TITLE = "title";

    private String title;

    public MineFragment() {
    }

    public static MineFragment newInstance(String title) {
        MineFragment fragment = new MineFragment();
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
