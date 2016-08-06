package tysheng.gank.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import tysheng.gank.R;
import tysheng.gank.adapter.MyGalleryAdapter;
import tysheng.gank.base.BaseActivity;
import tysheng.gank.bean.GankCategory;
import tysheng.gank.bean.GankResult;
import tysheng.gank.utils.BitmapUtil;
import tysheng.gank.utils.SnackbarUtil;
import tysheng.gank.widget.ViewPagerFixed;


/**
 * Created by shengtianyang on 16/4/30.
 *
 */
public class GalleryActivity extends BaseActivity  {
    private static final String POSITION = "POSITION";
    @BindView(R.id.viewPager)
    ViewPagerFixed mViewPager;
    @BindView(R.id.indicator)
    TextView mIndicator;
    @BindView(R.id.desc)
    TextView mDesc;
    private List<GankResult> mList;
    private MyGalleryAdapter mAdapter;
    public static final String GALLERY_LIST = "GALLERY_LIST";
    private int mPosition;
    private int mCount;

    RelativeSizeSpan mSpan0 = new RelativeSizeSpan(1.6f);
    RelativeSizeSpan mSpan1 = new RelativeSizeSpan(1f);


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initData() {
        parseIntent();
        mAdapter = new MyGalleryAdapter(mList, this);
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mDesc.setText(mAdapter.getPageTitle(position));
                setIndicator(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setCurrentItem(mPosition);
        mDesc.setText(mAdapter.getPageTitle(mPosition));
        setIndicator(mPosition);

    }

    /**
     *
     * @param position position
     */
    private void setIndicator(int position) {
        SpannableString spannableString = new SpannableString(String.valueOf((1 + position)+ "/" +mCount));
        if (position!=9){
            spannableString.setSpan(mSpan0, 0, 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(mSpan1, 1, 3, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        }else {
            spannableString.setSpan(mSpan0, 0, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(mSpan1, 2, 4, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        mIndicator.setText(spannableString);
    }

    private void parseIntent() {
        GankCategory category = (GankCategory) getIntent().getSerializableExtra(GALLERY_LIST);
        mList = category.results;
        mCount = mList.size();
        mPosition = getIntent().getIntExtra(POSITION, 0);
    }

    public static Intent newIntent(Context context, Serializable list, int pos) {
        Intent intent = new Intent(context, GalleryActivity.class);
        intent.putExtra(GALLERY_LIST, list);
        intent.putExtra(POSITION, pos);
        return intent;
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_gallery;
    }


    public void saveImageToGallery(final String url) {
        BitmapUtil.saveImageToGallery(this, url, String.valueOf(url.hashCode()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        File appDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                        String msg = String.format(getString(R.string.picture_has_save_to),
                                appDir.getAbsolutePath());
                        SnackbarUtil.showSnackbar(mViewPager, msg);
                    }
                });

    }

}
