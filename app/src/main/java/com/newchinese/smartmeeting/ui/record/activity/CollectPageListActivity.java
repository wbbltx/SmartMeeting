package com.newchinese.smartmeeting.ui.record.activity;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.base.BaseActivity;
import com.newchinese.smartmeeting.contract.CollectPageListActContract;
import com.newchinese.smartmeeting.model.bean.CollectPage;
import com.newchinese.smartmeeting.model.bean.CollectRecord;
import com.newchinese.smartmeeting.model.listener.OnItemClickedListener;
import com.newchinese.smartmeeting.presenter.record.CollectPageListActPresenter;
import com.newchinese.smartmeeting.ui.record.adapter.CollectPagesRecyAdapter;
import com.newchinese.smartmeeting.util.DataCacheUtil;

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
        implements CollectPageListActContract.View<View>, OnItemClickedListener {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_pen)
    TextView ivPen;
    @BindView(R.id.rv_collect_page_list)
    RecyclerView rvCollectPageList;
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
    protected void initStateAndData() {
        activeRecord = DataCacheUtil.getInstance().getActiveCollectRecord();
        //设置bar数据
        ivPen.setVisibility(View.GONE);
        tvTitle.setText(activeRecord.getCollectRecordName());
        //初始化RecyclerView
        rvCollectPageList.setHasFixedSize(true);
        rvCollectPageList.setLayoutManager(new GridLayoutManager(this, 2));
        rvCollectPageList.setItemAnimator(new DefaultItemAnimator());
        adapter = new CollectPagesRecyAdapter(this);
        rvCollectPageList.setAdapter(adapter);
        //加载数据库数据
        mPresenter.loadAllCollectPageData();
    }

    @Override
    protected void initListener() {
        adapter.setOnItemClickedListener(this);
    }

    @Override
    public void getAllCollectPageData(List<CollectPage> collectPages) {
        collectPageList.clear();
        collectPageList.addAll(collectPages);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.setCollectPageList(collectPageList);
            }
        });
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

    }

    /**
     * 列表长点击事件
     */
    @Override
    public void onLongClick(View view, int position) {

    }
}
