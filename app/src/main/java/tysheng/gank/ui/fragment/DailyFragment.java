package tysheng.gank.ui.fragment;

import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import tysheng.gank.Constant;
import tysheng.gank.MyApplication;
import tysheng.gank.R;
import tysheng.gank.adapter.DailyAdapter;
import tysheng.gank.api.GankApi;
import tysheng.gank.api.MyRetrofit;
import tysheng.gank.base.BaseFragment;
import tysheng.gank.bean.GankCategory;
import tysheng.gank.ui.DailyDetailActivity;
import tysheng.gank.ui.GalleryActivity;
import tysheng.gank.ui.PictureActivity;
import tysheng.gank.utils.SPHelper;
import tysheng.gank.utils.SnackbarUtil;
import tysheng.gank.widget.ACache;

/**
 * Created by shengtianyang on 16/5/2.
 */
public class DailyFragment extends BaseFragment {
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.floatingActionButton)
    FloatingActionButton mFloatingActionButton;
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindString(R.string.gank_daily)
    String mString;
    final String FULI = "福利";
    final int AMOUNT = 10;
    final int REFRESH = 0;
    final int LOAD = 1;
    private int page = 1;
    DailyAdapter mAdapter;
    GankCategory mGankCategory, gank10;
    ACache mCache;
    LinearLayoutManager mLayoutManager;
    private boolean is_gallery;

    @Override
    protected void setTitle() {
        getActivity().setTitle(mString);
    }

    public static DailyFragment newInstance() {
        return new DailyFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_daily;
    }

    @Override
    protected void initData() {
        SPHelper spHelper = new SPHelper(mContext);
        is_gallery = spHelper.getSpBoolean(Constant.IS_GALLERY, false);
        initSwipe();
        mCache = ACache.get(mContext);
        mGankCategory = (GankCategory) mCache.getAsObject(Constant.CACHE_DAILY);
        if (mGankCategory == null)
            mGankCategory = new GankCategory();
        gank10 = mGankCategory;
        View emptyView = getActivity().getLayoutInflater().inflate(R.layout.progressbar, (ViewGroup) mRecyclerView.getParent(), false);

        mAdapter = new DailyAdapter(mContext, mGankCategory.results);
        mAdapter.setEmptyView(emptyView);
        mAdapter.openLoadMore(AMOUNT, true);
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                getData(LOAD, ++page);
            }
        });
        mAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
        mAdapter.setOnRecyclerViewItemChildClickListener(new BaseQuickAdapter.OnRecyclerViewItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                switch (view.getId()) {
                    case R.id.imageView:
                        Intent intent;
                        if (is_gallery)
                            intent = GalleryActivity.newIntent(mContext, gank10, i % 10);
                        else
                            intent = PictureActivity.newIntent(mContext, mAdapter.getItem(i).url, mAdapter.getItem(i).desc);
                        startActivity(intent);
                        break;
                    case R.id.textView:
                        Intent intent1 = DailyDetailActivity.newIntent(mContext, mAdapter.getYMD(i), mAdapter.getUrl(i));
                        startActivity(intent1);
                        break;
                }
            }
        });
        initRecyclerView();
    }

    private void initRecyclerView() {
        mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                boolean isScrollingToBottom = dy > 0;
                if (mFloatingActionButton != null) {
                    if (isScrollingToBottom) {
                        if (mFloatingActionButton.isShown())
                            mFloatingActionButton.hide();
                    } else {
                        if (!mFloatingActionButton.isShown())
                            mFloatingActionButton.show();
                    }
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (!mFloatingActionButton.isShown())
                        mFloatingActionButton.show();
                }
            }
        });

    }

    private void initSwipe() {
        getData(REFRESH, page);
        mSwipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_red_light,
                android.R.color.holo_blue_light);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                getData(REFRESH, page);
            }
        });
    }

    @OnClick(R.id.floatingActionButton)
    public void onClick() {
        if (mLayoutManager.findLastCompletelyVisibleItemPosition() >= 30)
            mRecyclerView.scrollToPosition(0);
        else mRecyclerView.smoothScrollToPosition(0);
    }

    private void getData(final int type, int page) {
        addSubscription(MyRetrofit.getGankApi(MyApplication.getInstance(), GankApi.BASE_URL)
                .getCategory(FULI, AMOUNT, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GankCategory>() {
                    @Override
                    public void onCompleted() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onNext(GankCategory bean) {
                        if (!bean.error) {
                            if (type == REFRESH) {
                                //refresh
                                mAdapter.setNewData(bean.results);
                                mCache.put(Constant.CACHE_DAILY, bean, ACache.TIME_DAY * 2);
                            } else {
                                //load
                                mAdapter.notifyDataChangedAfterLoadMore(bean.results, true);
                                mAdapter.openLoadMore(true);
                            }
                            gank10 = bean;
                        } else
                            SnackbarUtil.showSnackbar(mCoordinatorLayout, getString(R.string.on_error));
                    }
                }));
    }
}
