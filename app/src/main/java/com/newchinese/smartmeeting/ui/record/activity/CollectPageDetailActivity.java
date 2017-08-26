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
import com.newchinese.smartmeeting.ui.record.adapter.CollectPageDetailVpAdapter;
import com.newchinese.smartmeeting.util.DataCacheUtil;
import com.newchinese.smartmeeting.util.DateUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Description:   收藏页详情Activity
 * author         xulei
 * Date           2017/8/26 10:43
 */
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
    private CollectPageDetailVpAdapter adapter;

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
            adapter = new CollectPageDetailVpAdapter(getSupportFragmentManager(), collectPageList);
            vpThumnbail.setAdapter(adapter);
            vpThumnbail.setCurrentItem(selectPosition);
        }
    }

    @Override
    protected void initListener() {
        vpThumnbail.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setTitle(collectPageList.get(position).getPageIndex(), collectPageList.get(position).getDate());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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
