package com.newchinese.smartmeeting.ui.record.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.base.BaseSimpleActivity;
import com.newchinese.smartmeeting.database.CollectRecordDao;
import com.newchinese.smartmeeting.entity.bean.CollectRecord;
import com.newchinese.smartmeeting.entity.listener.OnItemClickedListener;
import com.newchinese.smartmeeting.ui.record.adapter.RecordTypeRecAdapter;
import com.newchinese.smartmeeting.util.DataCacheUtil;
import com.newchinese.smartmeeting.util.GreenDaoUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class SearchResultActivity extends BaseSimpleActivity implements OnItemClickedListener, TextWatcher {
    @BindView(R.id.et_search_content)
    EditText etSearchContent;

    @BindView(R.id.rv_search)
    RecyclerView rvSearch;

    @BindView(R.id.rl_empty)
    RelativeLayout rlEmpty;

    private RecordTypeRecAdapter adapter;
    private CollectRecordDao collectRecordDao;
    private List<CollectRecord> searchCollectRecordList;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search_result;
    }

    @Override
    protected void onViewCreated(Bundle savedInstanceState) {
//        初始化RecyclerView
        rvSearch.setHasFixedSize(true);
        rvSearch.setLayoutManager(new GridLayoutManager(this, 2));
        rvSearch.setItemAnimator(new DefaultItemAnimator());

        collectRecordDao = GreenDaoUtil.getInstance().getCollectRecordDao();
        searchCollectRecordList = new ArrayList<>();
    }

    @Override
    protected void initStateAndData() {
        adapter = new RecordTypeRecAdapter(this);
        rvSearch.setAdapter(adapter);
    }

    @Override
    protected void initListener() {
        adapter.setOnItemClickedListener(this);
        etSearchContent.addTextChangedListener(this);
    }

    @OnClick({R.id.iv_search})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_search:
                if (!etSearchContent.getText().toString().isEmpty()) {
                    searchCollectRecordByName(etSearchContent.getText().toString());
                }
                break;
        }
    }

    private void searchCollectRecordByName(final String name){
        Observable.create(new ObservableOnSubscribe<List<CollectRecord>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<CollectRecord>> e) throws Exception {
                searchCollectRecordList.clear();
                List<CollectRecord> collectRecords = collectRecordDao.queryBuilder()
                        .orderDesc(CollectRecordDao.Properties.CollectDate).list();
                for (CollectRecord collectRecord : collectRecords) {
                    if (collectRecord.getCollectRecordName().contains(name)) {
                        searchCollectRecordList.add(collectRecord);
                    }
                }
                e.onNext(searchCollectRecordList);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<CollectRecord>>() {
                    @Override
                    public void accept(List<CollectRecord> collectRecords) throws Exception {
                        adapter.setCollectRecordList(collectRecords);
                    }
                });
    }

    @Override
    public void onClick(View view, int position) {
        DataCacheUtil.getInstance().setActiveCollectRecord(searchCollectRecordList.get(position));
        startActivity(new Intent(this, CollectPageListActivity.class));
    }

    @Override
    public void onLongClick(View view, int position) {

    }

    @Override
    public void isEmpty(boolean isEmpty) {
        if (isEmpty) {
            rlEmpty.setVisibility(View.VISIBLE);
        }else {
            rlEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        searchCollectRecordByName(s.toString());
    }
}
