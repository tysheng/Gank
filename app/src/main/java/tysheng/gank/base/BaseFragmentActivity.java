package tysheng.gank.base;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by shengtianyang on 16/2/22.
 */
public abstract class BaseFragmentActivity extends AppCompatActivity {

    private CompositeSubscription mSubscription;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setTheme(SPHelper.getTheme(this));
        setContentView(getLayoutId());

        if (savedInstanceState != null) {
            restoreFragment(savedInstanceState);
        }

        ButterKnife.bind(this);
        initData();
    }

    protected abstract void restoreFragment(@NonNull Bundle savedInstanceState);

    protected void addSubscription(Subscription s) {
        if (this.mSubscription == null) {
            this.mSubscription = new CompositeSubscription();
        }

        this.mSubscription.add(s);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.mSubscription != null) {
            this.mSubscription.unsubscribe();
        }
    }

    /**
     * 初始化数据
     */
    public abstract void initData();

    /**
     * setContentView
     */
    public abstract int getLayoutId();

    /**
     * activity之间的跳转
     *
     * @param clazz    目标activity
     * @param isfinish 是否关闭
     */
    protected void jumpActivity(Class clazz, boolean isfinish) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
        if (isfinish) {
            this.finish();
        }
    }

    /**
     * Fragment之间的切换
     *
     * @param from 当前
     * @param to   目标
     * @param id
     * @param tag
     */
    protected void jumpFragment(Fragment from, Fragment to, int id, String tag) {
        FragmentManager manager = getSupportFragmentManager();
        if (to == null) {
            return;
        }
        FragmentTransaction transaction = manager.beginTransaction();
        if (from == null) {
            transaction.add(id, to, tag);
        } else {
            transaction.hide(from);
            if (to.isAdded()) {
                transaction.show(to);
            } else {
                transaction.add(id, to, tag);
            }
        }
        transaction
//                .setCustomAnimations(0, 0, R.anim.abc_fade_in, R.anim.abc_fade_out)
                .commit();

    }

}
