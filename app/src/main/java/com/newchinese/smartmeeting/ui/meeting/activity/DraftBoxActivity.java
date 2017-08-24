package com.newchinese.smartmeeting.ui.meeting.activity;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.app.App;
import com.newchinese.smartmeeting.base.BaseActivity;
import com.newchinese.smartmeeting.base.BasePresenter;
import com.newchinese.smartmeeting.contract.DraftBoxContract;
import com.newchinese.smartmeeting.listener.OnDeviceItemClickListener;
import com.newchinese.smartmeeting.listener.PopWindowListener;
import com.newchinese.smartmeeting.log.XLog;
import com.newchinese.smartmeeting.model.bean.NotePage;
import com.newchinese.smartmeeting.model.event.CheckBlueStateEvent;
import com.newchinese.smartmeeting.model.event.ConnectEvent;
import com.newchinese.smartmeeting.model.event.ElectricityReceivedEvent;
import com.newchinese.smartmeeting.model.event.OpenBleEvent;
import com.newchinese.smartmeeting.model.event.ScanEvent;
import com.newchinese.smartmeeting.model.event.ScanResultEvent;
import com.newchinese.smartmeeting.model.listener.OnItemClickedListener;
import com.newchinese.smartmeeting.presenter.meeting.DraftBoxPresenter;
import com.newchinese.smartmeeting.ui.meeting.adapter.DraftPageRecyAdapter;
import com.newchinese.smartmeeting.util.BluCommonUtils;
import com.newchinese.smartmeeting.util.CustomizedToast;
import com.newchinese.smartmeeting.util.DataCacheUtil;
import com.newchinese.smartmeeting.util.SharedPreUtils;
import com.newchinese.smartmeeting.widget.BluePopUpWindow;
import com.newchinese.smartmeeting.widget.ScanResultDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Description:
 * author         xulei
 * Date           2017/8/18 16:34
 */
