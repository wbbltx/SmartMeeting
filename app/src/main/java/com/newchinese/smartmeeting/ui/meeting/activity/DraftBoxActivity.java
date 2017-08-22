package com.newchinese.smartmeeting.ui.meeting.activity;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.app.App;
import com.newchinese.smartmeeting.base.BaseActivity;
import com.newchinese.smartmeeting.contract.DraftBoxContract;
import com.newchinese.smartmeeting.listener.OnDeviceItemClickListener;
import com.newchinese.smartmeeting.listener.PopWindowListener;
import com.newchinese.smartmeeting.log.XLog;
import com.newchinese.smartmeeting.model.bean.NotePage;
import com.newchinese.smartmeeting.model.event.ConnectEvent;
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

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Description:
 * author         xulei
 * Date           2017/8/18 16:34
 */
public class DraftBoxActivity extends BaseActivity<DraftBoxPresenter, BluetoothDevice> implements
        DraftBoxContract.View<BluetoothDevice>, PopWindowListener, OnDeviceItemClickListener, OnItemClickedListener {
    @BindView(R.id.iv_back)
    ImageView ivBack; //返回
    @BindView(R.id.tv_title)
    TextView tvTitle; //标题
    @BindView(R.id.iv_pen)
    ImageView ivPen; //笔图标
    @BindView(R.id.rv_draft_page_list)
    RecyclerView rvDraftPageList;
    private String classifyName; //分类名
    private static boolean isFirstTime = true;

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
        initRecyclerView();
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        rvDraftPageList.setHasFixedSize(true);
        rvDraftPageList.setLayoutManager(new GridLayoutManager(this, 2));
        rvDraftPageList.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    protected DraftBoxPresenter initPresenter() {
        return new DraftBoxPresenter();
    }

    @Override
    protected void initStateAndData() {
        EventBus.getDefault().register(this);
        Intent intent = getIntent();
        classifyName = intent.getStringExtra("classify_name");

        tvTitle.setText(classifyName);
//        ivBack.setImageResource(0);
        ivPen.setBackgroundColor(Color.parseColor("#a6a6a6"));

        scanResultDialog = new ScanResultDialog(this);
        bluePopUpWindow = new BluePopUpWindow(this, this);

        //初始化Adapter
        adapter = new DraftPageRecyAdapter(this);
        rvDraftPageList.setAdapter(adapter);
    }


    @Override
    protected void initListener() {
        mPresenter.initListener();
        scanResultDialog.setOnDeviceItemClickListener(this);
        adapter.setOnItemClickedListener(this);
    }

    @OnClick({R.id.iv_back, R.id.iv_pen})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                break;
            case R.id.iv_pen:
//                EventBus.getDefault().post(new CheckBlueStateEvent());
                checkBle();
                break;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && isFirstTime) {
            XLog.d("haha", "onWindowFocusChanged被调用 " + hasFocus + isFirstTime);
            checkBle();
            isFirstTime = false;
        }
    }

    @Override
    public void onScanComplete() {
        onComplete();
    }

    private void checkBle() {
        boolean bluetoothOpen = mPresenter.isBluetoothOpen();
        if (!bluetoothOpen) {
            XLog.d("haha", "蓝牙没有打开");
            bluePopUpWindow.showAtLocation(root_view, Gravity.BOTTOM, 0, 0);
        } else {
            XLog.d("haha", "已经打开" + mPresenter.isConnected());
            if (!mPresenter.isConnected()) {
                mPresenter.scanBlueDevice();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void showResult(BluetoothDevice s) {
        scanResultDialog.addDevice(s);
    }

    @Subscribe
    public void onEvent(ConnectEvent type) {//重新连接
        if (mPresenter.isConnected()) {
            mPresenter.disConnect();
        }
        SharedPreUtils.setString(this, BluCommonUtils.SAVE_CONNECT_BLU_INFO_ADDRESS, type.getAddress());
        mPresenter.connectDevice(type.getAddress());
    }

    private void onComplete() {
        int count = scanResultDialog.getCount();
        if (count == 0) {//如果没有搜索到笔，提示
            CustomizedToast.showShort(App.getAppliction(), "请开启酷神笔！");
        } else if (count == 1) {   //如果只搜索到一支笔，将其地址信息保存，同时跳转 自动连接  要完善的是该笔是否处于可连接状态
            String address = scanResultDialog.getItem(0).getAddress();
            Log.i("controlltest", "扫描到一支笔 " + address);
            String key = SharedPreUtils.getString(App.getAppliction(), BluCommonUtils.SAVE_WRITE_PEN_KEY);
            if ("".equals(key)) {//如果key为空，则直接连接
                Log.i("controlltest", "没有key");
                EventBus.getDefault().post(new ConnectEvent(address, 0));
            } else {             //如果key不为空，则判断是否为上次连接的笔
                String preAddress = SharedPreUtils.getString(App.getAppliction(), BluCommonUtils.SAVE_CONNECT_BLU_INFO_ADDRESS);
                if (!preAddress.equals(address)) {//如果已经保存的address与扫描到的地址不相符，询问用户是否连接笔
                    Log.i("controlltest", "有key 地址与上次不同 " + address);
                    showDialog(address);
                } else {                           //如果保存的地址与本地扫描到的地址一致，直接连接 同时将信息保存到sp
                    SharedPreUtils.setString(App.getAppliction(), BluCommonUtils.SAVE_CONNECT_BLU_INFO_ADDRESS, address);
                    EventBus.getDefault().post(new ConnectEvent(address, 0));
                    Log.i("controlltest", "有key 地址与上次相同 " + address);
                }
            }
        } else {
            scanResultDialog.show();
        }
    }

    private void showDialog(final String address) {
        new AlertDialog.Builder(this)
                .setTitle("是否连接新笔" + address)
                .setPositiveButton("连接", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {//这里不能着急把信息写进sp，因为还不确定是不是连接成功 连接成功再写
//                        SharedPreUtils.setString(SearchBlueActivity.this, BluCommonUtils.SAVE_CONNECT_BLU_INFO_ADDRESS, address);
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
        checkBle();
    }

    @Override
    public void onCancel() {

    }

    /**
     * 设备列表的item点击事件
     *
     * @param add
     */
    @Override
    public void onDeviceClick(String add) {
        XLog.d("hahaha", "点击了" + add + ",停止扫描");
        if (!add.equals(SharedPreUtils.getString(this, BluCommonUtils.SAVE_CONNECT_BLU_INFO_ADDRESS)) || !mPresenter.isConnected()) {
            EventBus.getDefault().post(new ConnectEvent(add, 0));
        }
    }

    /**
     * 获取到数据库当前活动记录的所有页
     */
    @Override
    public void getActivePageList(final List<NotePage> pageList) {
        //更新当前记录表所有页缓存
        DataCacheUtil.getInstance().setActiveNotePageList(pageList);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e("test_greendao", "" + pageList.toString());
                if (pageList != null && pageList.size() > 0) {
                    adapter.setNotePageList(pageList);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //加载数据库当前活动记录的所有页
        mPresenter.loadActivePageList();
    }

    /**
     * 列表点击事件
     */
    @Override
    public void onClick(View view, int position) {
        NotePage selectNotePage = adapter.getItem(position);
        DataCacheUtil.getInstance().setActiveNotePage(selectNotePage); //更新活动页
        Intent intent = new Intent(this, DrawingBoardActivity.class);
        intent.putExtra(DrawingBoardActivity.TAG_PAGE_INDEX, selectNotePage.getPageIndex());
        startActivity(intent);
    }

    /**
     * 列表长点击事件
     */
    @Override
    public void onLongClick(View view, int position) {

    }
}
