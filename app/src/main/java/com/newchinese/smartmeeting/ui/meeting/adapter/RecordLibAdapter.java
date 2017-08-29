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
import com.newchinese.smartmeeting.model.bean.NotePage;
import com.newchinese.smartmeeting.model.listener.OnItemClickedListener;
import com.newchinese.smartmeeting.util.DataCacheUtil;
import com.newchinese.smartmeeting.util.DateUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Description:   录屏列表适配器
 */

public class RecordLibAdapter extends RecyclerView.Adapter<RecordLibAdapter.MyViewHolder> {
    private Context context;
    private List<String> notePageList = new ArrayList<>();
    private List<Boolean> isSelectedList = new ArrayList<>();
    private LayoutInflater inflater;
    private OnItemClickedListener onItemClickedListener;

    public RecordLibAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setNotePageList(List<String> notePageList) {
        this.notePageList = notePageList;
        notifyDataSetChanged();
    }

    public void setIsSelectedList(List<Boolean> isSelectedList) {
        this.isSelectedList = isSelectedList;
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
        View view = inflater.inflate(R.layout.item_record_lib, parent, false);
        return new MyViewHolder(view);
    }

    private String getTitle(String string){
        String[] split = string.split("/");
        if (split != null){
            return split[split.length-1];
        }
        return null;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.recordLibTitle.setText(getTitle(DataCacheUtil.getInstance().getRecordPathList().get(position)));
//        holder.tvDate.setText(DateUtils.formatLongDate1(notePageList.get(position).getDate()));
//        Glide.with(context)
//                .load(notePageList.get(position).getThumbnailPath())
//                .transition(new DrawableTransitionOptions().crossFade(1000)) //淡入淡出1s
//                .into(holder.ivThumnbail);
        holder.rlIsSelected.setVisibility(View.GONE);
        if (isSelectedList.size() != 0 && isSelectedList.size() == notePageList.size()) {
            boolean isSelected = isSelectedList.get(position); //是否被选中
            if (isSelected) {
                holder.rlIsSelected.setVisibility(View.VISIBLE);
            } else {
                holder.rlIsSelected.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return notePageList.size();
    }

    public String getItem(int position) {
        return notePageList.get(position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.record_lib_title)
        TextView recordLibTitle;
        @BindView(R.id.rl_is_selected)
        RelativeLayout rlIsSelected;

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
