package tysheng.gank.ui;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import tysheng.gank.Constant;
import tysheng.gank.R;
import tysheng.gank.base.BaseActivity;
import tysheng.gank.ui.fragment.MyPreferenceFragment;
import tysheng.gank.ui.inter.FragmentCallback;
import tysheng.gank.utils.SPHelper;
import tysheng.gank.utils.SnackbarUtil;

/**
 * Created by shengtianyang on 16/5/3.
 */
public class SettingActivity extends BaseActivity implements FragmentCallback {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    SPHelper mSPHelper;
    TextInputEditText mEditText;
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
//                Intent i = new Intent(Intent.ACTION_PICK, null);
//                i.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//                startActivityForResult(i, RESULT_LOAD_IMAGE);
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
//                new MaterialDialog.Builder(this)
//                        .title("输入你的邮箱")
//                        .positiveText("Done")
//                        .negativeText("Cancel")
//                        .inputRange(1, 15)
//                        .input("", "", false, new MaterialDialog.InputCallback() {
//                            @Override
//                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
//                                mSPHelper.setSpString(Constant.USER_EMAIL, input.toString());
//                                mSPHelper.setSpBoolean(Constant.IS_SETTING, true);
//                            }
//                        }).show();
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
}
