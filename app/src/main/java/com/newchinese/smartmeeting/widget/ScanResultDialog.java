package com.newchinese.smartmeeting.widget;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.newchinese.coolpensdk.manager.BluetoothLe;
import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.model.event.ConnectEvent;
import com.newchinese.smartmeeting.ui.meeting.adapter.BleListAdapter;
import com.newchinese.smartmeeting.util.BluCommonUtils;
import com.newchinese.smartmeeting.util.SharedPreUtils;

import org.greenrobot.eventbus.EventBus;


/**
 * Created by Administrator on 2017/7/20 0020.
 */

public class ScanResultDialog extends Dialog {
    private final Activity context;
    private ListView listView;
    private BleListAdapter bleListAdapter;
//    private static ScanResultDialog scanResultDialog = new ScanResultDialog()

    public ScanResultDialog(Activity context) {
        super(context, R.style.ScanResultDialog);
        this.context = context;
        listView = new ListView(context);
        if (bleListAdapter == null)
            bleListAdapter = new BleListAdapter(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initWindow();

        initListview();
    }

    private void initListview() {
        setContentView(listView);

        listView.setAdapter(bleListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice device = getItem(position);
                String address = device.getAddress();
                if (!address.equals(SharedPreUtils.getString(context, BluCommonUtils.SAVE_CONNECT_BLU_INFO_ADDRESS)) || !BluetoothLe.getDefault().getConnected()) {
                    EventBus.getDefault().post(new ConnectEvent(address, 0));
                }
                dismiss();
            }
        });
    }

    private void initWindow() {
//
        WindowManager windowManager = context.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = display.getWidth() * 4 / 5; // 设置dialog宽度为屏幕的4/5
        getWindow().setAttributes(lp);

        setTitle("搜索结果");
    }

    public void addDevice(BluetoothDevice bluetoothDevice) {
        bleListAdapter.addDevice(bluetoothDevice);
        bleListAdapter.notifyDataSetChanged();
    }

    public int getCount() {
        return bleListAdapter.getCount();
    }

    public BluetoothDevice getItem(int position) {
        return (BluetoothDevice) bleListAdapter.getItem(position);
    }

    public void unregister() {
//        EventBus.getDefault().unregister(context);
    }

    public String getActivePage(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        return am.getRunningTasks(1).get(0).topActivity.getClassName();
    }

    public void clear(){
        bleListAdapter.clear();
    }
}
