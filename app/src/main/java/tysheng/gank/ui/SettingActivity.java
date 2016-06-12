package tysheng.gank.ui;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import butterknife.BindView;
import tysheng.gank.Constant;
import tysheng.gank.R;
import tysheng.gank.base.BaseActivity;
import tysheng.gank.ui.fragment.MyPreferenceFragment;
import tysheng.gank.ui.inter.FragmentCallback;
import tysheng.gank.utils.SPHelper;
import tysheng.gank.utils.SnackbarUtil;
import tysheng.gank.widget.ACache;

/**
 * Created by shengtianyang on 16/5/3.
 */
public class SettingActivity extends BaseActivity implements FragmentCallback {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    SPHelper mSPHelper;
    TextInputEditText mEditText;
    private static final int RESULT_LOAD_IMAGE = 88;
    ACache mCache;
    @Override
    public void initData() {
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit();
            }
        });

        mSPHelper = new SPHelper(this);
        mCache = ACache.get(this);
        getFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, new MyPreferenceFragment())
                .commit();

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_setting;
    }
    private void jumpIntent() {
        mSPHelper.setSpBoolean(Constant.IS_SETTING, false);
        //重启方法 1
        Intent intentToBeNewRoot = new Intent(this, MainActivity.class);
        ComponentName cn = intentToBeNewRoot.getComponent();
        Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
        startActivity(mainIntent);
        //重启方法 2
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);
//        finish();
    }
    @Override
    public void onBackPressed() {
        exit();
    }

    private void exit() {
        if (!mSPHelper.getSpBoolean(Constant.IS_SETTING, false))
            finish();
        else
            jumpIntent();
    }
    @Override
    public void func1(String s) {
        switch (s) {
            case "cache":
//                Glide.get(this).clearDiskCache();
                Glide.get(this).clearMemory();
                SnackbarUtil.showSnackbar(mToolbar, getString(R.string.clear_cache));
                break;
            case "avatar":
                Intent i = new Intent(Intent.ACTION_PICK, null);
                i.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(i, RESULT_LOAD_IMAGE);
                break;
            case "name":
                getDialog().setTitle("输入你的姓名")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mSPHelper.setSpString(Constant.USER_NAME, mEditText.getText().toString());
                                mSPHelper.setSpBoolean(Constant.IS_SETTING, true);
                            }
                        })
                        .show();

                break;
            case "email":
                getDialog().setTitle("输入你的邮箱")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mSPHelper.setSpString(Constant.USER_EMAIL, mEditText.getText().toString());
                                mSPHelper.setSpBoolean(Constant.IS_SETTING, true);
                            }
                        })
                        .show();
                break;
            default:
                break;
        }
    }
    private AlertDialog.Builder getDialog(){
        LayoutInflater inflater = getLayoutInflater();
        View   dialog = inflater.inflate(R.layout.dialog_edittext,null);
        mEditText = (TextInputEditText) dialog.findViewById(R.id.editText);
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(dialog)
                .setNegativeButton("取消", null);
        return builder;
    }
    @Override
    protected void onActivityResult(final int requestCode, int resultCode, final Intent data) {
        if (resultCode == RESULT_OK && requestCode == RESULT_LOAD_IMAGE) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("是否更改头像")
                    .setNegativeButton("取消", null)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mSPHelper.setSpBoolean(Constant.IS_SETTING, true);
                            Glide.with(getApplicationContext())
                                    .loadFromMediaStore(data.getData())
                                    .asBitmap()
                                    .into(new SimpleTarget<Bitmap>(75, 75) {
                                        @Override
                                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                            mCache.put(Constant.AVATAR_BITMAP, ACache.Utils.Bitmap2Bytes(resource), ACache.TIME_DAY * 30);
                                        }
                                    });
                        }
                    })
                    .show();

        }
    }
}
