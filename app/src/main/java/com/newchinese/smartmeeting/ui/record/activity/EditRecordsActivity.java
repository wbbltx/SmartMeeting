package com.newchinese.smartmeeting.ui.record.activity;

import android.content.DialogInterface;
import android.renderscript.Allocation;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.base.BaseActivity;
import com.newchinese.smartmeeting.base.BaseView;
import com.newchinese.smartmeeting.contract.EditRecordContract;
import com.newchinese.smartmeeting.entity.bean.CollectRecord;
import com.newchinese.smartmeeting.entity.listener.OnItemClickedListener;
import com.newchinese.smartmeeting.presenter.record.EditRecordsPresenter;
import com.newchinese.smartmeeting.ui.meeting.activity.DraftBoxActivity;
import com.newchinese.smartmeeting.ui.record.adapter.RecordTypeRecAdapter;
import com.newchinese.smartmeeting.util.BluCommonUtils;
import com.newchinese.smartmeeting.util.CustomizedToast;
import com.newchinese.smartmeeting.util.DataCacheUtil;
import com.newchinese.smartmeeting.util.DateUtils;
import com.newchinese.smartmeeting.util.log.XLog;
import com.newchinese.smartmeeting.widget.CustomInputDialog;
import com.newchinese.smartmeeting.widget.NormalDialog;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.OnClick;

public class EditRecordsActivity extends BaseActivity<EditRecordsPresenter,View> implements EditRecordContract.View<View>,OnItemClickedListener {

    private static final java.lang.String TAG = "EditRecordsActivity";
    @BindView(R.id.rv_edit)
    RecyclerView rvEdit;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.iv_pen)
    ImageView ivPen;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_create)
    TextView tvCreate;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    private RecordTypeRecAdapter adapter;
    private List<CollectRecord> collectRecordList = new ArrayList<>();
    private List<Boolean> isSelectedList = new ArrayList<>();
    private boolean allSelect = false;
    private CustomInputDialog.Builder builder;
    private NormalDialog.Builder normalDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_records;
    }

    @Override
    protected void initStateAndData() {
        ivBack.setImageResource(R.mipmap.editmode_cancel);
        tvRight.setVisibility(View.VISIBLE);
        ivPen.setVisibility(View.GONE);
        tvRight.setText(getResources().getString(R.string.select_all));
        tvCancel.setText(getResources().getString(R.string.delete));
        tvCancel.setEnabled(false);
        tvCreate.setText(getResources().getString(R.string.rename));
        tvTitle.setText("选中0项");
        tvCreate.setEnabled(false);
//        初始化RecyclerView
        rvEdit.setHasFixedSize(true);
        rvEdit.setLayoutManager(new GridLayoutManager(this, 2));
        rvEdit.setItemAnimator(new DefaultItemAnimator());

        adapter = new RecordTypeRecAdapter(this);
        adapter.setIsSelectable(true);
        rvEdit.setAdapter(adapter);

        mPresenter.queryCollectRecords(DataCacheUtil.getInstance().getName());
        XLog.d(TAG,DataCacheUtil.getInstance().getName());
    }

    @Override
    protected void initListener() {
        adapter.setOnItemClickedListener(this);
    }

    @Override
    protected EditRecordsPresenter initPresenter() {
        return new EditRecordsPresenter();
    }

    @OnClick({R.id.iv_back,R.id.tv_right,R.id.tv_cancel,R.id.tv_create})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_right:
                if (!allSelect){
                    for (int i = 0; i < isSelectedList.size(); i++) {
                        isSelectedList.set(i, true);
                    }
                    tvCreate.setEnabled(false);
                    tvCancel.setEnabled(true);
                    tvTitle.setText("选中"+isSelectedList.size()+"项");
                    tvRight.setText(getResources().getString(R.string.not_select_all));
                }else {
                    for (int i = 0; i < isSelectedList.size(); i++) {
                        isSelectedList.set(i, false);
                    }
                    tvTitle.setText("选中0项");
                    tvCreate.setEnabled(false);
                    tvCancel.setEnabled(false);
                    tvRight.setText(getResources().getString(R.string.select_all));
                }
                adapter.setIsSelectedList(isSelectedList);
                allSelect = !allSelect;
                break;
            case R.id.tv_cancel://删除
                confirmDialog();
                break;
            case R.id.tv_create://重命名
                reNameDialog();
                break;
        }
    }

    public void confirmDialog() {
        normalDialog = new NormalDialog.Builder(this);
        normalDialog.setTitle(getString(R.string.delete));
        normalDialog.setContent("确定要删除该文件？");
        normalDialog.setPositiveButton(getString(R.string.btn_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPresenter.deleteCollectRecords(collectRecordList,isSelectedList);
                dialog.dismiss();
            }
        });
        normalDialog.setNegativeButton(getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        normalDialog.create().show();
    }

    @Override
    public void onClick(View view, int position) {
        int i = 0;
        isSelectedList.set(position, !adapter.getIsSelectedList().get(position));
        adapter.setIsSelectedList(isSelectedList);
        for (Boolean aBoolean : isSelectedList) {
            if (aBoolean) {
                i ++;
            }
        }
        tvTitle.setText("选中"+i+"项");
        if (i != 0){
            if (i == 1){
                tvCreate.setEnabled(true);
            }else {
                tvCreate.setEnabled(false);
            }
            tvCancel.setEnabled(true);
        }else {
            tvCreate.setEnabled(false);
            tvCancel.setEnabled(false);
        }
    }

    @Override
    public void onLongClick(View view, int position) {

    }

    @Override
    public void isEmpty(boolean isEmpty) {

    }

    @Override
    public void showQueryResult(List<CollectRecord> collectRecordList) {
        this.collectRecordList.clear();
        this.collectRecordList.addAll(collectRecordList);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initIsSelectedStatus(EditRecordsActivity.this.collectRecordList);
                adapter.setCollectRecordList(EditRecordsActivity.this.collectRecordList);
            }
        });
    }

    @Override
    public void showToast(final String info) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CustomizedToast.showShort(EditRecordsActivity.this,info);
            }
        });
    }

    private void initIsSelectedStatus(List<CollectRecord> pageList) {
        isSelectedList = new ArrayList<>();
        for (int i = 0; i < pageList.size(); i++) {
            isSelectedList.add(false);
        }
        adapter.setIsSelectedList(isSelectedList);
    }

    private void reNameDialog(){
        builder = new CustomInputDialog.Builder(this);
        builder.setTitle("重命名");
        builder.setPositiveButton(getString(R.string.btn_confirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (builder.getInputText().isEmpty()) {
                    Toast.makeText(EditRecordsActivity.this, getString(R.string.please_input_title), Toast.LENGTH_SHORT).show();
                } else {
//                    MobclickAgent.onEvent(EditRecordsActivity.this, "create_archives");
                    mPresenter.reNameCollectRecords(collectRecordList,isSelectedList,builder.getInputText());
                    dialog.dismiss();
                }
            }
        });
        builder.setNegativeButton(getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelableMethod(false);
        builder.setInputText("请输入名称");
        builder.createDoubleButton().show();
    }
}
