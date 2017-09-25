package com.newchinese.smartmeeting.widget;

import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.entity.listener.OnDeviceItemClickListener;
import com.newchinese.smartmeeting.ui.meeting.adapter.BleListAdapter;
import com.newchinese.smartmeeting.util.log.XLog;

import java.util.List;


/**
 * Created by Administrator on 2017/7/20 0020.
 */

public class ScanResultDialog extends Dialog {
    private final Context context;
    private ListView listView;
    private BleListAdapter bleListAdapter;
    private OnDeviceItemClickListener onDeviceItemClickListener;
    private final LayoutInflater localinflater;
    private TextView state;
    private TextView name;

    public ScanResultDialog(Context context) {
        super(context, R.style.ScanResultDialog);
        this.context = context;
        localinflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (bleListAdapter == null)
            bleListAdapter = new BleListAdapter(context);
        initListview();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        XLog.d("dialog", "dialog 的 oncreate");
//        initWindow();

    }

    private void initListview() {
        View inflate = localinflater.inflate(R.layout.layout_scan_result, null);
        setContentView(inflate);
        name = (TextView) inflate.findViewById(R.id.scan_result_curname);
        listView = (ListView) inflate.findViewById(R.id.scan_result_listview);
        state = (TextView) inflate.findViewById(R.id.scan_result_state);

        listView.setAdapter(bleListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice device = getItem(position);
                dismiss();
                if (onDeviceItemClickListener != null) {
                    onDeviceItemClickListener.onDeviceClick(device);
                }
            }
        });
    }

    public ScanResultDialog initWindow() {
//
//        WindowManager windowManager = context.getWindowManager();
//        Display display = windowManager.getDefaultDisplay();
//        WindowManager.LayoutParams lp = getWindow().getAttributes();
//        lp.width = display.getWidth() * 4 / 5; // 设置dialog宽度为屏幕的4/5
//        getWindow().setAttributes(lp);
        listView.setAdapter(bleListAdapter);
        return this;
//        setTitle("搜索结果");
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

    public ScanResultDialog setContent(String name, String state) {
        this.name.setText(name);
        this.state.setText(state.equals("1") ? ("连接成功") : ("连接失败"));
        return this;
    }
}
