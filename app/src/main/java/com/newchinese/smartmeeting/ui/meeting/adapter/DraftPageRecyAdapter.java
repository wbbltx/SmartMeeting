package com.newchinese.smartmeeting.ui.meeting.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.model.bean.NotePage;
import com.newchinese.smartmeeting.model.listener.OnItemClickedListener;
import com.newchinese.smartmeeting.util.DateUtils;

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
    private LayoutInflater inflater;
    private OnItemClickedListener onItemClickedListener;

    public DraftPageRecyAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setNotePageList(List<NotePage> notePageList) {
        this.notePageList = notePageList;
        notifyDataSetChanged();
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
        holder.tvDate.setText(DateUtils.formatLongDate(notePageList.get(position).getDate()));
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
