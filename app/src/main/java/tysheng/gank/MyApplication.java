package tysheng.gank;

import android.app.Application;

public class MyApplication extends Application {

    private static MyApplication instance;


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

//        Realm.setDefaultConfiguration(new RealmConfiguration.Builder(this).build());
    }

    public static MyApplication getInstance() {
        return instance;
    }



}
