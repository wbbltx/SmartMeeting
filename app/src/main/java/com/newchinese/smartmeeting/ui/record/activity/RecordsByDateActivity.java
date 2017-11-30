package com.newchinese.smartmeeting.ui.record.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.base.BaseSimpleActivity;
import com.newchinese.smartmeeting.constant.Constant;
import com.newchinese.smartmeeting.database.CollectRecordDao;
import com.newchinese.smartmeeting.entity.bean.CollectRecord;
import com.newchinese.smartmeeting.entity.listener.OnItemClickedListener;
import com.newchinese.smartmeeting.ui.record.adapter.CollectRecordsRecyAdapter;
import com.newchinese.smartmeeting.util.BluCommonUtils;
import com.newchinese.smartmeeting.util.DataCacheUtil;
import com.newchinese.smartmeeting.util.DateUtils;
import com.newchinese.smartmeeting.util.GreenDaoUtil;
import com.newchinese.smartmeeting.util.log.XLog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class RecordsByDateActivity extends BaseSimpleActivity implements OnItemClickedListener {
    private static final java.lang.String TAG = "RecordsByDateActivity";
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_pen)
    ImageView ivRight;
    @BindView(R.id.rv_records)
    RecyclerView rvRecords;
    @BindView(R.id.iv_empty)
    ImageView ivEmpty;

    private CollectRecordsRecyAdapter adapter;
    private CollectRecordDao collectRecordDao;
    private List<CollectRecord> collectRecordList = new ArrayList();
    private List<Long> dateList;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_records_by_date;
    }

    @Override
    protected void onViewCreated(Bundle savedInstanceState) {
        collectRecordDao = GreenDaoUtil.getInstance().getCollectRecordDao();
        ivRight.setVisibility(View.GONE);
        tvTitle.setText("会议簿");
    }

    @Override
    protected void initStateAndData() {
        Intent intent = getIntent();
        long longExtra = intent.getLongExtra(BluCommonUtils.DATA, 0);
        dateList = (List<Long>) intent.getSerializableExtra("alllist");
//        XLog.d(TAG, DateUtils.formatLongDate1(longExtra)+" intent是 "+ dateList.size());
        loadRecordsByDate((longExtra));
        //        初始化RecyclerView
        rvRecords.setHasFixedSize(true);
        rvRecords.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvRecords.setItemAnimator(new DefaultItemAnimator());

        adapter = new CollectRecordsRecyAdapter(this, "filter");
        rvRecords.setAdapter(adapter);


    }

    @OnClick({R.id.iv_back, R.id.iv_right})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_right:

                break;
        }
    }

    @Override
    protected void initListener() {
        adapter.setOnItemClickedListener(this);
    }

    @Override
    public void onClick(View view, int position) {
        DataCacheUtil.getInstance().setActiveCollectRecord(collectRecordList.get(position));
        startActivity(new Intent(this, CollectPageListActivity.class));
    }

    @Override
    public void onLongClick(View view, int position) {

    }

    @Override
    public void isEmpty(boolean isEmpty) {
        if (isEmpty){
            ivEmpty.setVisibility(View.VISIBLE);
        }else {
            ivEmpty.setVisibility(View.GONE);
        }
    }

    private void loadRecordsByDate(final Long date) {
        Observable.create(new ObservableOnSubscribe<List<CollectRecord>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<CollectRecord>> e) throws Exception {
                List<CollectRecord> collectRecords = new ArrayList<>();
                for (Long aLong : dateList) {
                    CollectRecord unique = collectRecordDao.queryBuilder().where(CollectRecordDao.Properties.CollectDate.eq(aLong)).unique();
                    collectRecords.add(unique);
                    collectRecordList.add(unique);
                }
//                List<CollectRecord> collectRecords = collectRecordDao.queryBuilder()
//                        .where(CollectRecordDao.Properties.CollectDate.eq(date)).list();
//                collectRecordList.addAll(collectRecords);
                e.onNext(collectRecords);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<CollectRecord>>() {
                    @Override
                    public void accept(List<CollectRecord> collectRecords) throws Exception {
                        adapter.setCollectRecordList(collectRecords);
                    }
                });
    }
}
