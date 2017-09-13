package com.newchinese.smartmeeting.ui.record.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.base.BaseActivity;
import com.newchinese.smartmeeting.contract.CollectRecordFilterActContract;
import com.newchinese.smartmeeting.entity.bean.CollectRecord;
import com.newchinese.smartmeeting.entity.listener.OnItemClickedListener;
import com.newchinese.smartmeeting.presenter.record.CollectRecordFilterActPresenter;
import com.newchinese.smartmeeting.ui.record.adapter.CollectRecordsRecyAdapter;
import com.newchinese.smartmeeting.util.DataCacheUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Description:   收藏记录表根据分类筛选列表页
 * author         xulei
 * Date           2017/8/26 14:43
 */
public class CollectRecordFilterActivity extends BaseActivity<CollectRecordFilterActPresenter, View> implements
        CollectRecordFilterActContract.View<View>, OnItemClickedListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_pen)
    ImageView ivPen;
    @BindView(R.id.rv_collect_record_filter)
    RecyclerView rvCollectRecordFilter;
    private CollectRecordsRecyAdapter adapter;
    private String classifyName;
    private List<CollectRecord> collectRecordList;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_collect_record_filter;
    }

    @Override
    protected CollectRecordFilterActPresenter initPresenter() {
        return new CollectRecordFilterActPresenter();
    }

    @Override
    protected void onViewCreated(Bundle savedInstanceState) {
        super.onViewCreated(savedInstanceState);
        //初始化RecyclerView
        rvCollectRecordFilter.setHasFixedSize(true);
        rvCollectRecordFilter.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvCollectRecordFilter.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    protected void initStateAndData() {
        ivPen.setVisibility(View.GONE);
        collectRecordList = new ArrayList<>();
        adapter = new CollectRecordsRecyAdapter(this, "filter");
        rvCollectRecordFilter.setAdapter(adapter);
        Intent intent = getIntent();
        classifyName = intent.getStringExtra("classifyName");
        if (classifyName != null) {
            tvTitle.setText(classifyName);
            mPresenter.loadCollectPageDataByClassify(classifyName);
        }
    }

    @Override
    protected void initListener() {
        adapter.setOnItemClickedListener(this);
    }

    @Override
    public void getFilterCollectRecordData(List<CollectRecord> collectRecords) {
        collectRecordList.clear();
        collectRecordList.addAll(collectRecords);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.setCollectRecordList(collectRecordList);
            }
        });
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }

    @Override
    public void onClick(View view, int position) {
        DataCacheUtil.getInstance().setActiveCollectRecord(collectRecordList.get(position));
        startActivity(new Intent(this, CollectPageListActivity.class));
    }

    @Override
    public void onLongClick(View view, int position) {

    }
}
