package tysheng.gank.ui;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.View;

import butterknife.BindView;
import tysheng.gank.R;
import tysheng.gank.base.BaseActivity;
import tysheng.gank.ui.fragment.WebviewFragment;


/**
 * Created by shengtianyang on 16/4/3.
 */
public class WebviewActivity extends BaseActivity {
    public static final String URL = "Url";
    public static final String TITLE = "Title";
    String mUrl;
    String mTitle;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    public void initData() {
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        mUrl = intent.getStringExtra(WebviewActivity.URL);
        mTitle = intent.getStringExtra(WebviewActivity.TITLE);
        jumpFragment(null, WebviewFragment.newInstance(mUrl, mTitle), R.id.frameLayout, "");


    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_setting;
    }

}
