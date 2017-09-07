package com.newchinese.smartmeeting.ui.meeting.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.entity.event.ColorEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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

    public HorizontalListViewAdapter(Context context, String[] titles, int[] ids, int[] colors) {
        EventBus.getDefault().register(this);
        this.mContext = context;
        this.mIconIDs = ids;
        this.mTitles = titles;
        this.colors = colors;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);//LayoutInflater.from(mContext);
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
            holder.mLL = (LinearLayout) convertView.findViewById(R.id.ll_root);
            lp = holder.mImage.getLayoutParams();
            lp.width = imageWidth;
            lp.height = imageWidth;
            holder.mImage.setLayoutParams(lp);

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

        if (position == 0) {
            holder.mImage.setImageResource(R.mipmap.blackcircle);
        } else if (position == 1) {
            holder.mImage.setImageResource(R.mipmap.redcircle);
        } else if (position == 2) {
            holder.mImage.setImageResource(R.mipmap.jusecircle);
        } else if (position == 3) {
            holder.mImage.setImageResource(R.mipmap.juhuangcircle);
        } else if (position == 4) {
            holder.mImage.setImageResource(R.mipmap.lvcircle);
        } else if (position == 5) {

            holder.mImage.setImageResource(R.mipmap.defaultcircle);
            GradientDrawable p = new GradientDrawable();
            p.setShape(GradientDrawable.OVAL);
            p.setColor(colors[5]);
            holder.mImage.setBackground(p);
        } else if (position == 6) {
            holder.mImage.setImageResource(R.mipmap.addcircle);
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
        private LinearLayout mLL;
    }
}
