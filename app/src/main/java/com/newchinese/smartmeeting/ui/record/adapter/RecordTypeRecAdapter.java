package com.newchinese.smartmeeting.ui.record.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.entity.bean.CollectRecord;
import com.newchinese.smartmeeting.entity.listener.OnItemClickedListener;
import com.newchinese.smartmeeting.util.DateUtils;
import com.newchinese.smartmeeting.util.log.XLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/11/21 0021.
 */

public class RecordTypeRecAdapter extends RecyclerView.Adapter<RecordTypeRecAdapter.MyViewHolder> {
    private final Context context;
    private OnItemClickedListener onItemClickedListener;
    private List<CollectRecord> collectRecordList = new ArrayList<>();
    private List<Boolean> isSelectedList = new ArrayList<>();
    private boolean isSelectable = false;

    public RecordTypeRecAdapter(Context context) {
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_record_type, null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final CollectRecord collectRecord = collectRecordList.get(position);
//        XLog.d("hahehe",collectRecord.getClassifyName()+" onBindViewHolder："+collectRecordList.size());
        holder.tvRecordTypTitle.setText(collectRecord.getCollectRecordName());
        holder.tvRecordTypTime.setText(DateUtils.formatLongDate2(collectRecord.getCollectDate()));
        if (isSelectable){
            holder.ivRecordTypeBackground.setVisibility(View.VISIBLE);
            holder.ivRecordTypSelect.setVisibility(View.GONE);
        }else {
            holder.ivRecordTypeBackground.setVisibility(View.GONE);
            holder.ivRecordTypSelect.setVisibility(View.GONE);
        }
        if (isSelectable && isSelectedList.size() != 0 && isSelectedList.size() == collectRecordList.size()) {
            boolean isSelected = isSelectedList.get(position); //是否被选中
            if (isSelected) {
                holder.ivRecordTypSelect.setVisibility(View.VISIBLE);
            } else {
                holder.ivRecordTypSelect.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        XLog.d("hahehe",collectRecordList+" getItemCount："+collectRecordList.size());
        return collectRecordList.size();
    }

    //设置监听
    public void setOnItemClickedListener(OnItemClickedListener onItemClickedListener) {
        this.onItemClickedListener = onItemClickedListener;
    }
//设置数据
    public void setCollectRecordList(List<CollectRecord> collectRecordList) {
        this.collectRecordList.clear();
        this.collectRecordList.addAll(collectRecordList);
        XLog.d("hahehe",this.collectRecordList+" setCollectRecordList："+collectRecordList.size());
        notifyDataSetChanged();
    }
//设置是否选定
    public void setIsSelectedList(List<Boolean> isSelectedList) {
        this.isSelectedList = isSelectedList;
        notifyDataSetChanged();
    }

    public List<Boolean> getIsSelectedList() {
        return isSelectedList;
    }

//设置是否进入编辑模式
    public void setIsSelectable(boolean isSelectable) {
        this.isSelectable = isSelectable;
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvRecordTypTitle;
        TextView tvRecordTypTime;
        ImageView ivRecordTypSelect;
        ImageView ivRecordTypeBackground;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvRecordTypTitle = (TextView) itemView.findViewById(R.id.tv_record_type_title);
            tvRecordTypTime = (TextView) itemView.findViewById(R.id.tv_record_type_time);
            ivRecordTypSelect = (ImageView) itemView.findViewById(R.id.iv_record_type_select);
            ivRecordTypeBackground = (ImageView) itemView.findViewById(R.id.iv_record_type_background);
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
