package com.newchinese.smartmeeting.ui.meeting.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.newchinese.smartmeeting.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Description:   会议Fragment分类列表Recycler适配器
 * author         xulei
 * Date           2017/8/18
 */

public class MeetingClassifyRecyAdapter extends RecyclerView.Adapter<MeetingClassifyRecyAdapter.MyViewHolder> {
    private Context context;
    private List<String> classifyList;
    private LayoutInflater inflater;
    private onItemClickedListener onItemClickedListener;

    public MeetingClassifyRecyAdapter(Context context, List<String> classifyList) {
        this.context = context;
        this.classifyList = classifyList;
        inflater = LayoutInflater.from(context);
    }

    public void setOnItemClickedListener(MeetingClassifyRecyAdapter.onItemClickedListener onItemClickedListener) {
        this.onItemClickedListener = onItemClickedListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_meeting_classify, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tvClassifyName.setText(classifyList.get(position));
    }

    @Override
    public int getItemCount() {
        return classifyList.size();
    }

    /**
     * 添加条目
     */
    public void addItem(int position, String name) {
        classifyList.add(position, name);
        notifyItemInserted(position);//调用这个才有动画效果
    }

    /**
     * 移除条目
     */
    public void removeItem(int position) {
        classifyList.remove(position);
        notifyItemRemoved(position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_back_line)
        ImageView ivBackLine;
        @BindView(R.id.tv_classify_name)
        TextView tvClassifyName;

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

    /**
     * 点击回调的接口
     */
    public interface onItemClickedListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }
}
