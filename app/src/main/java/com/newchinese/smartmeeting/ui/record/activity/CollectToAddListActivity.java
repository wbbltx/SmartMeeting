package com.newchinese.smartmeeting.ui.record.activity;

import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.base.BaseActivity;
import com.newchinese.smartmeeting.contract.CollectToAddListContract;
import com.newchinese.smartmeeting.entity.bean.NotePage;
import com.newchinese.smartmeeting.entity.bean.NoteRecord;
import com.newchinese.smartmeeting.entity.listener.OnItemClickedListener;
import com.newchinese.smartmeeting.manager.NoteRecordManager;
import com.newchinese.smartmeeting.presenter.record.CollectToAddListPresenter;
import com.newchinese.smartmeeting.ui.meeting.adapter.DraftPageRecyAdapter;
import com.newchinese.smartmeeting.ui.record.adapter.CollectPagesRecyAdapter;
import com.newchinese.smartmeeting.util.BluCommonUtils;
import com.newchinese.smartmeeting.util.CustomizedToast;
import com.newchinese.smartmeeting.util.GreenDaoUtil;
import com.newchinese.smartmeeting.util.log.XLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 点击要补充的某个类别后，在该页展示并选择要补充到收藏的页面
 */
public class CollectToAddListActivity extends BaseActivity<CollectToAddListPresenter, View> implements CollectToAddListContract.View<View>, OnItemClickedListener, View.OnClickListener {
    @BindView(R.id.iv_pen)
    ImageView ivPen;
    @BindView(R.id.rv_collect_page_list)
    RecyclerView rvCollectPageList;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    private DraftPageRecyAdapter adapter;
    private NoteRecord activeNoteRecord;

    private List<NotePage> notePageList = new ArrayList<>();
    private List<Boolean> isSelectedList = new ArrayList<>();

    private TextView tvCancel, tvCreate;
    private PopupWindow pwCreateRecord;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_collect_page_list;
    }

    @Override
    protected void onViewCreated(Bundle savedInstanceState) {
        super.onViewCreated(savedInstanceState);
        //初始化RecyclerView
        rvCollectPageList.setHasFixedSize(true);
        rvCollectPageList.setLayoutManager(new GridLayoutManager(this, 2));
        rvCollectPageList.setItemAnimator(new DefaultItemAnimator());

        //初始化PopupWindow
        View viewCreateRecord = LayoutInflater.from(this).inflate(R.layout.layout_create_record, null);
        tvCancel = (TextView) viewCreateRecord.findViewById(R.id.tv_cancel);
        tvCreate = (TextView) viewCreateRecord.findViewById(R.id.tv_create);
        pwCreateRecord = new PopupWindow(viewCreateRecord, RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        pwCreateRecord.setAnimationStyle(R.style.popup_anim);// 淡入淡出动画
        pwCreateRecord.setBackgroundDrawable(new BitmapDrawable());
        pwCreateRecord.setOutsideTouchable(false);
    }

    @Override
    protected void initStateAndData() {
        ivPen.setVisibility(View.GONE);
        tvCreate.setText(R.string.btn_confirm1);
        String classifyName = getIntent().getStringExtra(BluCommonUtils.CLASSIFY_NAME);
        tvTitle.setText(classifyName);
        activeNoteRecord = NoteRecordManager.getInstance().getNoteRecord(
                GreenDaoUtil.getInstance().getNoteRecordDao(), classifyName);

        adapter = new DraftPageRecyAdapter(this);
        rvCollectPageList.setAdapter(adapter);
    }

    @Override
    protected void initListener() {
        adapter.setOnItemClickedListener(this);
        tvCancel.setOnClickListener(this);
        tvCreate.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.loadLeftPage(activeNoteRecord);
    }

    @Override
    protected CollectToAddListPresenter initPresenter() {
        return new CollectToAddListPresenter();
    }

    @Override
    public void showActivePage(List<NotePage> pageList) {
        if (pageList.size() == 0){
            CustomizedToast.showShort(this,"当前页面无内容，无法添加！");
            finish();
            return;
        }
        notePageList.clear();
        notePageList.addAll(pageList);
        adapter.setNotePageList(notePageList);

        initIsSelectedStatus(pageList);

    }

    @Override
    public void showToast(String string) {
        CustomizedToast.showShort(this,string);
    }

    @OnClick({R.id.iv_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;

            case R.id.tv_cancel:
                resetState();
                break;

            case R.id.tv_create:
                mPresenter.transferPage(notePageList, isSelectedList);
                resetState();
                break;
        }
    }

    @Override
    public void onClick(View view, int position) {
        isSelectedList.set(position, !adapter.getIsSelectedList().get(position));
        adapter.setIsSelectedList(isSelectedList);
        boolean temp = false;
        for (Boolean aBoolean : isSelectedList) {
            temp = temp | aBoolean;
            if (temp) {
                pwCreateRecord.showAtLocation(findViewById(R.id.rv_collect_page_list), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            }
        }
        if (!temp){
            pwCreateRecord.dismiss();
        }
    }

    @Override
    public void onLongClick(View view, int position) {

    }

    private void resetState() {
        pwCreateRecord.dismiss();
        for (int i = 0; i < isSelectedList.size(); i++) {
            isSelectedList.set(i, false);
        }
        adapter.setIsSelectedList(isSelectedList);
    }

    /**
     * 初始化是否被选择的boolean集合都为false未选择状态
     */
    private void initIsSelectedStatus(List<NotePage> pageList) {
        isSelectedList = new ArrayList<>();
        for (int i = 0; i < pageList.size(); i++) {
            isSelectedList.add(false);
        }
        adapter.setIsSelectedList(isSelectedList);
    }
}
