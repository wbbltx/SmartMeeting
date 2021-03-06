package com.newchinese.smartmeeting.ui.meeting.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.entity.bean.NotePage;
import com.newchinese.smartmeeting.entity.listener.OnItemClickedListener;
import com.newchinese.smartmeeting.util.BluCommonUtils;
import com.newchinese.smartmeeting.util.DataCacheUtil;
import com.newchinese.smartmeeting.util.DateUtils;
import com.newchinese.smartmeeting.util.SharedPreUtils;
import com.newchinese.smartmeeting.util.log.XLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Description:   草稿箱页列表Recycler适配器
 * author         xulei
 * Date           2017/8/18
 */

public class DraftPageRecyAdapter extends RecyclerView.Adapter<DraftPageRecyAdapter.MyViewHolder> {
    private Context context;
    private List<NotePage> notePageList = new ArrayList<>();
    private List<Boolean> isSelectedList = new ArrayList<>();
    private LayoutInflater inflater;
    private OnItemClickedListener onItemClickedListener;
    private boolean isSelectable = false;

    public DraftPageRecyAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setNotePageList(List<NotePage> notePageList) {
        this.notePageList = notePageList;
        notifyDataSetChanged();
    }

    public void setIsSelectedList(List<Boolean> isSelectedList) {
        this.isSelectedList = isSelectedList;
        notifyDataSetChanged();
    }

    public void setIsSelectable(boolean isSelectable) {
        this.isSelectable = isSelectable;
        notifyDataSetChanged();
    }


    public List<Boolean> getIsSelectedList() {
        return isSelectedList;
    }

    public void setOnItemClickedListener(OnItemClickedListener onItemClickedListener) {
        this.onItemClickedListener = onItemClickedListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_draft_pages, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tvIndex.setText(notePageList.get(position).getPageIndex() + "");
        holder.tvDate.setText(DateUtils.formatLongDate1(notePageList.get(position).getDate()));
        if (notePageList.get(position).getScreenPathList() != null && notePageList.get(position).getScreenPathList().size() != 0) {
            holder.record_flag.setVisibility(View.VISIBLE);
        } else {
            holder.record_flag.setVisibility(View.GONE);
        }
        Glide.with(context)
                .load(notePageList.get(position).getThumbnailPath())
                .transition(new DrawableTransitionOptions().crossFade(1000)) //淡入淡出1s
                .into(holder.ivThumnbail);
        if (isSelectable) {  //是否处于可选择状态
            holder.rlIsSelected.setVisibility(View.VISIBLE);
            holder.iv_tick.setVisibility(View.GONE);
        }else {
            holder.rlIsSelected.setVisibility(View.GONE);
            holder.iv_tick.setVisibility(View.GONE);
        }
        if (isSelectable && isSelectedList.size() != 0 && isSelectedList.size() == notePageList.size()) {
            boolean isSelected = isSelectedList.get(position); //是否被选中
            if (isSelected) {
                holder.iv_tick.setVisibility(View.VISIBLE);
            } else {
                holder.iv_tick.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return notePageList.size();
    }

    public NotePage getItem(int position) {
        return notePageList.get(position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_thumnbail)
        ImageView ivThumnbail;
        @BindView(R.id.tv_index)
        TextView tvIndex;
        @BindView(R.id.tv_date)
        TextView tvDate;
        @BindView(R.id.rl_is_selected)
        RelativeLayout rlIsSelected;
        @BindView(R.id.record_flag)
        ImageView record_flag;
        @BindView(R.id.iv_tick)
        ImageView iv_tick;

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
