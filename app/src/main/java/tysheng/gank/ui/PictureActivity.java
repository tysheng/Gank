package tysheng.gank.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;

import butterknife.BindView;
import tysheng.gank.R;
import tysheng.gank.base.BaseActivity;
import tysheng.gank.utils.GankUtil;
import tysheng.gank.utils.ImageUtil;
import tysheng.gank.utils.SnackbarUtil;
import uk.co.senab.photoview.PhotoViewAttacher;


/**
 * Created by shengtianyang on 16/3/27.
 */
public class PictureActivity extends BaseActivity  {
    @BindView(R.id.imageView)
    ImageView mImageView;
    public static final String EXTRA_IMAGE_URL = "image_url";
    public static final String EXTRA_IMAGE_TITLE = "image_title";

    private String mUrl;
    private String mTitle;
//    protected boolean mIsHidden = false;
    private PhotoViewAttacher mAttacher;
    private Bitmap mBitmap;

    @Override
    public void initData() {
        parseIntent();
        initPicture();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
    }

    private void initPicture() {
        mAttacher = new PhotoViewAttacher(mImageView);
        mAttacher.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Glide.with(this)
                .load(mUrl)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        mBitmap = resource;
                        mImageView.setImageBitmap(mBitmap);
                        mAttacher.update();
                    }
                });
        mAttacher.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(PictureActivity.this)
                        .setItems(new CharSequence[]{"保存", "分享"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        saveImageToGallery(mImageView);
                                        break;
                                    case 1:
                                        GankUtil.share(getApplicationContext(), mUrl, mTitle);
                                    default:
                                        break;
                                }
                            }
                        })
                .show();
                return true;
            }
        });
        mAttacher.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float v, float v1) {
                finish();
            }
        });
    }



//    private void hideOrShowToolbar() {
//        mToolbar.animate()
//                .translationY(mIsHidden ? 0 : -mToolbar.getHeight())
//                .setInterpolator(new DecelerateInterpolator(2))
//                .start();
//
//        mIsHidden = !mIsHidden;
//    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_picture;
    }

    public static Intent newIntent(Context context, String url, String desc) {
        Intent intent = new Intent(context, PictureActivity.class);
        intent.putExtra(PictureActivity.EXTRA_IMAGE_URL, url);
        intent.putExtra(PictureActivity.EXTRA_IMAGE_TITLE, desc);
        return intent;
    }

    private void parseIntent() {
        mUrl = getIntent().getStringExtra(EXTRA_IMAGE_URL);
        mTitle = getIntent().getStringExtra(EXTRA_IMAGE_TITLE);
    }

    private void saveImageToGallery(ImageView imageView){
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        if (drawable!=null){
            Bitmap bitmap = drawable.getBitmap();
            ImageUtil.saveImageToGallery(getApplicationContext(),bitmap,mTitle);
            File appDir = new File(Environment.getExternalStorageDirectory(), "Gank");
            String msg = String.format(getString(R.string.picture_has_save_to),
                    appDir.getAbsolutePath());
            SnackbarUtil.showSnackbar(mImageView,msg);
        }
    }

}
