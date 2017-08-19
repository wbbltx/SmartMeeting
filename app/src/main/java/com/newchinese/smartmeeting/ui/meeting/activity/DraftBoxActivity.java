package com.newchinese.smartmeeting.ui.meeting.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.base.BaseActivity;
import com.newchinese.smartmeeting.base.BasePresenter;
import com.newchinese.smartmeeting.presenter.DraftBoxPresenter;
/**
 * Description:   
 * author         xulei
 * Date           2017/8/18 16:34
 */
public class DraftBoxActivity extends BaseActivity {
    private String classifyName; //分类名

    @Override
    protected int getLayoutId() {
        return R.layout.activity_draft_box;
    }

    @Override
    protected void onViewCreated(Bundle savedInstanceState) {
        super.onViewCreated(savedInstanceState);

    }

    @Override
    protected void initStateAndData() {
        Intent intent = getIntent();
        classifyName = intent.getStringExtra("classify_name");
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected BasePresenter initPresenter() {
        return new DraftBoxPresenter();
    }
}
