package tysheng.gank.utils;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Created by shengtianyang on 16/7/21.
 */
public class RxBus {
    private static volatile RxBus instance;
    private final Subject<Object,Object> mBus;
//    private final Subject<Object,Object> mStickyBus;

    public RxBus() {
        mBus = new SerializedSubject<>(PublishSubject.create());
//        mStickyBus = new SerializedSubject<>(BehaviorSubject.create());
    }

    public static RxBus getDefault() {
        if (instance == null) {
            synchronized (RxBus.class) {
                if (instance == null) {
                    instance = new RxBus();
                }
            }
        }
        return instance;
    }

    public void post(Object o) {
        mBus.onNext(o);
    }
    public <T> Observable<T> getObservable(Class<T> c) {
        return mBus.ofType(c);
    }


//    public void postSticky(Object o) {
//        mStickyBus.onNext(o);
//    }
//
//
//    public <T> Observable<T> getStickyObservable(Class<T> c) {
//        return mStickyBus.ofType(c);
//    }
}
