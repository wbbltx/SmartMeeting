package com.newchinese.smartmeeting.ui.record.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.base.BaseSimpleActivity;
import com.newchinese.smartmeeting.model.bean.CollectPage;
import com.newchinese.smartmeeting.util.DataCacheUtil;
import com.newchinese.smartmeeting.util.DateUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CollectPageDetailActivity extends BaseSimpleActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_share)
    ImageView ivShare;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.vp_thumnbail)
    ViewPager vpThumnbail;
    private int selectPosition = 0;
    private CollectPage currentPage;
    private List<CollectPage> collectPageList = new ArrayList<>(); //活动收藏记录表中当前所有收藏页

    @Override
    protected int getLayoutId() {
        return R.layout.activity_collect_page_detail;
    }

    @Override
    protected void onViewCreated(Bundle savedInstanceState) {

    }

    @Override
    protected void initStateAndData() {
        Intent intent = getIntent();
        selectPosition = intent.getIntExtra("selectPosition", selectPosition);
        collectPageList = DataCacheUtil.getInstance().getActiveCollectPageList();
        if (collectPageList.size() > 0 && selectPosition < (collectPageList.size())) {
            currentPage = collectPageList.get(selectPosition);
            setTitle(currentPage.getPageIndex(), currentPage.getDate());
        }
    }

    @Override
    protected void initListener() {
        
    }

    /**
     * 设置标题内容
     */
    public void setTitle(int pageIndex, long date) {
        tvTitle.setText("第" + pageIndex + "页 | " + DateUtils.formatLongDate3(date));
    }

    @OnClick({R.id.iv_back, R.id.iv_share})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back: //返回
                finish();
                break;
            case R.id.iv_share: //分享

                break;
        }
    }
}
