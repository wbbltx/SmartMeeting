package com.newchinese.smartmeeting.ui.meeting.activity;

import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.app.App;
import com.newchinese.smartmeeting.base.BaseActivity;
import com.newchinese.smartmeeting.contract.DraftBoxActContract;
import com.newchinese.smartmeeting.entity.bean.NotePage;
import com.newchinese.smartmeeting.entity.event.AddDeviceEvent;
import com.newchinese.smartmeeting.entity.event.CheckBlueStateEvent;
import com.newchinese.smartmeeting.entity.event.ConnectEvent;
import com.newchinese.smartmeeting.entity.event.ElectricityReceivedEvent;
import com.newchinese.smartmeeting.entity.event.HisInfoEvent;
import com.newchinese.smartmeeting.entity.event.OnHisInfoEvent;
import com.newchinese.smartmeeting.entity.event.OpenBleEvent;
import com.newchinese.smartmeeting.entity.event.ScanEvent;
import com.newchinese.smartmeeting.entity.event.ScanResultEvent;
import com.newchinese.smartmeeting.entity.listener.OnDeviceItemClickListener;
import com.newchinese.smartmeeting.entity.listener.OnItemClickedListener;
import com.newchinese.smartmeeting.entity.listener.PopWindowListener;
import com.newchinese.smartmeeting.presenter.meeting.DraftBoxPresenter;
import com.newchinese.smartmeeting.ui.meeting.adapter.DraftPageRecyAdapter;
import com.newchinese.smartmeeting.util.BluCommonUtils;
import com.newchinese.smartmeeting.util.CustomizedToast;
import com.newchinese.smartmeeting.util.DataCacheUtil;
import com.newchinese.smartmeeting.util.DateUtils;
import com.newchinese.smartmeeting.util.SharedPreUtils;
import com.newchinese.smartmeeting.util.log.XLog;
import com.newchinese.smartmeeting.widget.BluePopUpWindow;
import com.newchinese.smartmeeting.widget.CustomInputDialog;
import com.newchinese.smartmeeting.widget.FirstTimeHintDialog;
import com.newchinese.smartmeeting.widget.ScanResultDialog;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import pl.droidsonroids.gif.GifImageView;

/**
 * Description:
 * author         xulei
 * Date           2017/8/18 16:34
 */
