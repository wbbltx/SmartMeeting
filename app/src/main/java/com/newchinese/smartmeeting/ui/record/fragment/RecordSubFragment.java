package com.newchinese.smartmeeting.ui.record.fragment;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Size;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.base.BaseFragment;
import com.newchinese.smartmeeting.base.BaseSimpleFragment;
import com.newchinese.smartmeeting.contract.RecordTypeContract;
import com.newchinese.smartmeeting.entity.bean.CollectRecord;
import com.newchinese.smartmeeting.entity.event.EditModeEvent;
import com.newchinese.smartmeeting.entity.listener.OnItemClickedListener;
import com.newchinese.smartmeeting.presenter.record.RecordsTypePresenter;
import com.newchinese.smartmeeting.ui.record.activity.CollectPageListActivity;
import com.newchinese.smartmeeting.ui.record.activity.EditRecordsActivity;
import com.newchinese.smartmeeting.ui.record.adapter.RecordTypeRecAdapter;
import com.newchinese.smartmeeting.util.DataCacheUtil;
import com.newchinese.smartmeeting.util.log.XLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Administrator on 2017/11/21 0021.
 */

public class RecordSubFragment extends BaseFragment<RecordsTypePresenter> implements RecordTypeContract.View, OnItemClickedListener {
    private static final java.lang.String TAG = "RecordSubFragment";
    @BindView(R.id.rv_record_list)
    RecyclerView rvRecordList;

    private RecordTypeRecAdapter adapter;
    private List<CollectRecord> collectRecordList = new ArrayList<>();
    private List<Boolean> isSelectedList = new ArrayList<>();
    private View viewEditRecord;
    private TextView tvCancel;
    private TextView tvCreate;

//    private static RecordSubFragment instance = null;
//
//    public static  RecordSubFragment getInstance(){
//        if (instance == null){
//            instance = new RecordSubFragment();
//        }
//        return instance;
//    }

    @Override
    protected RecordsTypePresenter initPresenter() {
        return new RecordsTypePresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_subfragment;
    }

    @Override
    protected void onFragViewCreated() {
        super.onFragViewCreated();
//        初始化RecyclerView
        rvRecordList.setHasFixedSize(true);
        rvRecordList.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        rvRecordList.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    protected void initStateAndData() {
        adapter = new RecordTypeRecAdapter(getActivity());
        rvRecordList.setAdapter(adapter);


    }

    @Override
    protected void initListener() {
        adapter.setOnItemClickedListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.loadRecordsPages(DataCacheUtil.getInstance().getChosenClassifyName());
    }

    //子条目的点击事件
    @Override
    public void onClick(View view, int position) {
        DataCacheUtil.getInstance().setActiveCollectRecord(collectRecordList.get(position));
        startActivity(new Intent(mActivity, CollectPageListActivity.class));
    }

    @Override
    public void onLongClick(View view, int position) {
        Intent intent = new Intent(getActivity(), EditRecordsActivity.class);
        startActivity(intent);
    }

    @Override
    public void getRightCollectRecordData(List<CollectRecord> collectRecords) {
        collectRecordList.clear();
        collectRecordList.addAll(collectRecords);
        XLog.d(TAG, "长度是：" + collectRecordList.size());
        adapter.setCollectRecordList(collectRecordList);
    }


}
