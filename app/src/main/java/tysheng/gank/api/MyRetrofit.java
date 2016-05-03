package tysheng.gank.api;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by shengtianyang on 16/3/19.
 */
public class MyRetrofit {

    private static Retrofit retrofit = null;
    private static GankApi sGankApi = null;
    public static final int TIME_MAX = 6;

    public static void init(Context context, String url) {

        Executor executor = Executors.newCachedThreadPool();
        final File baseDir = context.getCacheDir();
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


        Gson gson = new GsonBuilder().create();

        retrofit = new Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(url)
                .callbackExecutor(executor)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

    }


    public static GankApi getGankApi(Context context, String url) {
        if (sGankApi != null) return sGankApi;
        init(context, url);
        sGankApi = retrofit.create(GankApi.class);
        return getGankApi(context, url);
    }

}

