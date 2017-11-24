package com.newchinese.smartmeeting.ui.record.fragment;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.base.BaseFragment;
import com.newchinese.smartmeeting.contract.RecordsFragContract;
import com.newchinese.smartmeeting.entity.bean.CollectRecord;
import com.newchinese.smartmeeting.entity.event.EditModeEvent;
import com.newchinese.smartmeeting.presenter.record.RecordsFragPresenter;
import com.newchinese.smartmeeting.ui.record.adapter.CollectRecordsRecyAdapter;
import com.newchinese.smartmeeting.ui.record.adapter.RecordTypeFragmentAdapter;
import com.newchinese.smartmeeting.util.DataCacheUtil;
import com.newchinese.smartmeeting.util.log.XLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * Description:   记录页Fragment
 * author         xulei
 * Date           2017/8/17 17:30
 */
public class RecordsFragment extends BaseFragment<RecordsFragPresenter> implements RecordsFragContract.View, ViewPager.OnPageChangeListener {
    @BindView(R.id.et_search_content)
    EditText etSearchContent;
    @BindView(R.id.iv_search)
    ImageView ivSearch;
//    @BindView(R.id.rv_record_list)
//    RecyclerView rvRecordList;
    @BindView(R.id.tab_record_fragment)
    TabLayout mTabLayout;
    @BindView(R.id.vp_record_fragment)
    ViewPager mViewPager;
//    private CollectRecordsRecyAdapter adapter;
    private List<CollectRecord> collectRecordList = new ArrayList<>();
    private RecordTypeFragmentAdapter recordTypeFragmentAdapter;
    private static final String TAG = "RecordsFragment";

    public RecordsFragment() {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_records;
    }

    @Override
    protected RecordsFragPresenter initPresenter() {
        return new RecordsFragPresenter();
    }

    @Override
    protected void onFragViewCreated() {
        super.onFragViewCreated();
        //初始化RecyclerView
//        rvRecordList.setHasFixedSize(true);
//        rvRecordList.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
//        rvRecordList.setItemAnimator(new DefaultItemAnimator());
        recordTypeFragmentAdapter = new RecordTypeFragmentAdapter(getActivity(), getChildFragmentManager());
        mViewPager.setAdapter(recordTypeFragmentAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    protected void initStateAndData() {
//        adapter = new CollectRecordsRecyAdapter(mContext, "records");
//        rvRecordList.setAdapter(adapter);
//        mPresenter.loadAllCollectRecordData();
        DataCacheUtil.getInstance().setChosenClassifyName("全部");
    }

    @Override
    protected void initListener() {
//        adapter.setOnItemClickedListener(this);
        mViewPager.addOnPageChangeListener(this);
        //搜索输入框内容改变监听
        etSearchContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 0) {
//                    mPresenter.loadAllCollectRecordData();
                } else {
//                    mPresenter.searchCollectRecordByName(s.toString());
                }
            }
        });
    }


    @OnClick(R.id.iv_search)
    public void onViewClicked() {
        if (!etSearchContent.getText().toString().isEmpty()) {
//            mPresenter.searchCollectRecordByName(etSearchContent.getText().toString());
        }
    }

    /**
     * 获取到所有CollectRecord集合
     */
    @Override
    public void getAllCollectRecordData(List<CollectRecord> collectRecords) {
        if (!mActivity.isFinishing()) {
            collectRecordList.clear();
            collectRecordList.addAll(collectRecords);
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    adapter.setCollectRecordList(collectRecordList);
                }
            });
        }
    }

    /**
     * 刷新数据
     */
    public void refreshData() {
        if (etSearchContent.getText().toString().length() == 0) {
//            mPresenter.loadAllCollectRecordData();
        } else {
//            mPresenter.searchCollectRecordByName(etSearchContent.getText().toString());
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        String pageTitle = (String) recordTypeFragmentAdapter.getPageTitle(position);

        DataCacheUtil.getInstance().setChosenClassifyName(pageTitle);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

//    /**
//     * 列表点击事件
//     */
//    @Override
//    public void onClick(View view, int position) {
//        MobclickAgent.onEvent(getActivity(), "book_res", collectRecordList.get(position).getCollectRecordName());
//        DataCacheUtil.getInstance().setActiveCollectRecord(collectRecordList.get(position));
//        startActivity(new Intent(mActivity, CollectPageListActivity.class));
//    }
//
//    /**
//     * 列表长点击事件
//     */
//    @Override
//    public void onLongClick(View view, int position) {
//
//    }

}
