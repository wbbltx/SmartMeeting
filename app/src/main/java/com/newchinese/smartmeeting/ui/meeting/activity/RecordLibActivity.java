package com.newchinese.smartmeeting.ui.meeting.activity;

import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.base.BaseActivity;
import com.newchinese.smartmeeting.contract.RecordLibContract;
import com.newchinese.smartmeeting.entity.bean.CollectPage;
import com.newchinese.smartmeeting.entity.listener.OnItemClickedListener;
import com.newchinese.smartmeeting.presenter.meeting.RecordLibPresenter;
import com.newchinese.smartmeeting.ui.meeting.adapter.RecordLibAdapter;
import com.newchinese.smartmeeting.util.DataCacheUtil;
import com.newchinese.smartmeeting.util.log.XLog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.newchinese.smartmeeting.ui.meeting.activity.DrawingBoardActivity.TAG_PAGE_INDEX;

public class RecordLibActivity extends BaseActivity<RecordLibPresenter, View> implements RecordLibContract.View<View>, OnItemClickedListener {

    private static final java.lang.String TAG = "RecordLibActivity";
    @BindView(R.id.record_lib_list)
    RecyclerView recordView;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.iv_pen)
    ImageView ivPen;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_create)
    TextView tvDelete;
    @BindView(R.id.record_lib_oper)
    LinearLayout recordLibOper;
    private DataCacheUtil dataCacheUtil;
    private int selectPageIndex;
    private List<String> recordPathList;
    private List<Boolean> isSelectedList = new ArrayList<>();
    private RecordLibAdapter adapter;
    private String fromFlag;
    private CollectPage currentPage;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_record_lib;
    }

    @Override
    protected void initStateAndData() {
        dataCacheUtil = DataCacheUtil.getInstance();
        Intent intent = getIntent();
        selectPageIndex = intent.getIntExtra("selectPageIndex", 0);
        fromFlag = intent.getStringExtra("fromFlag");
        currentPage = (CollectPage) intent.getSerializableExtra("currentPage");
        setTitle(selectPageIndex); //设置当前页数
        if (fromFlag.equals("1")) {  //画板跳转
            recordPathList = dataCacheUtil.getRecordPathList();
        } else {                     //记录跳转
            recordPathList = currentPage.getScreenPathList();
        }
        //recycler初始化
        recordView.setHasFixedSize(true);
        recordView.setLayoutManager(new GridLayoutManager(this, 2));
        recordView.setItemAnimator(new DefaultItemAnimator());

        adapter = new RecordLibAdapter(this, recordPathList);
        initIsSelectedStatus(recordPathList);
        recordView.setAdapter(adapter);

        adapter.setNotePageList(recordPathList);
    }

    @Override
    protected void initListener() {
        adapter.setOnItemClickedListener(this);
    }

    @Override
    protected RecordLibPresenter initPresenter() {
        return new RecordLibPresenter();
    }

    @Override
    public void setTitle(int pageIndex) {
        tvTitle.setText(getString(R.string.record_page_index, pageIndex));
        ivPen.setVisibility(View.GONE);
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setText(getString(R.string.edit));
    }

    @Override
    public void refreshRecord(final List<String> recordPath) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.setNotePageList(recordPath);
                initIsSelectedStatus(recordPath);
            }
        });
    }

    @OnClick({R.id.iv_back, R.id.tv_right, R.id.tv_cancel, R.id.tv_create})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back: //返回键
                finish();
                break;
            case R.id.tv_right: //编辑按钮
                String s = tvRight.getText().toString();
                int state = getState(s);
                if (state == 0) {//当前是编辑
                    recordLibOper.setVisibility(View.VISIBLE);
                    tvRight.setText(getString(R.string.select_all));
                    tvDelete.setText(getString(R.string.delete));
                } else if (state == 1) {//当前是全选
                    for (int i = 0; i < isSelectedList.size(); i++) {
                        isSelectedList.set(i, true);
                    }
                    tvRight.setText(getString(R.string.not_select_all));
                } else if (state == -1) {//当前是全不选
                    for (int i = 0; i < isSelectedList.size(); i++) {
                        isSelectedList.set(i, false);
                    }
                    tvRight.setText(getString(R.string.select_all));
                }
                adapter.setIsSelectedList(isSelectedList);
                break;
            case R.id.tv_create:
                if (fromFlag.equals("1")) {
                    mPresenter.deleteRecord(recordPathList, isSelectedList, selectPageIndex);
                }else {
                    //删除数据库中的数据
                    mPresenter.deleteCollectRecord(recordPathList, isSelectedList, selectPageIndex);
                    //删除缓存中的数据
                    //...待续
                }
                tvRight.setText(getString(R.string.edit));
                recordLibOper.setVisibility(View.GONE);
                break;
            case R.id.tv_cancel:
                tvRight.setText(getString(R.string.edit));
                recordLibOper.setVisibility(View.GONE);
                for (int i = 0; i < isSelectedList.size(); i++) {
                    isSelectedList.set(i, false);
                }
                adapter.setIsSelectedList(isSelectedList);
                break;
        }
    }

    private int getState(String str) {
        int i = 0;
        if (str.equals(getString(R.string.edit))) {
            i = 0;
        } else if (str.equals(getString(R.string.select_all))) {
            i = 1;
        } else if (str.equals(getString(R.string.not_select_all))) {
            i = -1;
        }
        return i;
    }

    private void initIsSelectedStatus(List<String> pageList) {
        isSelectedList = new ArrayList<>();
        for (int i = 0; i < pageList.size(); i++) {
            isSelectedList.add(false);
        }
        adapter.setIsSelectedList(isSelectedList);
    }

    @Override
    public void onClick(View view, int position) {
        int state = getState(tvRight.getText().toString());
        if (state != 0) {
            isSelectedList.set(position, !adapter.getIsSelectedList().get(position));
            adapter.setIsSelectedList(isSelectedList);
        } else {
            Intent intent = new Intent(this, RecordPlayActivity.class);
            intent.putExtra("recordPath", adapter.getItem(position));
            startActivity(intent);
        }
    }

    @Override
    public void onLongClick(View view, int position) {

    }
}
