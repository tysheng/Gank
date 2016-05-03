package tysheng.gank.ui;

import android.support.v7.widget.Toolbar;
import android.view.View;

import butterknife.BindView;
import tysheng.gank.R;
import tysheng.gank.base.BaseActivity;
import tysheng.gank.ui.fragment.SortFragment;

/**
 * Created by shengtianyang on 16/5/3.
 */
public class SortActivity extends BaseActivity {
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
        setTitle(getString(R.string.sort));

        jumpFragment(null, new SortFragment(), R.id.frameLayout, "");
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_setting;
    }
}
