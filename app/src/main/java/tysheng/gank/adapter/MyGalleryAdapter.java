package tysheng.gank.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;

import java.util.List;

import tysheng.gank.R;
import tysheng.gank.bean.GankResult;
import tysheng.gank.ui.GalleryActivity;
import tysheng.gank.utils.SystemUtil;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by shengtianyang on 16/4/30.
 */
public class MyGalleryAdapter extends BaseGalleryAdapter<GankResult> {


    public MyGalleryAdapter(List<GankResult> images, Activity activity) {
        super(images, activity);
    }

    @Override
    public void initAttacher(PhotoViewAttacher attacher, final int position) {
        attacher.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new AlertDialog.Builder(mActivity)
                        .setItems(new String[]{"保存", "分享"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                switch (i) {
                                    case 0:
                                        ((GalleryActivity) mActivity).saveImageToGallery(mImages.get(position).url);
                                        break;
                                    case 1:
                                        SystemUtil.share(mActivity,"来自Gank的图片",mImages.get(position).desc);
                                    default:
                                        break;
                                }
                            }
                        }).show();
                return true;
            }
        });
    }

    @Override
    protected String setItemUrl(int position) {
        return mImages.get(position).url;
    }

    @Override
    protected int setProgressBarId() {
        return R.id.progressBar;
    }

    @Override
    protected int setLayoutId() {
        return R.layout.item_gallery;
    }

    @Override
    protected int setImageViewId() {
        return R.id.imageView;
    }

    @Override
    protected CharSequence setPageTitle(int position) {
        return mImages.get(position).desc;
    }
}
