package tysheng.gank.base;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by shengtianyang on 16/2/22.
 */
public abstract class BaseFragment extends Fragment {
    protected View mRootView;
    protected Context mContext;
    protected CompositeSubscription mSubscription;
    protected Unbinder mUnbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(getLayoutId(), container, false);
        mUnbinder = ButterKnife.bind(this, mRootView);
        mSubscription = new CompositeSubscription();

        initData();
        return mRootView;
    }

    public void addSubscription(Subscription s) {
        if (this.mSubscription == null) {
            this.mSubscription = new CompositeSubscription();
        }

        this.mSubscription.add(s);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setTitle();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden)
            setTitle();
    }
    protected void addFragment(@Nullable FragmentManager manager, @NonNull Fragment from, @NonNull Fragment to, @IdRes int id, String tag, String backStackTag){
        doAddFragment(manager, from, to, id, tag, backStackTag);
    }
    protected void addFragment(@Nullable FragmentManager manager, @NonNull Fragment from, @NonNull Fragment to, @IdRes int id, String tag){
        doAddFragment(manager, from, to, id, tag,null);
    }

    private void doAddFragment(@Nullable FragmentManager manager, @NonNull Fragment from, @NonNull Fragment to, @IdRes int id, String tag, String backStackTag) {
        if (manager==null)
            manager = getActivity().getSupportFragmentManager();
        manager.beginTransaction()
                .hide(from)
                .addToBackStack(backStackTag)
                .add(id, to, tag)
                .commit();
    }

    protected abstract void setTitle();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
        if (this.mSubscription != null) {
            this.mSubscription.unsubscribe();
        }
    }
    protected abstract int getLayoutId();
    protected abstract void initData();

}
