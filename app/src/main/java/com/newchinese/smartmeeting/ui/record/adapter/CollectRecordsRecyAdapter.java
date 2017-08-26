package com.newchinese.smartmeeting.ui.record.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.model.bean.CollectRecord;
import com.newchinese.smartmeeting.model.listener.OnItemClickedListener;
import com.newchinese.smartmeeting.ui.record.activity.CollectRecordFilterActivity;
import com.newchinese.smartmeeting.util.DateUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Description:   收藏记录列表Adapter
 * author         xulei
 * Date           2017/8/25
 */

public class CollectRecordsRecyAdapter extends RecyclerView.Adapter<CollectRecordsRecyAdapter.MyViewHolder> {
    private Context context;
    private String whichEntry; //records,filter
    private List<CollectRecord> collectRecordList = new ArrayList<>();
    private LayoutInflater inflater;
    private OnItemClickedListener onItemClickedListener;

    public CollectRecordsRecyAdapter(Context context, String whichEntry) {
        this.context = context;
        this.whichEntry = whichEntry;
        inflater = LayoutInflater.from(context);
    }

    public void setCollectRecordList(List<CollectRecord> collectRecordList) {
        this.collectRecordList = collectRecordList;
        notifyDataSetChanged();
    }

    public void setOnItemClickedListener(OnItemClickedListener onItemClickedListener) {
        this.onItemClickedListener = onItemClickedListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_collect_records, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final CollectRecord collectRecord = collectRecordList.get(position);
        holder.tvRecordName.setText(collectRecord.getCollectRecordName());
        int picResource = 0;
        switch (collectRecord.getClassifyName()) {
            case "工作例会":
                picResource = R.mipmap.record_work;
                break;
            case "项目会议":
                picResource = R.mipmap.record_project;
                break;
            case "学习培训":
                picResource = R.mipmap.record_study;
                break;
            case "研讨会":
                picResource = R.mipmap.record_explore;
                break;
            case "评审会":
                picResource = R.mipmap.record_review;
                break;
            case "工作汇报":
                picResource = R.mipmap.record_report;
                break;
            case "其他":
                picResource = R.mipmap.record_other;
                break;
        }
        holder.ivClassify.setImageResource(picResource);
        holder.tvDate.setText(DateUtils.formatLongDate2(collectRecord.getCollectDate()));
        if ("records".equals(whichEntry)) {
            holder.ivClassify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CollectRecordFilterActivity.class);
                    intent.putExtra("classifyName", collectRecord.getClassifyName());
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return collectRecordList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_classify)
        ImageView ivClassify;
        @BindView(R.id.tv_record_name)
        TextView tvRecordName;
        @BindView(R.id.tv_date)
        TextView tvDate;

        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickedListener != null)
                        onItemClickedListener.onClick(view, getAdapterPosition());//可立刻获取到当前position
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (onItemClickedListener != null)
                        onItemClickedListener.onLongClick(view, getAdapterPosition());
                    return false;
                }
            });

        }
    }
}
