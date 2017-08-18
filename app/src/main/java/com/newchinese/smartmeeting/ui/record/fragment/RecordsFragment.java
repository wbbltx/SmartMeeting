package com.newchinese.smartmeeting.ui.record.fragment;

import android.os.Bundle;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.base.BaseFragment;
import com.newchinese.smartmeeting.contract.RecordsContract;
import com.newchinese.smartmeeting.presenter.RecordsFragPresenter;

/**
 * Description:   记录页Fragment
 * author         xulei
 * Date           2017/8/17 17:30
 */
public class RecordsFragment extends BaseFragment<RecordsFragPresenter> implements RecordsContract.View {
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
    protected int getLayoutId() {
        return R.layout.fragment_records;
    }

    @Override
    protected void onFragViewCreated() {

    }

    @Override
    protected void initStateAndData() {
        if (getArguments() != null) {
            title = getArguments().getString(TITLE);
        }
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected RecordsFragPresenter initPresenter() {
        return new RecordsFragPresenter();
    }
}
