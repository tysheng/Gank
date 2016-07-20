package tysheng.gank.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;
import tysheng.gank.Constant;
import tysheng.gank.R;
import tysheng.gank.adapter.GankViewPagerAdapter;
import tysheng.gank.base.BaseFragment;
import tysheng.gank.bean.GankViewPagerItem;
import tysheng.gank.ui.SortActivity;
import tysheng.gank.widget.ACache;
import tysheng.gank.widget.RecyclerTransformAnimation;

/**
 * Created by shengtianyang on 16/5/2.
 */
public class CategoryFragment extends BaseFragment {
    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindString(R.string.gank_category)
    String mString;
    private static final int REQUEST_CODE = 80;
    private GankViewPagerAdapter mAdapter;
    private GankViewPagerItem mItem;
    private ACache mCache;

    @Override
    protected void setTitle() {
        getActivity().setTitle(mString);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_category;
    }

    public static CategoryFragment newInstance() {
        return new CategoryFragment();
    }

    public CategoryFragment() {
    }

    @Override
    protected void initData() {
        getCache();

        mAdapter = new GankViewPagerAdapter(getFragmentManager());
        for (String str : mItem.mList) {
            mAdapter.addFragment(SingleCategoryFragment.newInstance(str), str);
        }
        mViewPager.setPageTransformer(true, new RecyclerTransformAnimation());
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
    }

    @OnClick(R.id.imageView)
    public void onClick() {
        startActivityForResult(new Intent(mContext, SortActivity.class), REQUEST_CODE);
    }

    private void getCache() {
        mCache = ACache.get(mContext);
        mItem = (GankViewPagerItem) mCache.getAsObject(Constant.CACHE_GANK_VIEWPAGER_ITEM);
        if (mItem == null)
            mItem = new GankViewPagerItem();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            onRefresh();
        }
    }

    private void onRefresh() {
        getCache();

        mAdapter.clear();
//        mTabLayout.removeAllTabs();
//        mViewPager.removeAllViews();
//        for (String str : mItem.mList) {
//            mAdapter.addFragment(SingleCategoryFragment.newInstance(str), str);
//        }
//        mViewPager.setAdapter(mAdapter);
//        mAdapter.notifyDataSetChanged();
//        mTabLayout.setupWithViewPager(mViewPager);
//        mViewPager.setCurrentItem(0);
        List<Fragment> list = new ArrayList<>();
        for (String str : mItem.mList) {
            list.add(SingleCategoryFragment.newInstance(str));
        }
        mAdapter.addFragments(list,mItem.mList);
        mViewPager.setCurrentItem(0);
    }
}