public class DraftBoxActivity extends BaseActivity<DraftBoxPresenter, BluetoothDevice> implements
        DraftBoxContract.View<BluetoothDevice>, PopWindowListener, OnDeviceItemClickListener,
        OnItemClickedListener, View.OnClickListener {
    private static final String TAG = "DraftBoxActivity";
    private static boolean isFirstTime = true;
    @BindView(R.id.iv_empty)
    ImageView ivEmpty; //空页面背景
    @BindView(R.id.iv_back)
    ImageView ivBack; //返回
    @BindView(R.id.tv_title)
    TextView tvTitle; //标题
    @BindView(R.id.tv_right)
    TextView tvRight; //创建会议，全选/全不选
    @BindView(R.id.iv_pen)
    TextView ivPen; //笔图标
    @BindView(R.id.rv_page_list)
    RecyclerView rvPageList;
    private View viewCreateRecord;
    private TextView tvCancel, tvCreate;
    private PopupWindow pwCreateRecord;
    private boolean isEditMode = false;
    private String classifyName; //分类名
    private List<NotePage> notePageList = new ArrayList<>();
    private List<Boolean> isSelectedList = new ArrayList<>();

    private ScanResultDialog scanResultDialog;
    private BluePopUpWindow bluePopUpWindow;
    private ViewGroup root_view;
    private DraftPageRecyAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_draft_box;
    }

    @Override
    protected void onViewCreated(Bundle savedInstanceState) {
        super.onViewCreated(savedInstanceState);
        root_view = (ViewGroup) findViewById(R.id.rl_parent);
        //初始化地图弹出窗口view
        viewCreateRecord = LayoutInflater.from(this).inflate(R.layout.layout_create_record, null);
        tvCancel = (TextView) viewCreateRecord.findViewById(R.id.tv_cancel);
        tvCreate = (TextView) viewCreateRecord.findViewById(R.id.tv_create);
        //初始化PopupWindow
        pwCreateRecord = new PopupWindow(viewCreateRecord, RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        pwCreateRecord.setAnimationStyle(R.style.popup_anim);// 淡入淡出动画
        pwCreateRecord.setBackgroundDrawable(new BitmapDrawable());
        pwCreateRecord.setOutsideTouchable(false);
        //初始化RecyclerView
        rvPageList.setHasFixedSize(true);
        rvPageList.setLayoutManager(new GridLayoutManager(this, 2));
        rvPageList.setItemAnimator(new DefaultItemAnimator());
    }

    private void initView() {
        if (mPresenter.isConnected()) {
//            mPresenter.updatePenState(BasePresenter.BSTATE_CONNECTED);
            ivPen.setBackgroundResource(R.mipmap.pen_succes);
            ivPen.clearAnimation();
            animation.cancel();
        } else {
//            mPresenter.updatePenState(BasePresenter.BSTATE_DISCONNECT);
            ivPen.setBackgroundResource(R.mipmap.pen_break);
            ivPen.clearAnimation();
            animation.cancel();
        }
    }

    @Override
    protected DraftBoxPresenter initPresenter() {
        return new DraftBoxPresenter();
    }

    @Override
    protected void initStateAndData() {
        EventBus.getDefault().register(this);

        classifyName = getIntent().getStringExtra("classify_name");
        tvTitle.setText(classifyName);
        ivPen.setBackgroundColor(Color.parseColor("#a6a6a6"));

        scanResultDialog = new ScanResultDialog(this);
        bluePopUpWindow = new BluePopUpWindow(this, this);

        //初始化Adapter
        adapter = new DraftPageRecyAdapter(this);
        rvPageList.setAdapter(adapter);
        //初始化蓝牙图标状态
        initView();
    }

    @Override
    protected void initListener() {
        mPresenter.initListener();
        scanResultDialog.setOnDeviceItemClickListener(this);
        adapter.setOnItemClickedListener(this);
        tvCancel.setOnClickListener(this);
        tvCreate.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel: //取消
                pwCreateRecord.dismiss();
                isEditMode = false;
                tvRight.setText("创建会议");
                break;
            case R.id.tv_create: //生成记录
                mPresenter.createSelectedRecords(notePageList, isSelectedList);
                isEditMode = false;
                tvRight.setText("创建会议");
                pwCreateRecord.dismiss();
                break;
        }
    }

    @OnClick({R.id.iv_back, R.id.iv_pen, R.id.tv_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_pen:
                checkBle();
                break;
            case R.id.tv_right:
                if (!isEditMode) { //不是编辑模式，生成或取消时置为false,text置为创建会议
                    tvRight.setText("全选");
                    isEditMode = true;
                    //显示弹窗
                    pwCreateRecord.showAtLocation(findViewById(R.id.rl_parent), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                } else { //编辑模式
                    if ("全选".equals(tvRight.getText().toString())) { //点击全选
                        for (int i = 0; i < isSelectedList.size(); i++) {
                            isSelectedList.set(i, true);
                        }
                        tvRight.setText("全不选");
                    } else { //点击全不选
                        for (int i = 0; i < isSelectedList.size(); i++) {
                            isSelectedList.set(i, false);
                        }
                        tvRight.setText("全选");
                    }
                    adapter.setIsSelectedList(isSelectedList);
                }
                break;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            initView();
            if (isFirstTime) {
                XLog.d(TAG, TAG + " onWindowFocusChanged");
                checkBle();
                isFirstTime = false;
            }
        }
    }

    /**
     * 扫描结束调用该方法
     */
    @Override
    public void onScanComplete() {
        if (mPresenter.isConnected()) {
            scanResultDialog.show();
            EventBus.getDefault().post(new ScanResultEvent(1));
        } else {
            EventBus.getDefault().post(new ScanResultEvent(0));
            onComplete();
        }
    }

    private void checkBle() {
        boolean bluetoothOpen = mPresenter.isBluetoothOpen();
        if (!bluetoothOpen) {
            bluePopUpWindow.showAtLocation(root_view, Gravity.BOTTOM, 0, 0);
        } else {
            XLog.d("haha", "连接状态" + mPresenter.isConnected());
            mPresenter.scanBlueDevice();
            mPresenter.updatePenState(BasePresenter.BSTATE_SCANNING);
            ivPen.startAnimation(animation);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * 每扫描到一个设备，调用该方法
     *
     * @param s
     */
    @Override
    public void showResult(BluetoothDevice s) {
        scanResultDialog.addDevice(s);
    }

    @Subscribe
    public void onEvent(ConnectEvent type) {//重新连接
        if (mPresenter.isConnected()) {
            mPresenter.disConnect();
        }
        mPresenter.connectDevice(type.getAddress());
    }

    private void onComplete() {
        int count = scanResultDialog.getCount();
        List<BluetoothDevice> devices = scanResultDialog.getDevices();
        String key = SharedPreUtils.getString(App.getAppliction(), BluCommonUtils.SAVE_WRITE_PEN_KEY);
        String address = SharedPreUtils.getString(App.getAppliction(), BluCommonUtils.SAVE_CONNECT_BLU_INFO_ADDRESS);
        if (count == 0) {//如果没有搜索到笔，提示
            CustomizedToast.showShort(App.getAppliction(), "请开启酷神笔！");
        } else {
            for (BluetoothDevice device : devices) {
                XLog.d(TAG, "连接的所有设备：" + device.getAddress());
                if (device.getAddress().equals(address)) {
                    EventBus.getDefault().post(new ConnectEvent(address, 0));
                    return;
                }
            }
            if (count == 1) {
                showDialog(devices.get(0).getAddress());
            } else {
                scanResultDialog.show();
            }
        }
    }

    private void showDialog(final String address) {
        new AlertDialog.Builder(this)
                .setTitle("是否连接新笔" + address)
                .setPositiveButton("连接", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EventBus.getDefault().post(new ConnectEvent(address, 0));
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create().show();
    }

    /**
     * 点击确认打开蓝牙
     */
    @Override
    public void onConfirm() {
        mPresenter.openBle();
    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onSuccess() {
        XLog.d(TAG, TAG + " onSuccess");
        SharedPreUtils.setString(App.getAppliction(), BluCommonUtils.SAVE_CONNECT_BLU_INFO_ADDRESS, BluCommonUtils.getDeviceAddress());
        EventBus.getDefault().post(new CheckBlueStateEvent(1));
//        mPresenter.updatePenState(BasePresenter.BSTATE_CONNECTED);
        ivPen.setBackgroundResource(R.mipmap.pen_succes);
        ivPen.clearAnimation();
        animation.cancel();
        mPresenter.startTimer();
    }

    @Override
    public void onFailed() {
        EventBus.getDefault().post(new CheckBlueStateEvent(-1));
//        mPresenter.updatePenState(BasePresenter.BSTATE_DISCONNECT);
        ivPen.setBackgroundResource(R.mipmap.pen_break);
        ivPen.setText("");
        animation.cancel();
        ivPen.clearAnimation();
    }

    @Override
    public void onConnecting() {
        EventBus.getDefault().post(new CheckBlueStateEvent(0));
//        mPresenter.updatePenState(BasePresenter.BSTATE_CONNECTING);
        ivPen.setBackgroundResource(R.mipmap.pen_loading);
        ivPen.startAnimation(animation);
        ivPen.setText("");
    }

    @Override
    public void onDisconnected() {
        XLog.d(TAG, TAG + "中的onDisconnected被调用1");
        EventBus.getDefault().post(new CheckBlueStateEvent(-1));
//        mPresenter.updatePenState(BasePresenter.BSTATE_DISCONNECT);
        ivPen.setBackgroundResource(R.mipmap.pen_break);
        animation.cancel();
        mPresenter.stopTimer();
        ivPen.clearAnimation();
        ivPen.setText("");
    }

    @Override
    public void onElecReceived(String s) {
        if (ivPen != null) {
            ivPen.setText(s + "%");
            EventBus.getDefault().post(new ElectricityReceivedEvent(s));
            Log.d("hahaha", "收到电量信息 笔不为空:" + ivPen.getText());
        }
    }

    @Override
    public void setState(int id) {
        ivPen.setBackgroundResource(id);
    }

    /**
     * 设备列表的item点击事件
     *
     * @param add
     */
    @Override
    public void onDeviceClick(String add) {
        if (!add.equals(SharedPreUtils.getString(this, BluCommonUtils.SAVE_CONNECT_BLU_INFO_ADDRESS)) || !mPresenter.isConnected()) {
            EventBus.getDefault().post(new ConnectEvent(add, 0));
        }
    }

    /**
     * 获取到数据库当前活动记录的所有页
     */
    @Override
    public void getActivePageList(List<NotePage> pageList) {
        notePageList.clear();
        notePageList.addAll(pageList);
        //更新当前记录表所有页缓存
        DataCacheUtil.getInstance().setActiveNotePageList(pageList);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i("test_greendao", "" + notePageList.toString());
                if (notePageList != null && notePageList.size() > 0) {
                    tvRight.setVisibility(View.VISIBLE);
                    tvRight.setText("创建会议");
                    ivEmpty.setVisibility(View.GONE);
                    adapter.setNotePageList(notePageList);
                    //初始化是否被选择的集合
                    initIsSelectedStatus(notePageList);
                } else {
                    tvRight.setVisibility(View.GONE);
                    ivEmpty.setVisibility(View.VISIBLE);
                }
            }
        });
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

    @Override
    protected void onResume() {
        super.onResume();
        //加载数据库当前活动记录的所有页
        mPresenter.loadActivePageList();
        //置位非编辑模式
        isEditMode = false;
        pwCreateRecord.dismiss();
    }

    /**
     * 列表点击事件
     */
    @Override
    public void onClick(View view, int position) {
        if (!isEditMode) { //不是编辑状态点击则跳转详情
            NotePage selectNotePage = adapter.getItem(position);
            DataCacheUtil.getInstance().setActiveNotePage(selectNotePage); //更新活动页
            Intent intent = new Intent(this, DrawingBoardActivity.class);
            intent.putExtra(DrawingBoardActivity.TAG_PAGE_INDEX, selectNotePage.getPageIndex());
            startActivity(intent);
        } else { //编辑状态点击则为相反选中效果
            isSelectedList.set(position, !adapter.getIsSelectedList().get(position));
            adapter.setIsSelectedList(isSelectedList);
        }
    }

    /**
     * 列表长点击事件
     */
    @Override
    public void onLongClick(View view, int position) {

    }

    @Subscribe
    public void onEvent(OpenBleEvent openBleEvent) {
        mPresenter.openBle();
    }

    @Subscribe
    public void onEvent(ScanEvent scanEvent) {
        mPresenter.scanBlueDevice();
    }
}
