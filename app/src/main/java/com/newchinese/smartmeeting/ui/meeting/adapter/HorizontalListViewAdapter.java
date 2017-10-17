package com.newchinese.smartmeeting.ui.meeting.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.entity.event.ColorEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * author         xulei
 * Date           2017/8/22 20:42
 */
public class HorizontalListViewAdapter extends BaseAdapter {

    private int[] mIconIDs;
    private int[] colors;
    private String[] mTitles;
    private Context mContext;
    private LayoutInflater mInflater;
    private int selectIndex = -1;
    private ViewGroup.LayoutParams lp;
    private View viewById;
    private int selectedposition = -1;
    private List<Boolean> isSelectedList;

    public HorizontalListViewAdapter(Context context, String[] titles, int[] ids, int[] colors, List<Boolean> isSelectedList) {
        EventBus.getDefault().register(this);
        this.mContext = context;
        this.mIconIDs = ids;
        this.mTitles = titles;
        this.colors = colors;
        this.isSelectedList = isSelectedList;
        Log.e("test_select", "isSelectedList:" + isSelectedList.toString());
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);//LayoutInflater.from(mContext);
    }

    public void setIsSelectedList(List<Boolean> isSelectedList) {
        this.isSelectedList = isSelectedList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
//        return mIconIDs.length;
        return colors.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int imageWidth = parent.getMeasuredWidth() / getCount() - 27;
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.horizontal_list_item, null);
            holder.mImage = (ImageView) convertView.findViewById(R.id.img_list_item);
            holder.mRL = (RelativeLayout) convertView.findViewById(R.id.rl_root);
            holder.ivSelect = (ImageView) convertView.findViewById(R.id.iv_select);

            lp = holder.mImage.getLayoutParams();
            lp.width = imageWidth;
            lp.height = imageWidth;
            holder.mImage.setLayoutParams(lp);
            holder.ivSelect.setLayoutParams(lp);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (position == selectIndex) {
            convertView.setSelected(true);
        } else {
            convertView.setSelected(false);
        }
        if (position == selectedposition) {
            // holder.mLL.setBackgroundColor(Color.parseColor("#ff669900"));
        } else {
            // holder.mLL.setBackgroundColor(Color.parseColor("#ffffff"));
        }

//        holder.mTitle.setText(mTitles[position]);
//        iconBitmap = getPropThumnail(mIconIDs[position]);
//        holder.mImage.setImageBitmap(iconBitmap);
        holder.ivSelect.setVisibility(View.GONE);
        if (position == 0) {
            holder.mImage.setImageResource(R.mipmap.black_color);
            if (isSelectedList.get(0)){
                holder.ivSelect.setVisibility(View.VISIBLE);
            }else {
                holder.ivSelect.setVisibility(View.GONE);
            }
        } else if (position == 1) {
            holder.mImage.setImageResource(R.mipmap.red_color);
            if (isSelectedList.get(1)){
                holder.ivSelect.setVisibility(View.VISIBLE);
            }else {
                holder.ivSelect.setVisibility(View.GONE);
            }
        } else if (position == 2) {
            holder.mImage.setImageResource(R.mipmap.orange_color);
            if (isSelectedList.get(2)){
                holder.ivSelect.setVisibility(View.VISIBLE);
            }else {
                holder.ivSelect.setVisibility(View.GONE);
            }
        } else if (position == 3) {
            holder.mImage.setImageResource(R.mipmap.yellow_color);
            if (isSelectedList.get(3)){
                holder.ivSelect.setVisibility(View.VISIBLE);
            }else {
                holder.ivSelect.setVisibility(View.GONE);
            }
        } else if (position == 4) {
            holder.mImage.setImageResource(R.mipmap.green_color);
            if (isSelectedList.get(4)){
                holder.ivSelect.setVisibility(View.VISIBLE);
            }else {
                holder.ivSelect.setVisibility(View.GONE);
            }
        } else if (position == 5) {
            holder.mImage.setImageResource(R.mipmap.defaultcircle);
            GradientDrawable p = new GradientDrawable();
            p.setShape(GradientDrawable.OVAL);
            p.setColor(colors[5]);
            holder.mImage.setBackground(p);
            if (isSelectedList.get(5)){
                holder.ivSelect.setVisibility(View.VISIBLE);
            }else {
                holder.ivSelect.setVisibility(View.GONE);
            }
        } else if (position == 6) {
            holder.mImage.setImageResource(R.mipmap.add_color);
        }
        return convertView;
    }

    public void setSelectIndex(int i) {
        selectIndex = i;
    }

    @Subscribe
    public void onEvent(ColorEvent event) {
        selectedposition = event.getSrc();
    }

    private static class ViewHolder {
        private TextView mTitle;
        private ImageView mImage;
        private RelativeLayout mRL;
        private ImageView ivSelect;
    }
}
