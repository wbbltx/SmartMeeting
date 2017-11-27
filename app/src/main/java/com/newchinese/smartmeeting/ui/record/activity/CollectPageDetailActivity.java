package com.newchinese.smartmeeting.ui.record.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.newchinese.smartmeeting.R;
import com.newchinese.smartmeeting.base.BaseSimpleActivity;
import com.newchinese.smartmeeting.entity.listener.OnShareListener;
import com.newchinese.smartmeeting.entity.listener.ShareCallBackListener;
import com.newchinese.smartmeeting.entity.bean.CollectPage;
import com.newchinese.smartmeeting.ui.record.adapter.CollectPageAdapter;
import com.newchinese.smartmeeting.util.DataCacheUtil;
import com.newchinese.smartmeeting.util.DateUtils;
import com.newchinese.smartmeeting.util.log.XLog;
import com.newchinese.smartmeeting.widget.SharePopWindow;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Description:   收藏页详情Activity
 * author         xulei
 * Date           2017/8/26 10:43
 */
public class CollectPageDetailActivity extends BaseSimpleActivity implements OnShareListener, PopupWindow.OnDismissListener {
    private static final java.lang.String TAG = "CollectPageDetailActivity";
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_share)
    ImageView ivShare;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.vp_thumnbail)
    ViewPager vpThumnbail;
    private int selectPosition = 0;
    private CollectPage currentPage;
    private List<CollectPage> collectPageList = new ArrayList<>(); //活动收藏记录表中当前所有收藏页
    private CollectPageAdapter adapter;
    private SharePopWindow sharePopWindow;
    private UMImage umImage;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_collect_page_detail;
    }

    @Override
    protected void onViewCreated(Bundle savedInstanceState) {
    }

    @Override
    protected void initStateAndData() {
        sharePopWindow = new SharePopWindow(this, this);
        sharePopWindow.setOnDismissListener(this);
        Intent intent = getIntent();
        selectPosition = intent.getIntExtra("selectPosition", selectPosition);
        collectPageList = DataCacheUtil.getInstance().getActiveCollectPageList();
        if (collectPageList.size() > 0 && selectPosition < (collectPageList.size())) {
            currentPage = collectPageList.get(selectPosition);
            setTitle(currentPage.getPageIndex(), currentPage.getDate());
            adapter = new CollectPageAdapter(this, collectPageList);
            vpThumnbail.setAdapter(adapter);
            vpThumnbail.setCurrentItem(selectPosition);

            String thumbnailPath = currentPage.getThumbnailPath();
            Bitmap bitmapFromPath = getBitmapFromPath(thumbnailPath);
            umImage = new UMImage(this, bitmapFromPath);
            UMImage thumb = new UMImage(this, bitmapFromPath);
            umImage.setThumb(thumb);
        }
    }

    public Bitmap getBitmapFromPath(String path) {

        if (!new File(path).exists()) {
            System.err.println("getBitmapFromPath: file not exists");
            return null;
        }
        byte[] buf = new byte[1024 * 1024];// 1M
        Bitmap bitmap = null;
        try {
            FileInputStream fis = new FileInputStream(path);
            int len = fis.read(buf, 0, buf.length);
            bitmap = BitmapFactory.decodeByteArray(buf, 0, len);
            if (bitmap == null) {
                System.out.println("len= " + len);
                System.err.println("path: " + path + "  could not be decode!!!");
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return bitmap;
    }

    @Override
    protected void initListener() {
        vpThumnbail.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setTitle(collectPageList.get(position).getPageIndex(), collectPageList.get(position).getDate());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 设置标题内容
     */
    public void setTitle(int pageIndex, long date) {
        tvTitle.setText(getString(R.string.page_number, pageIndex));
    }

    @OnClick({R.id.iv_back, R.id.iv_share})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back: //返回
                finish();
                break;
            case R.id.iv_share: //分享
                bgDark();
                sharePopWindow.showAtLocation(findViewById(R.id.rl_root), Gravity.BOTTOM, 0, 0);
                break;
        }
    }

    private void bgDark() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.7f;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(lp);
    }

    private void share(SHARE_MEDIA shareMedia) {
        new ShareAction(this)
                .setPlatform(shareMedia)
//                .withText("content")
// .withTargetUrl(linkHref)
                .withMedia(umImage)
                .setCallback(new ShareCallBackListener(this))
                .share();
    }

    @Override
    public void onShare(String i) {
        switch (i) {
            case "0"://qq空间分享
                share(SHARE_MEDIA.QZONE);
                break;
            case "1"://qq分享
                share(SHARE_MEDIA.QQ);
                break;
            case "2"://朋友圈
                share(SHARE_MEDIA.WEIXIN_CIRCLE);
                break;
            case "3"://微信
                share(SHARE_MEDIA.WEIXIN);
                break;
            case "4"://微博
                share(SHARE_MEDIA.SINA);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDismiss() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 1f;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(lp);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
    }
}
