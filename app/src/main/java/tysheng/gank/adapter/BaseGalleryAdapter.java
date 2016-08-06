package tysheng.gank.adapter;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by shengtianyang on 16/4/30.
 */
public abstract class BaseGalleryAdapter<T> extends PagerAdapter {
    protected List<T> mImages;
    protected Activity mActivity;

    public BaseGalleryAdapter(List<T> images, Activity activity) {
        mImages = images;
        mActivity = activity;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(mActivity).inflate(setLayoutId(), container, false);
        final PhotoViewAttacher mAttacher;
        final ImageView imageView = (ImageView) view.findViewById(setImageViewId());
        final ProgressBar progressBar = (ProgressBar) view.findViewById(setProgressBarId());
        mAttacher = new PhotoViewAttacher(imageView);
        mAttacher.setScaleType(ImageView.ScaleType.FIT_CENTER);
        mAttacher.setMinimumScale(1);
        initAttacher(mAttacher,position);
        Glide.with(mActivity)
                .load(setItemUrl(position))
                .into(new GlideDrawableImageViewTarget(imageView) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                        super.onResourceReady(resource, animation);
                        progressBar.setVisibility(View.GONE);
                        mAttacher.update();
                    }
                });
        mAttacher.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                mActivity.finish();
            }
        });
        container.addView(view);
        return view;
    }
    public abstract void initAttacher(PhotoViewAttacher attacher,int position);
    protected abstract String setItemUrl(int position);
    protected abstract int setProgressBarId();
    protected abstract int setLayoutId();

    protected abstract int setImageViewId();

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return setPageTitle(position);
    }

    protected abstract CharSequence setPageTitle(int position);

    @Override
    public int getCount() {
        return mImages.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

}
