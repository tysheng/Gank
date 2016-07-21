package tysheng.gank.api;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import tysheng.gank.MyApplication;

/**
 * Created by shengtianyang on 16/3/19.
 */
public class MyRetrofit {

    private static Retrofit retrofit = null;
    private static volatile GankApi sGankApi = null;
    private static final int TIME_MAX = 6;

    public static void init() {
        final File baseDir = MyApplication.getInstance().getCacheDir();
        Cache cache = null;
        if (baseDir != null) {
            final File cacheDir = new File(baseDir, "HttpResponseCache");
            cache = new Cache(cacheDir, 10 * 1024 * 1024);
        }
        //设置缓存 10M
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.readTimeout(TIME_MAX, TimeUnit.SECONDS);
        builder.connectTimeout(TIME_MAX, TimeUnit.SECONDS);
        builder.writeTimeout(TIME_MAX, TimeUnit.SECONDS);
        OkHttpClient client = builder.cache(cache).build();


        retrofit = new Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(GankApi.BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        sGankApi = retrofit.create(GankApi.class);
    }


    public static GankApi getGankApi() {
        if (sGankApi == null) {
            synchronized (MyRetrofit.class){
                if (sGankApi==null)
                    init();
            }
        }
        return sGankApi;
    }

}

