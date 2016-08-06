package tysheng.gank.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;
import tysheng.gank.R;
import tysheng.gank.adapter.EndlessRecyclerOnScrollListener;
import tysheng.gank.adapter.GankCategoryAdapter;
import tysheng.gank.api.MyRetrofit;
import tysheng.gank.base.BaseFragment;
import tysheng.gank.bean.GankCategory;
import tysheng.gank.bean.GankResult;
import tysheng.gank.ui.PictureActivity;
import tysheng.gank.ui.WebVideoActivity;
import tysheng.gank.ui.WebviewActivity;
import tysheng.gank.utils.SnackbarUtil;
import tysheng.gank.widget.ACache;
import tysheng.gank.widget.SectionsDecoration;

/**
 * Created by shengtianyang on 16/5/2.
 */
@SuppressLint("ValidFragment")
public class SingleCategoryFragment extends BaseFragment {
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    private GankCategoryAdapter mAdapter;
    private String typeName;
    private GankCategory mGankCategory;
    private List<GankResult> data;
    private LinearLayoutManager mLayoutManager;
    private ACache mCache;
    private int page = 1;
    private final String TAG = "tag";


    public SingleCategoryFragment(String typeName) {
        this.typeName = typeName;
    }

    public SingleCategoryFragment() {
    }

    @Override
    protected void setTitle() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && TextUtils.isEmpty(typeName)) {
            typeName = savedInstanceState.getString(TAG);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(TAG, typeName);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.swipe_recyclerview;
    }

    @Override
    protected void initData() {
        mCache = ACache.get(mContext);
        mGankCategory = (GankCategory) mCache.getAsObject(typeName);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);
        if (!TextUtils.equals(typeName, "福利"))
            mRecyclerView.addItemDecoration(new SectionsDecoration(true));
        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                getData(typeName, current_page);
            }
        });
        if (mGankCategory == null) {
            mGankCategory = new GankCategory(null);
            data = new ArrayList<>();
            getData(typeName, page = 1);
        } else {
            data.addAll(mGankCategory.results.subList(0, 9));
            mProgressBar.setVisibility(View.GONE);
        }
        mAdapter = new GankCategoryAdapter(mContext, data);
        mRecyclerView.setAdapter(mAdapter);

        setItemClick();
        initSwipe();
    }

    public static SingleCategoryFragment newInstance(String typeName) {
        return new SingleCategoryFragment(typeName);
    }

    private void initSwipe() {
        mSwipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_red_light,
                android.R.color.holo_blue_light);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                data.clear();
                getData(typeName, page = 1);
            }
        });
    }

    private void setItemClick() {
        mAdapter.setOnItemClickListener(new GankCategoryAdapter.OnItemClickListener() {
            @Override
            public void onClickListener(View view, int position) {
                if (data.get(position).type.equals("福利")) {
                    Intent intent = PictureActivity.newIntent(mContext, data.get(position).url,
                            data.get(position).desc);
                    startActivity(intent);
                } else if (data.get(position).type.equals("休息视频")) {
                    Intent intent = new Intent(getActivity(), WebVideoActivity.class);
                    intent.putExtra(WebVideoActivity.URL, data.get(position).url);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(mContext, WebviewActivity.class);
                    intent.putExtra(WebviewActivity.URL, data.get(position).url);
                    intent.putExtra(WebviewActivity.TITLE, data.get(position).desc);
                    startActivity(intent);
                }
            }
        });
    }

    private void getData(String category, final int page) {
        addSubscription(MyRetrofit.getGankApi()
                .getCategory(category, 10, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retry(1)
                .doOnTerminate(new Action0() {
                    @Override
                    public void call() {
                        stopSwipe();
                    }
                })
                .subscribe(new Subscriber<GankCategory>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        SnackbarUtil.showSnackbar(mSwipeRefreshLayout, getString(R.string.net_data_error));
                    }

                    @Override
                    public void onNext(GankCategory gankCategory) {
                        if (gankCategory.results.isEmpty()) {
                            SnackbarUtil.showSnackbar(mSwipeRefreshLayout, getString(R.string.no_more_data));
                            return;
                        }
                        if (!gankCategory.error) {
                            if (page == 1 && gankCategory.results.size() < 5) {
                                mGankCategory.results.addAll(gankCategory.results);
                                mCache.put(typeName, mGankCategory, 2 * ACache.TIME_DAY);
                            }
                            mProgressBar.setVisibility(View.GONE);
                            data.addAll(gankCategory.results);
                            mAdapter.notifyDataSetChanged();
                        } else {
                            onError(null);
                        }
                    }
                }));

    }

    private void stopSwipe() {
        if (mSwipeRefreshLayout.isRefreshing())
            mSwipeRefreshLayout.setRefreshing(false);
    }
}
