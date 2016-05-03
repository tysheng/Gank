package tysheng.gank.ui.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import butterknife.BindView;
import butterknife.OnClick;
import tysheng.gank.Constant;
import tysheng.gank.R;
import tysheng.gank.adapter.DragSortRecycler;
import tysheng.gank.adapter.GankSortAdapter;
import tysheng.gank.base.BaseFragment;
import tysheng.gank.bean.GankViewPagerItem;
import tysheng.gank.widget.ACache;

/**
 * Created by shengtianyang on 16/5/3.
 */
public class SortFragment extends BaseFragment {
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    GankSortAdapter mAdapter;
    private GankViewPagerItem mItem;
    ACache mCache;
    @Override
    protected void setTitle() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.recyclerview_fab;
    }
    @Override
    protected void initData() {
        mCache = ACache.get(mContext);
        mItem = (GankViewPagerItem) mCache.getAsObject(Constant.CACHE_GANK_VIEWPAGER_ITEM);
        if (mItem == null)
            mItem = new GankViewPagerItem();
        mAdapter = new GankSortAdapter(mContext, mItem);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setItemAnimator(null);
        DragSortRecycler dragSortRecycler = new DragSortRecycler();
        dragSortRecycler.setViewHandleId(R.id.tv);
        dragSortRecycler.setFloatingAlpha(0.4f);
        dragSortRecycler.setFloatingBgColor(Color.LTGRAY);
        mRecyclerView.addItemDecoration(dragSortRecycler);
        mRecyclerView.addOnItemTouchListener(dragSortRecycler);
        mRecyclerView.addOnScrollListener(dragSortRecycler.getScrollListener());
        dragSortRecycler.setOnItemMovedListener(new DragSortRecycler.OnItemMovedListener() {
            @Override
            public void onItemMoved(int from, int to) {
                String temp = mItem.mList.remove(from);
                mItem.mList.add(to, temp);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void saveData() {
        mCache.put(Constant.CACHE_GANK_VIEWPAGER_ITEM, mItem, ACache.TIME_DAY * 30);
    }

    @OnClick(R.id.floatingActionButton)
    public void onClick() {
        saveData();
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }
}
