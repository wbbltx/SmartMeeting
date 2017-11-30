package com.newchinese.smartmeeting.ui.record.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.base.BaseSimpleActivity;
import com.newchinese.smartmeeting.database.CollectRecordDao;
import com.newchinese.smartmeeting.entity.bean.CollectRecord;
import com.newchinese.smartmeeting.entity.bean.EventDecorator;
import com.newchinese.smartmeeting.util.BluCommonUtils;
import com.newchinese.smartmeeting.util.DateUtils;
import com.newchinese.smartmeeting.util.GreenDaoUtil;
import com.newchinese.smartmeeting.util.log.XLog;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class CalendarActivity extends BaseSimpleActivity implements OnDateSelectedListener, OnMonthChangedListener, TimePickerView.OnTimeSelectListener {

    private static final java.lang.String TAG = "CalendarActivity";
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.iv_time_pre)
    ImageView ivTimePre;
    @BindView(R.id.iv_time_next)
    ImageView ivTimeNext;
    @BindView(R.id.materialcalendarview)
    MaterialCalendarView calendatView;
    @BindView(R.id.tv_select_time)
    TextView tvDate;
    @BindView(R.id.iv_pen)
    ImageView ivPen;

    private CollectRecordDao collectRecordDao;
    private TimePickerView timePickerView;
    private List<Long> dateList;
    private List<Long> dateList1;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_calendar;
    }

    @Override
    protected void onViewCreated(Bundle savedInstanceState) {
        collectRecordDao = GreenDaoUtil.getInstance().getCollectRecordDao();
        tvDate.setText(DateUtils.formatLongDate6(System.currentTimeMillis()));
        tvTitle.setText("会议簿");
        ivPen.setImageResource(R.mipmap.category);
        ivBack.setVisibility(View.GONE);
        ivRight.setImageResource(R.mipmap.icon_back);
        dateList = new ArrayList<>();
        dateList1 = new ArrayList<>();
    }

    @Override
    protected void initStateAndData() {
        calendatView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);
        calendatView.setTopbarVisible(!calendatView.getTopbarVisible());
        calendatView.setSelectedDate(new Date());
        calendatView.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        calendatView.setSelectionColor(getResources().getColor(R.color.simple_blue));//设置选中背景颜色
        calendatView.setShowOtherDates(MaterialCalendarView.SHOW_OTHER_MONTHS);//设置显示不是当月的灰色日
        calendatView.setTileSizeDp(40);

        timePickerView = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH);
        timePickerView.setCyclic(false);
        timePickerView.setCancelable(false);

        queryAllDate();
    }

    @Override
    protected void initListener() {
        calendatView.setOnDateChangedListener(this);
        calendatView.setOnMonthChangedListener(this);
        timePickerView.setOnTimeSelectListener(this);
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        widget.setSelectionColor(getResources().getColor(R.color.simple_blue), date);
        widget.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);//选择一天
        dateList1.clear();
        for (Long aLong : dateList) {
            if (DateUtils.formatLongDate7(aLong).equals(date.getYear() + "-" + (date.getMonth()+1) + "-" + date.getDay())) {
                dateList1.add(aLong);
            }
        }

        Intent intent = new Intent(this, RecordsByDateActivity.class);
        intent.putExtra(BluCommonUtils.DATA, date.getCalendar().getTimeInMillis());
        intent.putExtra("alllist", (Serializable) dateList1);
        startActivity(intent);
    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
        tvDate.setText(date.getYear() + "/" + (date.getMonth() + 1));
    }

    @OnClick({R.id.tv_today, R.id.tv_select_time, R.id.iv_pen})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_today:
                Calendar instance = Calendar.getInstance();
                calendatView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);
                calendatView.setSelectedDate(instance);
//                calendatView.setCurrentDate(CalendarDay.from(instance.get(Calendar.YEAR), instance.get(Calendar.MONTH), instance.get(Calendar.DAY_OF_MONTH)), true);
                calendatView.setSelected(true);
                break;
            case R.id.tv_select_time:
                timePickerView.show();
                break;
            case R.id.iv_pen:
                finish();
                break;
        }
    }

    private void queryAllDate() {
        Observable.create(new ObservableOnSubscribe<List<CalendarDay>>() {
            @Override
            public void subscribe(@io.reactivex.annotations.NonNull ObservableEmitter<List<CalendarDay>> e) throws Exception {
                List<CollectRecord> list = collectRecordDao.queryBuilder().list();
                ArrayList<CalendarDay> dates = new ArrayList<>();
                for (CollectRecord collectRecord : list) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date(collectRecord.getCollectDate()));
                    CalendarDay day = CalendarDay.from(calendar);
                    dates.add(day);
                    dateList.add(collectRecord.getCollectDate());
                }
                e.onNext(dates);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<CalendarDay>>() {
                    @Override
                    public void accept(List<CalendarDay> calendarDays) throws Exception {
                        calendatView.addDecorator(new EventDecorator(Color.parseColor("#f47565"), calendarDays));
                    }
                });
    }

    @Override
    public void onTimeSelect(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
//        XLog.d("year-----", year + "");
//        XLog.d("month-----", month + "");
//        XLog.d("date-----", CalendarDay.from(year, month, 1) + "");
        calendatView.setCurrentDate(CalendarDay.from(year, month, 0), true);
        //   widget.setDateSelected(CalendarDay.from(year, month, 1), true);
        calendatView.setSelected(true);
        calendatView.setSelectedDate(CalendarDay.from(year, month, 0));
    }
}
