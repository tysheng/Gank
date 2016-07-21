package tysheng.gank.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import tysheng.gank.Constant;
import tysheng.gank.R;
import tysheng.gank.base.BaseFragmentActivity;
import tysheng.gank.ui.fragment.CategoryFragment;
import tysheng.gank.ui.fragment.DailyFragment;
import tysheng.gank.utils.FixUtil;
import tysheng.gank.utils.NightModeHelper;
import tysheng.gank.utils.SPHelper;
import tysheng.gank.utils.SnackbarUtil;
import tysheng.gank.utils.SystemUtil;
import tysheng.gank.utils.TimeUtil;
import tysheng.gank.widget.ACache;
import tysheng.gank.widget.GlideCircleTransform;

public class MainActivity extends BaseFragmentActivity implements NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view)
    NavigationView mNavigationView;
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout mCoordinatorLayout;

    GlideCircleTransform mGlideCircleTransform;
    private long mExitTime;
    private FragmentManager mManager;
    private DailyFragment mDailyFragment;
    private CategoryFragment mCategoryFragment;
    private Fragment currentFragment;
    //日夜模式切换
    private NightModeHelper mNightModeHelper;
    private static final String TAG = "tag";

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(TAG, currentFragment.getTag());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void initData() {
        initToolbar();

        initNav();

        if (!SystemUtil.isNetworkConnected(this))
            SnackbarUtil.showSnackbar(mCoordinatorLayout, getString(R.string.no_network_connected));

        mManager = getSupportFragmentManager();

        if (currentFragment == null)
            currentFragment = DailyFragment.newInstance();
        mManager.beginTransaction()
                .replace(R.id.frameLayout, currentFragment)
                .commit();
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }


    private void initNav() {
        mNightModeHelper = new NightModeHelper(this);
        mNavigationView.setNavigationItemSelectedListener(this);
        View headerView = mNavigationView.getHeaderView(0);

        ImageView imageView = (ImageView) headerView.findViewById(R.id.imageView);
        TextView name = (TextView) headerView.findViewById(R.id.tv_name);
        TextView email = (TextView) headerView.findViewById(R.id.tv_email);
        mNavigationView.getMenu().findItem(R.id.nav_daily).setIcon(TimeUtil.getWeekIcon());
        byte[] bitmap = ACache.get(this).getAsBinary(Constant.AVATAR_BITMAP);
        mGlideCircleTransform = new GlideCircleTransform(this);
        if (bitmap == null) {
            Glide.with(this)
                    .load(R.drawable.menu_myavatar)
                    .bitmapTransform(mGlideCircleTransform)
                    .into(imageView);
        } else {
            Glide.with(this)
                    .load(bitmap)
                    .bitmapTransform(mGlideCircleTransform)
                    .into(imageView);
        }

        SPHelper spHelper = new SPHelper(this);
        name.setText(spHelper.getSpString(Constant.USER_NAME, getString(R.string.user_name)));
        email.setText(spHelper.getSpString(Constant.USER_EMAIL, getString(R.string.user_email)));
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        mManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_daily:
                if (mDailyFragment == null)
                    mDailyFragment = DailyFragment.newInstance();
                if (!(currentFragment instanceof DailyFragment))
                    jumpFragment(currentFragment, mDailyFragment, R.id.frameLayout, DailyFragment.class.getName());
                currentFragment = mDailyFragment;
                break;
            case R.id.nav_setting:
                jumpActivity(SettingActivity.class, false);
                break;
            case R.id.nav_category:
                if (mCategoryFragment == null)
                    mCategoryFragment = CategoryFragment.newInstance();
                if (!(currentFragment instanceof CategoryFragment))
                    jumpFragment(currentFragment, mCategoryFragment, R.id.frameLayout, CategoryFragment.class.getName());
                currentFragment = mCategoryFragment;
                break;
            case R.id.nav_theme:
                mNightModeHelper.toggle();
                break;
            default:
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mManager.getBackStackEntryCount() == 0 && !mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            if (keyCode == KeyEvent.KEYCODE_BACK
                    && event.getAction() == KeyEvent.ACTION_DOWN) {
                if ((System.currentTimeMillis() - mExitTime) > 2000) {
                    SnackbarUtil.showSnackbar(mCoordinatorLayout, getString(R.string.exit));
                    mExitTime = System.currentTimeMillis();
                } else {
                    finish();
                }
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void restoreFragment(@NonNull Bundle savedInstanceState) {
        currentFragment = getSupportFragmentManager().findFragmentByTag(savedInstanceState.getString(TAG));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FixUtil.fixInputMethodManagerLeak(this);
    }
}
