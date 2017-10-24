package com.newchinese.smartmeeting.ui.record.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.base.BaseActivity;
import com.newchinese.smartmeeting.contract.CollectPageListActContract;
import com.newchinese.smartmeeting.entity.bean.CollectPage;
import com.newchinese.smartmeeting.entity.bean.CollectRecord;
import com.newchinese.smartmeeting.entity.listener.OnItemClickedListener;
import com.newchinese.smartmeeting.presenter.record.CollectPageListActPresenter;
import com.newchinese.smartmeeting.ui.record.adapter.CollectPagesRecyAdapter;
import com.newchinese.smartmeeting.util.DataCacheUtil;
import com.newchinese.smartmeeting.util.log.XLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Description:   收藏页列表Activity
 * author         xulei
 * Date           2017/8/25 14:47
 */
public class CollectPageListActivity extends BaseActivity<CollectPageListActPresenter, View>
        implements CollectPageListActContract.View<View>, OnItemClickedListener, View.OnClickListener {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_pen)
    ImageView ivPen;
    @BindView(R.id.rv_collect_page_list)
    RecyclerView rvCollectPageList;
    @BindView(R.id.tv_right)
    TextView tvRight;
    private CollectPagesRecyAdapter adapter;
    private CollectRecord activeRecord;
    private List<CollectPage> collectPageList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_collect_page_list;
    }

    @Override
    protected CollectPageListActPresenter initPresenter() {
        return new CollectPageListActPresenter();
    }

    @Override
    protected void onViewCreated(Bundle savedInstanceState) {
        super.onViewCreated(savedInstanceState);
        //初始化RecyclerView
        rvCollectPageList.setHasFixedSize(true);
        rvCollectPageList.setLayoutManager(new GridLayoutManager(this, 2));
        rvCollectPageList.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    protected void initStateAndData() {
        activeRecord = DataCacheUtil.getInstance().getActiveCollectRecord();
        //设置bar数据
        ivPen.setVisibility(View.GONE);
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setText(R.string.add);
        tvTitle.setText(activeRecord.getCollectRecordName());
        //初始化适配器
        adapter = new CollectPagesRecyAdapter(this);
        rvCollectPageList.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //加载数据库数据
        mPresenter.loadAllCollectPageData();
    }

    @Override
    protected void initListener() {
        adapter.setOnItemClickedListener(this);
        tvRight.setOnClickListener(this);
    }

    @Override
    public void getAllCollectPageData(List<CollectPage> collectPages) {
        if (!isFinishing()) {
            collectPageList.clear();
            collectPageList.addAll(collectPages);
            DataCacheUtil.getInstance().setActiveCollectPageList(collectPageList);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.setCollectPageList(collectPageList);
                }
            });
        }
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }

    /**
     * 列表点击事件
     */
    @Override
    public void onClick(View view, int position) {
        Intent intent = new Intent(this, CollectPageDetailActivity.class);
        intent.putExtra("selectPosition", position);
        startActivity(intent);
    }

    /**
     * 列表长点击事件
     */
    @Override
    public void onLongClick(View view, int position) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_right:
                startActivity(new Intent(CollectPageListActivity.this,CollectAddTypeActivity.class));
                break;
        }
    }
}
