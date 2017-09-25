package com.newchinese.smartmeeting.widget;

import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.entity.listener.OnDeviceItemClickListener;
import com.newchinese.smartmeeting.ui.meeting.adapter.BleListAdapter;

import java.util.List;


/**
 * Created by Administrator on 2017/7/20 0020.
 */

public class ScanResultDialog extends Dialog {
    private final Context context;
    private ListView listView;
    private BleListAdapter bleListAdapter;
    private OnDeviceItemClickListener onDeviceItemClickListener;

    public ScanResultDialog(Context context) {
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
                dismiss();
                if (onDeviceItemClickListener != null){
                    onDeviceItemClickListener.onDeviceClick(device);
                }
            }
        });
    }

    private void initWindow() {
//
//        WindowManager windowManager = context.getWindowManager();
//        Display display = windowManager.getDefaultDisplay();
//        WindowManager.LayoutParams lp = getWindow().getAttributes();
//        lp.width = display.getWidth() * 4 / 5; // 设置dialog宽度为屏幕的4/5
//        getWindow().setAttributes(lp);

        setTitle(context.getString(R.string.search_result));
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

    public List<BluetoothDevice> getDevices() {
        return bleListAdapter.getDevices();
    }

    public void clear() {
        bleListAdapter.clear();
    }

    public void setOnDeviceItemClickListener(OnDeviceItemClickListener onDeviceItemClickListener) {
        this.onDeviceItemClickListener = onDeviceItemClickListener;
    }
}
