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
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.newchinese.smartmeeting.entity.event.EditModeEvent;
import com.newchinese.smartmeeting.entity.event.ElectricityReceivedEvent;
import com.newchinese.smartmeeting.entity.event.HisInfoEvent;
import com.newchinese.smartmeeting.entity.event.OnHisInfoEvent;
import com.newchinese.smartmeeting.entity.event.OpenBleEvent;
import com.newchinese.smartmeeting.entity.event.RequestPowerEvent;
import com.newchinese.smartmeeting.entity.event.ScanEvent;
import com.newchinese.smartmeeting.entity.event.ScanResultEvent;
import com.newchinese.smartmeeting.entity.http.Kits;
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
import com.newchinese.smartmeeting.widget.GuiDangInfoWindow;
import com.newchinese.smartmeeting.widget.MenuPopUpWindow;
import com.newchinese.smartmeeting.widget.NormalDialog;
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
        OnItemClickedListener, View.OnClickListener, DialogInterface.OnDismissListener, MenuPopUpWindow.OnMenuClicked {
    private static final String TAG = "DraftBoxActivity";
    //    private static boolean isFirstTime = true;
    @BindView(R.id.iv_empty)
    ImageView ivEmpty; //空页面背景
    @BindView(R.id.iv_back)
    ImageView ivBack; //返回
    @BindView(R.id.iv_right)
    ImageView ivRight; //归档 删除
    @BindView(R.id.tv_title)
    TextView tvTitle; //标题
    @BindView(R.id.tv_right)
    TextView tvRight; //全选/全不选
    @BindView(R.id.iv_pen)
    ImageView ivPen; //笔图标
    @BindView(R.id.tv_power)//电量
            TextView tvPower;
    @BindView(R.id.rv_page_list)
    RecyclerView rvPageList;
    @BindView(R.id.rl_remind)//归档提醒
            RelativeLayout rlRemind;
    @BindView(R.id.gifImageView)
    GifImageView gifImageView;
    @BindView(R.id.dark_background)//背景
            ImageView bar;
    private int time = 0;
    private View viewCreateRecord;
    private TextView tvCancel, tvCreate;
    private PopupWindow pwCreateRecord;
    private CustomInputDialog.Builder builder;

    private boolean isEditMode = false;
    private String pageMode = BluCommonUtils.NORMAL_MODE;
    private String classifyName; //分类名
    private List<NotePage> notePageList = new ArrayList<>();
    private List<Boolean> isSelectedList = new ArrayList<>();

    private ScanResultDialog scanResultDialog;
    private BluePopUpWindow bluePopUpWindow;
    private ViewGroup root_view;
    private DraftPageRecyAdapter adapter;
    private FirstTimeHintDialog.Builder hintbuilder;
    private MenuPopUpWindow menuPopUpWindow;
    private GuiDangInfoWindow guiDangInfoWindow;
    private NormalDialog.Builder normalDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_draft_box;
    }

    @Override
    protected void onViewCreated(Bundle savedInstanceState) {
        if (SharedPreUtils.getBoolean(BluCommonUtils.IS_FIRST_INSTALL, true)) {
//            startActivity(new Intent(this, MaskActivity.class));
            showMask(true, R.mipmap.mask_two);
        }
        Log.d(TAG, TAG + "onViewCreated " + isFinishing());
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

        //初始化菜单popupWindow
        menuPopUpWindow = new MenuPopUpWindow(this);

        //归档提示弹出框
        guiDangInfoWindow = new GuiDangInfoWindow(this, this);
    }

    private void initView() {
        switch (DataCacheUtil.getInstance().getPenState()) {
            case BluCommonUtils.PEN_CONNECTED:
                mPresenter.requestElectricity();
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
        ivRight.setImageResource(R.mipmap.icon_menu);
        ivRight.setVisibility(View.GONE);
        scanResultDialog = new ScanResultDialog(this);
        bluePopUpWindow = new BluePopUpWindow(this, this);
        //初始化Adapter
        adapter = new DraftPageRecyAdapter(this);
        rvPageList.setAdapter(adapter);
    }

    @Override
    protected void initListener() {
        mPresenter.initListener();
        scanResultDialog.setOnDeviceItemClickListener(this);
        scanResultDialog.setOnDismissListener(this);
        adapter.setOnItemClickedListener(this);
        tvCancel.setOnClickListener(this);
        tvCreate.setOnClickListener(this);
        if (SharedPreUtils.getBoolean(BluCommonUtils.IS_FIRST_INSTALL, true)) {
            ivEmpty.setOnClickListener(this);
        }
        menuPopUpWindow.setOnMenuClicked(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel: //取消
                resetEditMode();
                pageMode = BluCommonUtils.NORMAL_MODE;
                adapter.setIsSelectedList(isSelectedList);
                break;
            case R.id.tv_create: //选择相应的页之后 点击确认 生成记录或者删除
                //先判断是否未选择
                boolean isSelectEmpty = true;
                for (Boolean isSelected : isSelectedList) {
                    if (isSelected) {
                        isSelectEmpty = false;
                    }
                }
                if (!isSelectEmpty) {
                    if (pageMode.equals(BluCommonUtils.DELETE_MODE)) {//直接删除之后将模式设置为普通模式
//                        mPresenter.createSelectedRecords(notePageList, isSelectedList, null, pageMode);
//                        resetEditMode();
                        confirmDialog();
                    } else if (pageMode.equals(BluCommonUtils.EDIT_MODE)) {//弹出归档提示框
                        guiDangInfoWindow.showAtLocation(findViewById(R.id.rl_parent), Gravity.CENTER, 0, 0);
                    }
                } else {
                    Toast.makeText(this, getString(R.string.please_select_record), Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.iv_empty:
                if (time == 0) {
                    showMask(false, 0);
                    checkBle(false);
                    time++;
                } else if (time == 1) {
                    showMask(false, 0);
                    ivEmpty.setOnClickListener(null);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mPresenter.isScanning()) {
            mPresenter.stopScan();
        } else if (mPresenter.isConnected()) {
            mPresenter.openWrite();
        }
        hideGif();
        super.onBackPressed();
    }

    @OnClick({R.id.iv_back, R.id.iv_pen, R.id.tv_right, R.id.rl_right, R.id.iv_close})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                if (mPresenter.isScanning()) {
                    hideGif();
                    mPresenter.stopScan();
                } else if (mPresenter.isConnected()) {
                    mPresenter.openWrite();
                }
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
            case R.id.rl_right:  //点击菜单按钮之后，弹出菜单
//                adapter.setIsSelectedList(isSelectedList);
                menuPopUpWindow.showAtLocation(findViewById(R.id.ll_root), Gravity.RIGHT | Gravity.TOP, (int) Kits.Dimens.pxToDp(this, 50), (int) Kits.Dimens.pxToDp(this, 280));
                break;
            case R.id.iv_close:
                rlRemind.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 创建Dialog
     */
    private void createDialog() {  //最终生成记录之后将模式重置为普通模式
        builder = new CustomInputDialog.Builder(this);
        builder.setTitle(getString(R.string.change_record_title));
        builder.setPositiveButton(getString(R.string.btn_confirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (builder.getInputText().isEmpty()) {
                    Toast.makeText(DraftBoxActivity.this, getString(R.string.please_input_title), Toast.LENGTH_SHORT).show();
                } else {
                    MobclickAgent.onEvent(DraftBoxActivity.this, "create_archives");
                    mPresenter.createSelectedRecords(notePageList, isSelectedList, builder.getInputText(), pageMode);
                    dialog.dismiss();
                    resetEditMode();
                }
            }
        });
        builder.setNegativeButton(getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                pageMode = BluCommonUtils.NORMAL_MODE;
                dialog.dismiss();
            }
        });
        builder.setCancelableMethod(false);
        builder.setInputText(classifyName + DateUtils.formatLongDate5(System.currentTimeMillis()));
        builder.createDoubleButton().show();
    }

    private void createHintDialog() {
        hintbuilder = new FirstTimeHintDialog.Builder(this);
        hintbuilder.setPositiveButton(new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                EventBus.getDefault().post(new ScanEvent().setSource("createHintDialog"));
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
        adapter.setIsSelectable(false);
        pwCreateRecord.dismiss();
        for (int i = 0; i < isSelectedList.size(); i++) {
            isSelectedList.set(i, false);
        }
    }

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
            XLog.d(TAG, TAG + " 搜索到笔 " + devices.size());
            for (BluetoothDevice device : devices) {
                if (!address.equals("") && device.getName().equals(address)) {
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
                if (SharedPreUtils.getBoolean(BluCommonUtils.IS_FIRST_INSTALL, true)) {
                    createHintDialog();
                    return;
                }
                if (!mPresenter.isScanning()) {
                    EventBus.getDefault().post(new ScanEvent().setSource("checkBle"));
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
    public void onEvent(ConnectEvent type) {//连接
        if (DataCacheUtil.getInstance().getPenState() == BluCommonUtils.PEN_CONNECTED) {
            mPresenter.disConnect();
        }
        mPresenter.connectDevice(type.getDevice());
        showGif();
    }

    /**
     * 0 点击确认打开蓝牙 同时弹出使用提示框
     * 3 弹出归档不可编辑的提示框，点击了确认之后，弹出更改名称的提示框
     */
    @Override
    public void onConfirm(int tag) {
        XLog.d(TAG, " onConfirm " + tag);
        switch (tag) {
            case 0:
                mPresenter.openBle();
                boolean b = SharedPreUtils.getBoolean(App.getAppliction(), BluCommonUtils.IS_FIRST_LAUNCH, true);
                if (b) {//第一次启动应用，弹出如何使用对话框
                    createHintDialog();
                } else {//不是第一次启动该应用，不弹出，直接扫描蓝牙
                    Flowable.timer(3, TimeUnit.SECONDS).subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(Long aLong) throws Exception {
                            EventBus.getDefault().post(new ScanEvent().setSource("onConfirm"));
                        }
                    });
                    mPresenter.updatePenState(DraftBoxPresenter.BSTATE_SCANNING);
                }
                break;

            case 3:
                createDialog();
                break;
        }

    }

    @Override
    public void onCancel(int i) {
        XLog.d(TAG, TAG + " onCancel");
    }

    @Override
    public void onSuccess() {
        XLog.d(TAG, TAG + " onSuccess");
        if (SharedPreUtils.getBoolean(BluCommonUtils.IS_FIRST_INSTALL, true)) {
            showMask(true, R.mipmap.mask_three);
        }
        SharedPreUtils.setBoolean(BluCommonUtils.IS_FIRST_INSTALL, false); //首次安装标记置否
        hideGif();
        EventBus.getDefault().post(new CheckBlueStateEvent(1));
//        将该页图标设置为连接成功
        setState(R.mipmap.pen_normal_power);
//        开启定时任务获取电量
        mPresenter.startTimer();
    }

    @Override
    public void onFailed(int i) {//0是超时 1是其他
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
        tvPower.setText("");
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
        tvPower.setText(i + "");
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
            showGif();
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
                        //进入这个界面如果经过查询没有页时才去连接
                        XLog.d(TAG, "状态是 " + pageMode);
                        if (!SharedPreUtils.getBoolean(BluCommonUtils.IS_FIRST_INSTALL, true) && pageMode == BluCommonUtils.NORMAL_MODE) {
                            checkBle(false);
                        }
                    }
                    pageMode = BluCommonUtils.NORMAL_MODE;
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

    @Override
    public void isEmpty(boolean isEmpty) {

    }

    @Subscribe
    public void onEvent(OpenBleEvent openBleEvent) {
        mPresenter.openBle();
    }

    @Subscribe
    public void onEvent(ScanEvent scanEvent) {
//        DataCacheUtil.getInstance().setPenState(BluCommonUtils.PEN_SCANNING);
        XLog.d(TAG, TAG + " 将蓝牙状态设置为扫描 " + scanEvent.getSource());
        mPresenter.scanBlueDevice();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showGif();
            }
        });
    }

    @Subscribe
    public void onEvent(RequestPowerEvent requestPowerEvent) {
        mPresenter.requestElectricity();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        DataCacheUtil.getInstance().setFirstTime(true);
        if (mPresenter.isScanning()) {
            mPresenter.stopScan();
        }
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
        initView();
        hideGif();
    }

    private void showGif() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bar.setVisibility(View.VISIBLE);
                gifImageView.setVisibility(View.VISIBLE);
            }
        });

    }

    private void hideGif() {
        gifImageView.setVisibility(View.GONE);
        bar.setVisibility(View.GONE);
    }

    private void showMask(boolean show, int res) {
        if (show) {
            ivEmpty.setVisibility(View.VISIBLE);
            ivEmpty.setBackground(getResources().getDrawable(R.mipmap.empty_icon));
            ivEmpty.setImageResource(res);
        } else {
            ivEmpty.setImageResource(R.mipmap.empty_icon);
        }
    }

    public void confirmDialog() {
        normalDialog = new NormalDialog.Builder(this);
        normalDialog.setTitle(getString(R.string.delete));
        normalDialog.setContent("确定要删除该文件？");
        normalDialog.setPositiveButton(getString(R.string.btn_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPresenter.createSelectedRecords(notePageList, isSelectedList, null, pageMode);
                resetEditMode();
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

    /**
     * 点击右上角菜单的每一个item之后 进入编辑状态，同时弹出下方的归档或者删除框 点击进入相应的模式
     *
     * @param flag
     */
    @Override
    public void onMenuClicked(int flag) {
        isEditMode = true;
        ivRight.setVisibility(View.GONE);
        tvRight.setVisibility(View.VISIBLE);
        adapter.setIsSelectable(true);
        pwCreateRecord.showAtLocation(findViewById(R.id.rl_parent), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        if (flag == 1) {//归档模式
            tvCreate.setText(getString(R.string.create_current_record));
            pageMode = BluCommonUtils.EDIT_MODE;
        } else if (flag == 2) {//删除模式
            tvCreate.setText(getString(R.string.delete));
            pageMode = BluCommonUtils.DELETE_MODE;
        }
    }
}
