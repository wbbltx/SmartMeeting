package com.newchinese.smartmeeting.ui.record.fragment;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.base.BaseFragment;
import com.newchinese.smartmeeting.constant.Constant;
import com.newchinese.smartmeeting.contract.RecordsFragContract;
import com.newchinese.smartmeeting.entity.bean.CollectRecord;
import com.newchinese.smartmeeting.presenter.record.RecordsFragPresenter;
import com.newchinese.smartmeeting.ui.record.activity.SearchResultActivity;
import com.newchinese.smartmeeting.ui.record.adapter.RecordTypeFragmentAdapter;
import com.newchinese.smartmeeting.util.DataCacheUtil;
import com.newchinese.smartmeeting.util.log.XLog;
import com.newchinese.smartmeeting.widget.NoPreloadViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import magicindicator.MagicIndicator;
import magicindicator.ViewPagerHelper;
import magicindicator.buildins.commonnavigator.CommonNavigator;
import magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;


/**
 * Description:   记录页Fragment
 * author         xulei
 * Date           2017/8/17 17:30
 */
public class RecordsFragment extends BaseFragment<RecordsFragPresenter> implements RecordsFragContract.View {
    @BindView(R.id.tv_search_content)
    TextView tvSearchContent;
    @BindView(R.id.iv_search)
    ImageView ivSearch;
    @BindView(R.id.tab_record_fragment)
    MagicIndicator mTabLayout;
    @BindView(R.id.vp_record_fragment)
    NoPreloadViewPager mViewPager;
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
        recordTypeFragmentAdapter = new RecordTypeFragmentAdapter(getActivity(), getChildFragmentManager());
        mViewPager.setAdapter(recordTypeFragmentAdapter);
    }

    @Override
    protected void initStateAndData() {
        CommonNavigator commonNavigator = new CommonNavigator(getActivity());
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return Constant.titleList.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                final ColorTransitionPagerTitleView colorTransitionPagerTitleView = new ColorTransitionPagerTitleView(context);
                colorTransitionPagerTitleView.setNormalColor(getActivity().getResources().getColor(R.color.gray3));
                colorTransitionPagerTitleView.setSelectedColor(getActivity().getResources().getColor(R.color.simple_blue));
                colorTransitionPagerTitleView.setText(Constant.titleList[index]);
                colorTransitionPagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DataCacheUtil.getInstance().setName(Constant.titleList[index]);
                        mViewPager.setCurrentItem(index);
                    }
                });
                return colorTransitionPagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setColors(getActivity().getResources().getColor(R.color.simple_blue));
                return indicator;
            }
        });
        mTabLayout.setNavigator(commonNavigator);
        ViewPagerHelper.bind(mTabLayout, mViewPager);
        DataCacheUtil.getInstance().setName("全部");
    }

    @Override
    protected void initListener() {
//        adapter.setOnItemClickedListener(this);
//        mViewPager.addOnPageChangeListener(this);
//        mViewPager.setOnPageChangeListener(this);
        //搜索输入框内容改变监听
//        etSearchContent.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (s.toString().length() == 0) {
//                    mPresenter.loadAllCollectRecordData();
//                } else {
//                    mPresenter.searchCollectRecordByName(s.toString());
//                }
//            }
//        });
    }


    @OnClick({R.id.iv_search,R.id.tv_search_content})
    public void onViewClicked() {
//        if (!etSearchContent.getText().toString().isEmpty()) {
//            mPresenter.searchCollectRecordByName(etSearchContent.getText().toString());
//        }
        Intent intent = new Intent(getActivity(), SearchResultActivity.class);
        startActivity(intent);
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
//    public void refreshData() {
//        if (etSearchContent.getText().toString().length() == 0) {
//            mPresenter.loadAllCollectRecordData();
//        } else {
//            mPresenter.searchCollectRecordByName(etSearchContent.getText().toString());
//        }
//    }

    @Override
    public void onResume() {
        super.onResume();
        XLog.d(TAG,TAG+"onResume");
    }

//    @Override
//    public void onPageSelected(int position) {
////        String pageTitle = (String) recordTypeFragmentAdapter.getPageTitle(position);
//        XLog.d(TAG,TAG+"onPageSelected");
//        String pageTitle = titleList.get(position);
//        DataCacheUtil.getInstance().setName(pageTitle);
//    }


}
