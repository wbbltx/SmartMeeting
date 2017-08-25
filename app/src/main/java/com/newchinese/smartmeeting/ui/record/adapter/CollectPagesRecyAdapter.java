package com.newchinese.smartmeeting.ui.record.adapter;

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
import com.newchinese.smartmeeting.model.bean.CollectPage;
import com.newchinese.smartmeeting.model.bean.NotePage;
import com.newchinese.smartmeeting.model.listener.OnItemClickedListener;
import com.newchinese.smartmeeting.util.DateUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Description:   收藏页列表Recycler适配器
 * author         xulei
 * Date           2017/8/25
 */

public class CollectPagesRecyAdapter extends RecyclerView.Adapter<CollectPagesRecyAdapter.MyViewHolder> {
    private Context context;
    private List<CollectPage> collectPageList = new ArrayList<>();
    private LayoutInflater inflater;
    private OnItemClickedListener onItemClickedListener;

    public CollectPagesRecyAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setCollectPageList(List<CollectPage> collectPageList) {
        this.collectPageList = collectPageList;
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
        holder.tvIndex.setText(collectPageList.get(position).getPageIndex() + "");
        holder.tvDate.setText(DateUtils.formatLongDate1(collectPageList.get(position).getDate()));
        Glide.with(context)
                .load(collectPageList.get(position).getThumbnailPath())
                .transition(new DrawableTransitionOptions().crossFade(1000)) //淡入淡出1s
                .into(holder.ivThumnbail);
        holder.rlIsSelected.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return collectPageList.size();
    }

    public CollectPage getItem(int position) {
        return collectPageList.get(position);
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
