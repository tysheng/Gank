package tysheng.gank.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import tysheng.gank.R;
import tysheng.gank.adapter.GankDailyDetailAdapter;
import tysheng.gank.api.MyRetrofit;
import tysheng.gank.base.BaseActivity;
import tysheng.gank.bean.GankDaily;
import tysheng.gank.utils.SnackbarUtil;
import tysheng.gank.widget.SectionsDecoration;


/**
 * Created by shengtianyang on 16/4/7.
 */
public class DailyDetailActivity extends BaseActivity {
    @BindView(R.id.iv_head)
    ImageView ivHead;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_gank)
    RecyclerView recyclerView;
    public static final String DATE = "date";
    public static final String URL = "mUrl";
    GankDailyDetailAdapter mAdapter;
    String[] date;
    String url;
    GankDaily mDaily;

    @Override
    public void initData() {
        date = getIntent().getStringArrayExtra(DATE);
        url = getIntent().getStringExtra(URL);
        Glide.with(this)
                .load(url)
                .centerCrop()
                .into(ivHead);
        toolbar.setTitle(date[1] + "/" + date[2]);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mDaily = new GankDaily();
        mAdapter = new GankDailyDetailAdapter(this, mDaily.results.androidList);
        mAdapter.setOnItemClickListener(new GankDailyDetailAdapter.OnItemClickListener() {
            @Override
            public void onClickListener(View view, int position) {
                Intent intent = new Intent(DailyDetailActivity.this, WebviewActivity.class);
                intent.putExtra(WebviewActivity.URL, mAdapter.getDataItem(position).url);
                intent.putExtra(WebviewActivity.TITLE, mAdapter.getDataItem(position).desc);
                startActivity(intent);
            }
        });
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new SectionsDecoration(true));
        getData(date);
    }

    private void getData(String[] date) {
        MyRetrofit.getGankApi()
                .getDaily(date[0], date[1], date[2])
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GankDaily>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        SnackbarUtil.showSnackbar(toolbar,getString(R.string.net_data_error));
                    }

                    @Override
                    public void onNext(GankDaily gankDaily) {
                        mAdapter.add(gankDaily);

                    }
                });

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_gank_daily_detail;
    }

    public static Intent newIntent(Context context, String[] date, String url) {
        Intent intent = new Intent(context, DailyDetailActivity.class);
        intent.putExtra(DATE, date);
        intent.putExtra(URL, url);
        return intent;
    }
    @OnClick(R.id.fab)
    public void onClick() {
        if (mAdapter.getDataItem(0)!=null&&mAdapter.getDataItem(0).type.equals("休息视频")){
            Intent intent = new Intent(DailyDetailActivity.this, WebVideoActivity.class);
            intent.putExtra(WebVideoActivity.URL, mAdapter.getDataItem(0).url);
            startActivity(intent);
        }else {
            SnackbarUtil.showSnackbar(toolbar,getString(R.string.video_error));
        }
    }
}
