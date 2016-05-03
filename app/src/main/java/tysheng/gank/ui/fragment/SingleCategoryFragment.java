package tysheng.gank.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import tysheng.gank.MyApplication;
import tysheng.gank.R;
import tysheng.gank.adapter.EndlessRecyclerOnScrollListener;
import tysheng.gank.adapter.GankCategoryAdapter;
import tysheng.gank.api.GankApi;
import tysheng.gank.api.MyRetrofit;
import tysheng.gank.base.BaseFragment;
import tysheng.gank.bean.GankCategory;
import tysheng.gank.bean.GankResult;
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

    GankCategoryAdapter mAdapter;
    private String typeName;
    GankCategory mGankCategory;
    private List<GankResult> data;

    public SingleCategoryFragment(String typeName) {
        this.typeName = typeName;
    }

    ACache mCache;
    int page = 1;

    @Override
    protected void setTitle() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.swipe_recyclerview;
    }

    @Override
    protected void initData() {
        initSwipe();
        mCache = ACache.get(mContext);
        mGankCategory = (GankCategory) mCache.getAsObject(typeName);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(layoutManager);
        if (!typeName.equals("福利"))
            mRecyclerView.addItemDecoration(new SectionsDecoration(true));
        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                getData(typeName, current_page);
            }
        });
        if (mGankCategory == null) {
            mGankCategory = new GankCategory();
            data = new ArrayList<>();

        } else {
            data.addAll(mGankCategory.results.subList(0, 9));
        }
        mAdapter = new GankCategoryAdapter(mContext, data);
        mRecyclerView.setAdapter(mAdapter);

        setItemClick();
    }

    public static SingleCategoryFragment newInstance(String typeName) {
        return new SingleCategoryFragment(typeName);
    }

    private void initSwipe() {
        mSwipeRefreshLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mSwipeRefreshLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mSwipeRefreshLayout.setRefreshing(true);
                getData(typeName, page = 1);
            }
        });
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
//                    Intent intent = PictureActivity.newIntent(frmContext, data.get(position).url,
//                            data.get(position).desc);
//                    startActivity(intent);
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
        mSubscription.add(MyRetrofit.getGankApi(MyApplication.getInstance(), GankApi.BASE_URL)
                .getCategory(category, 10, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GankCategory>() {
                    @Override
                    public void onCompleted() {
                        stopSwipe();
                    }

                    @Override
                    public void onError(Throwable e) {
                        stopSwipe();
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

                            data.addAll(gankCategory.results);
                            mAdapter.notifyDataSetChanged();
                        } else {
                            SnackbarUtil.showSnackbar(mSwipeRefreshLayout, getString(R.string.net_data_error));
                        }


                    }
                }));


    }

    private void stopSwipe() {
        if (mSwipeRefreshLayout.isRefreshing())
            mSwipeRefreshLayout.setRefreshing(false);
    }
}