public class DraftBoxActivity extends BaseActivity<DraftBoxPresenter, BluetoothDevice> implements
        DraftBoxActContract.View<BluetoothDevice>, PopWindowListener, OnDeviceItemClickListener,
        OnItemClickedListener, View.OnClickListener, DialogInterface.OnDismissListener {
    private static final String TAG = "DraftBoxActivity";
    //    private static boolean isFirstTime = true;
    @BindView(R.id.iv_empty)
    ImageView ivEmpty; //空页面背景
    @BindView(R.id.iv_back)
    ImageView ivBack; //返回
    @BindView(R.id.iv_right)
    ImageView ivRight; //创建会议
    @BindView(R.id.tv_title)
    TextView tvTitle; //标题
    @BindView(R.id.tv_right)
    TextView tvRight; //全选/全不选
    @BindView(R.id.iv_pen)
    ImageView ivPen; //笔图标
    @BindView(R.id.tv_power)
    TextView tvPower;
    @BindView(R.id.rv_page_list)
    RecyclerView rvPageList;
    @BindView(R.id.rl_remind)
    RelativeLayout rlRemind;
    @BindView(R.id.gifImageView)
    GifImageView gifImageView;
    private View viewCreateRecord;
    private TextView tvCancel, tvCreate;
    private PopupWindow pwCreateRecord;
    private CustomInputDialog.Builder builder;

    private boolean isEditMode = false;
    private String classifyName; //分类名
    private List<NotePage> notePageList = new ArrayList<>();
    private List<Boolean> isSelectedList = new ArrayList<>();

    private ScanResultDialog scanResultDialog;
    private BluePopUpWindow bluePopUpWindow;
    private ViewGroup root_view;
    private DraftPageRecyAdapter adapter;
    private FirstTimeHintDialog.Builder hintbuilder;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_draft_box;
    }

    @Override
    protected void onViewCreated(Bundle savedInstanceState) {
        if (SharedPreUtils.getBoolean(BluCommonUtils.IS_FIRST_INSTALL, true)) {
            startActivity(new Intent(this, MaskActivity.class));
        }
        XLog.d(TAG, TAG + "onViewCreated " + isFinishing());
        super.onViewCreated(savedInstanceState);
        root_view = (ViewGroup) findViewById(R.id.rl_parent);
        //初始化地图弹出窗口view
        viewCreateRecord = LayoutInflater.from(this).inflate(R.layout.layout_create_record, null);
        tvCancel = (TextView) viewCreateRecord.findViewById(R.id.tv_cancel);
        tvCreate = (TextView) viewCreateRecord.findViewById(R.id.tv_create);
        initView();
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
        switch (DataCacheUtil.getInstance().getPenState()) {
            case BluCommonUtils.PEN_CONNECTED:
                if (DataCacheUtil.getInstance().isLowPower()) {
                    setState(R.mipmap.pen_low_power);
                } else {
                    setState(R.mipmap.pen_normal_power);
                }
                break;
            case BluCommonUtils.PEN_CONNECTING:
//                setState(R.mipmap.weilianjie);
                break;
            default:
                setState(R.mipmap.pen_disconnect);
                break;
        }
    }

    @Override
    protected DraftBoxPresenter initPresenter() {
        return new DraftBoxPresenter();
    }

    @Override
    protected void initStateAndData() {
        EventBus.getDefault().register(this);

        classifyName = getIntent().getStringExtra(BluCommonUtils.CLASSIFY_NAME);
        tvTitle.setText(classifyName);
        tvRight.setVisibility(View.GONE);
        ivRight.setImageResource(R.mipmap.icon_create);
        ivRight.setVisibility(View.GONE);
        scanResultDialog = new ScanResultDialog(this);
        bluePopUpWindow = new BluePopUpWindow(this, this);
        XLog.d(TAG, TAG + "initStateAndData " + isFinishing());
        //初始化Adapter
        adapter = new DraftPageRecyAdapter(this);
        rvPageList.setAdapter(adapter);
//        //初始化蓝牙图标状态
//        initView();
    }

    @Override
    protected void initListener() {
        mPresenter.initListener();
        scanResultDialog.setOnDeviceItemClickListener(this);
        scanResultDialog.setOnDismissListener(this);
        adapter.setOnItemClickedListener(this);
        tvCancel.setOnClickListener(this);
        tvCreate.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel: //取消
                resetEditMode();
                adapter.setIsSelectedList(isSelectedList);
                break;
            case R.id.tv_create: //生成记录
                //先判断是否未选择
                boolean isSelectEmpty = true;
                for (Boolean isSelected : isSelectedList) {
                    if (isSelected) {
                        isSelectEmpty = false;
                    }
                }
                if (!isSelectEmpty) {
                    createDialog();
                } else {
                    Toast.makeText(this, getString(R.string.please_select_record), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @OnClick({R.id.iv_back, R.id.iv_pen, R.id.tv_right, R.id.iv_right, R.id.iv_close})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_pen:
                checkBle(true);
                break;
            case R.id.tv_right:
                if (getString(R.string.select_all).equals(tvRight.getText().toString())) { //点击全选
                    for (int i = 0; i < isSelectedList.size(); i++) {
                        isSelectedList.set(i, true);
                    }
                    tvRight.setText(getString(R.string.not_select_all));
                } else { //点击全不选
                    for (int i = 0; i < isSelectedList.size(); i++) {
                        isSelectedList.set(i, false);
                    }
                    tvRight.setText(getString(R.string.select_all));
                }
                adapter.setIsSelectedList(isSelectedList);
                break;
            case R.id.iv_right:
                isEditMode = true;
                ivRight.setVisibility(View.GONE);
                tvRight.setVisibility(View.VISIBLE);
                pwCreateRecord.showAtLocation(findViewById(R.id.rl_parent), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.iv_close:
                rlRemind.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 创建Dialog
     */
    private void createDialog() {
        builder = new CustomInputDialog.Builder(this);
        builder.setTitle(getString(R.string.change_record_title));
        builder.setPositiveButton(getString(R.string.btn_confirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (builder.getInputText().isEmpty()) {
                    Toast.makeText(DraftBoxActivity.this, getString(R.string.please_input_title), Toast.LENGTH_SHORT).show();
                } else {
                    MobclickAgent.onEvent(DraftBoxActivity.this, "create_archives");
                    mPresenter.createSelectedRecords(notePageList, isSelectedList, builder.getInputText());
                    dialog.dismiss();
                    resetEditMode();
                }
            }
        });
        builder.setNegativeButton(getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelableMethod(false);
        builder.setInputText(classifyName + DateUtils.formatLongDate3(System.currentTimeMillis()));
        builder.createDoubleButton().show();
    }

    private void createHintDialog() {
        hintbuilder = new FirstTimeHintDialog.Builder(this);
        hintbuilder.setPositiveButton(new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                EventBus.getDefault().post(new ScanEvent());
                CustomizedToast.showShort(DraftBoxActivity.this, getString(R.string.scan_blue_pen));
                dialog.dismiss();
            }
        });
        hintbuilder.setCancelableMethod(false);
        hintbuilder.createDoubleButton().show();
    }

    /**
     * 重置为非编辑模式
     */
    private void resetEditMode() {
        isEditMode = false;
        ivRight.setVisibility(View.VISIBLE);
        tvRight.setVisibility(View.GONE);
        pwCreateRecord.dismiss();
        for (int i = 0; i < isSelectedList.size(); i++) {
            isSelectedList.set(i, false);
        }
    }

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        XLog.d(TAG, TAG + " onWindowFocusChanged " + DataCacheUtil.getInstance().isFirstTime());
//        if (hasFocus) {
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    initView();
//                    if (DataCacheUtil.getInstance().isFirstTime()) {
//                        DataCacheUtil.getInstance().setFirstTime(false);
//                        checkBle(false);
//                    }
//                }
//            }, 1500);
//        }
//    }

    /**
     * 扫描结束调用该方法
     */
    @Override
    public void onScanComplete() {
        XLog.d(TAG, TAG + " onScanComplete " + !isFinishing());
        if (DataCacheUtil.getInstance().getPenState() == BluCommonUtils.PEN_CONNECTED && !isFinishing()) {
            scanResultDialog
                    .setContent(SharedPreUtils.getString(this, BluCommonUtils.SAVE_CONNECT_BLU_INFO_NAME), "1")
                    .show();
            EventBus.getDefault().post(new ScanResultEvent(1));
        } else {
            onComplete();
        }
    }

    private void onComplete() {
        if (scanResultDialog == null) {
            scanResultDialog = new ScanResultDialog(this);
        }
        int count = scanResultDialog.getCount();
        XLog.d(TAG, TAG + " onComplete " + count);
        List<BluetoothDevice> devices = scanResultDialog.getDevices();
        String address = SharedPreUtils.getString(App.getAppliction(), BluCommonUtils.SAVE_CONNECT_BLU_INFO_NAME);
        if (count == 0) {//如果没有搜索到笔，提示
            XLog.d(TAG, TAG + " 没有搜索到笔 ");
            EventBus.getDefault().post(new ScanResultEvent(0));
            CustomizedToast.showShort(this, getString(R.string.please_open_pen));
            setState(R.mipmap.pen_disconnect);
            hideGif();
        } else {
            XLog.d(TAG, TAG + " 搜索到笔 "+devices.size());
            for (BluetoothDevice device : devices) {
                if (device.getName().equals(address)) {
                    if (DataCacheUtil.getInstance().getPenState() != BluCommonUtils.PEN_CONNECTED) {
                        EventBus.getDefault().post(new ConnectEvent(device, 0));
                        return;
                    }
                    break;
                }
            }
            if (count == 1) {
                EventBus.getDefault().post(new ConnectEvent(devices.get(0), 0));
                return;
            }
            if (scanResultDialog != null && !isFinishing()) {
                EventBus.getDefault().post(new ScanResultEvent(1));
                scanResultDialog.setContent(address, "0");
                scanResultDialog.show();
            }
        }
    }

    /**
     * 每扫描到一个设备，调用该方法
     *
     * @param s
     */
    @Override
    public void showResult(BluetoothDevice s) {
        XLog.d(TAG, TAG + " showResult " + s.getAddress());
        scanResultDialog.addDevice(s);
        EventBus.getDefault().post(new AddDeviceEvent(s));
    }

    private void checkBle(boolean isClick) {
        boolean bluetoothOpen = mPresenter.isBluetoothOpen();
        if (!bluetoothOpen) {
            bluePopUpWindow.showAtLocation(root_view, Gravity.BOTTOM, 0, 0);
        } else {
            if (DataCacheUtil.getInstance().getPenState() == BluCommonUtils.PEN_CONNECTED && !isClick) {
                mPresenter.updatePenState(DraftBoxPresenter.BSTATE_CONNECTED_NORMAL);
            } else {
                if (!mPresenter.isScanning()) {
                    EventBus.getDefault().post(new ScanEvent());
                    mPresenter.updatePenState(DraftBoxPresenter.BSTATE_SCANNING);
                }
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Subscribe
    public void onEvent(ConnectEvent type) {//重新连接
        if (DataCacheUtil.getInstance().getPenState() == BluCommonUtils.PEN_CONNECTED) {
            mPresenter.disConnect();
        }
        mPresenter.connectDevice(type.getDevice());
        showGif();
    }

    /**
     * 点击确认打开蓝牙 同时弹出使用提示框
     */
    @Override
    public void onConfirm(int tag) {
        XLog.d(TAG, TAG + " onConfirm");
        mPresenter.openBle();
        boolean b = SharedPreUtils.getBoolean(App.getAppliction(), BluCommonUtils.IS_FIRST_LAUNCH, true);
        if (b) {//第一次启动应用，弹出如何使用对话框
            createHintDialog();
        } else {//不是第一次启动该应用，不弹出，直接扫描蓝牙
            Flowable.timer(3, TimeUnit.SECONDS).subscribe(new Consumer<Long>() {
                @Override
                public void accept(Long aLong) throws Exception {
                    EventBus.getDefault().post(new ScanEvent());
                }
            });
            mPresenter.updatePenState(DraftBoxPresenter.BSTATE_SCANNING);
        }
    }

    @Override
    public void onCancel(int i) {
        XLog.d(TAG, TAG + " onCancel");
    }

    @Override
    public void onSuccess() {
        XLog.d(TAG, TAG + " onSuccess");
        hideGif();
        EventBus.getDefault().post(new CheckBlueStateEvent(1));
//        将该页图标设置为连接成功
//        mPresenter.updatePenState(DraftBoxPresenter.BSTATE_CONNECTED_NORMAL);
        setState(R.mipmap.pen_normal_power);
//        开启定时任务获取电量
        mPresenter.startTimer();
    }

    @Override
    public void onFailed() {
        hideGif();
        CustomizedToast.showShort(this, "连接失败 请点击图标重新连接");
        EventBus.getDefault().post(new CheckBlueStateEvent(-1));
        mPresenter.updatePenState(DraftBoxPresenter.BSTATE_DISCONNECT);
    }

    @Override
    public void onConnecting() {
        EventBus.getDefault().post(new CheckBlueStateEvent(0));
        mPresenter.updatePenState(DraftBoxPresenter.BSTATE_CONNECTING);
    }

    @Override
    public void onDisconnected() {
        XLog.d(TAG, TAG + " onDisconnected");
        hideGif();
        EventBus.getDefault().post(new CheckBlueStateEvent(-1));
//        mPresenter.updatePenState(DraftBoxPresenter.BSTATE_DISCONNECT);
        CustomizedToast.showShort(this, "连接失败 请点击图标重新连接");
        setState(R.mipmap.pen_disconnect);
        mPresenter.stopTimer();
    }

    @Override
    public void onElecReceived(String s) {
        int i = Integer.parseInt(s);
        XLog.d(TAG, TAG + " onElecReceived " + i);
        tvPower.setText(i+"");
        EventBus.getDefault().post(new ElectricityReceivedEvent(s));
        boolean lowPower = DataCacheUtil.getInstance().isLowPower();
        if (i <= 30) {//默认图标是电量正常，小于30才更新图标
            if (!lowPower) {//只在电量发生变化的时候
                EventBus.getDefault().post(new ElectricityReceivedEvent(s, true));
                mPresenter.updatePenState(DraftBoxPresenter.BSTATE_CONNECTED_LOW);
                DataCacheUtil.getInstance().setLowPower(true);
            }
        } else {//上一次低电量，这次正常 电量的变化点
            if (lowPower) {
                EventBus.getDefault().post(new ElectricityReceivedEvent(s, false));
                mPresenter.updatePenState(DraftBoxPresenter.BSTATE_CONNECTED_NORMAL);
                DataCacheUtil.getInstance().setLowPower(false);
            }
        }
    }

    @Override
    public DraftBoxActivity initBluListener() {
        return this;
    }

    @Override
    public void showAnim() {
        EventBus.getDefault().post(new OnHisInfoEvent("deletingOrreading"));
        showGif();
    }

    @Override
    public void dismissAnim() {
        EventBus.getDefault().post(new OnHisInfoEvent("done"));
        hideGif();
    }

    @Override
    public void setState(int id) {
        if (ivPen != null)
            ivPen.setImageResource(id);
    }

    @Override
    public void showToast(final String toastContent) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(DraftBoxActivity.this, toastContent, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 设备列表的item点击事件
     *
     * @param add
     */
    @Override
    public void onDeviceClick(BluetoothDevice add) {
        if (!add.getAddress().equals(SharedPreUtils.getString(this, BluCommonUtils.SAVE_CONNECT_BLU_INFO_ADDRESS)) || !mPresenter.isConnected()) {
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
                if (!isFinishing()) {
                    adapter.setNotePageList(notePageList); //使用RecyclerView数据保持一致
                    if (notePageList != null && notePageList.size() > 0) {
                        if (ivRight != null && ivEmpty != null && rlRemind != null) {
                            ivRight.setVisibility(View.VISIBLE);
                            ivEmpty.setVisibility(View.GONE);
                            //初始化是否被选择的集合
                            initIsSelectedStatus(notePageList);
                            //显示提示框
                            rlRemind.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (ivRight != null && ivEmpty != null) {
                            ivRight.setVisibility(View.GONE);
                            ivEmpty.setVisibility(View.VISIBLE);
                        }
                        checkBle(false);
                    }
                    if (tvRight != null) {
                        tvRight.setText(getString(R.string.select_all));
                        tvRight.setVisibility(View.GONE);
                    }
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
        //加载数据库当前活动记录的所有页，延迟500ms加载是为了让back时的截图截一会儿才能加载出来
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mPresenter.loadActivePageList();
            }
        }, 500);
        //置位非编辑模式
        isEditMode = false;
        tvRight.setVisibility(View.GONE);
        pwCreateRecord.dismiss();
        for (int i = 0; i < isSelectedList.size(); i++) {
            isSelectedList.set(i, false);
        }
        adapter.setIsSelectedList(isSelectedList);
        if (scanResultDialog.isShowing()) {
            scanResultDialog.dismiss();
        }
        if (hisInfoWindow != null && hisInfoWindow.isShowing()) {
            hisInfoWindow.dismiss();
        }
    }

    /**
     * 列表点击事件
     */
    @Override
    public void onClick(View view, int position) {
        if (!isEditMode) { //不是编辑状态点击则跳转详情
            if (!notePageList.isEmpty()) {
                NotePage selectNotePage = adapter.getItem(position);
                DataCacheUtil.getInstance().setActiveNotePage(selectNotePage); //更新活动页
                Intent intent = new Intent(this, DrawingBoardActivity.class);
                intent.putExtra(DrawingBoardActivity.TAG_PAGE_INDEX, selectNotePage.getPageIndex());
                startActivity(intent);
            }
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
//        DataCacheUtil.getInstance().setPenState(BluCommonUtils.PEN_SCANNING);
        XLog.d(TAG, TAG + " 将蓝牙状态设置为扫描");
        mPresenter.scanBlueDevice();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showGif();
            }
        });
    }

    @Subscribe
    public void onEvent(){
        mPresenter.requestElectricity();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        DataCacheUtil.getInstance().setFirstTime(true);
        super.onDestroy();
    }

    @Override
    public void onHistoryDetected(PopWindowListener listener) {
        XLog.d(TAG, TAG + " onHistoryDetected");
        EventBus.getDefault().post(new HisInfoEvent(listener));
        if (!isFinishing())
            showDialog(listener, findViewById(android.R.id.content));
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
//        progressBar.setVisibility(View.GONE);
        initView();
        hideGif();
    }

    private void showGif(){
        gifImageView.setVisibility(View.VISIBLE);
    }

    private void hideGif(){
        gifImageView.setVisibility(View.GONE);
    }
}
