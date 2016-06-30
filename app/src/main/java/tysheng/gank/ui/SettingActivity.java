package tysheng.gank.ui;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
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

import butterknife.BindString;
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
    private static final int CAMERA_WITH_DATA = 77;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    SPHelper mSPHelper;
    TextInputEditText mEditText;
    private static final int RESULT_LOAD_IMAGE = 88;
    ACache mCache;
    @BindString(R.string.button_ok)
    String ok;
    @BindString(R.string.button_cancel)
    String cancel;

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
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.check_avatar))
                        .setItems(new String[]{"拍照", "相册"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        takePhoto();
                                        break;
                                    case 1:
                                        choosePhoto();
                                        break;
                                    default:
                                        break;
                                }
                            }
                        })
                        .show();
                break;
            case "name":
                getDialog().setTitle(getString(R.string.check_name))
                        .setPositiveButton(ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mSPHelper.setSpString(Constant.USER_NAME, mEditText.getText().toString());
                                mSPHelper.setSpBoolean(Constant.IS_SETTING, true);
                            }
                        })
                        .show();
                break;
            case "email":
                getDialog().setTitle(getString(R.string.check_email))
                        .setPositiveButton(ok, new DialogInterface.OnClickListener() {
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

    private void choosePhoto() {
        Intent i = new Intent(Intent.ACTION_PICK, null);
        i.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    private void takePhoto() {
        try {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_WITH_DATA);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    private AlertDialog.Builder getDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialog = inflater.inflate(R.layout.dialog_edittext, null);
        mEditText = (TextInputEditText) dialog.findViewById(R.id.editText);
        return new AlertDialog.Builder(this).setView(dialog).setNegativeButton(cancel, null);
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, final Intent data) {
        if (resultCode == RESULT_OK) {
            mSPHelper.setSpBoolean(Constant.IS_SETTING, true);
            switch (requestCode) {
                case RESULT_LOAD_IMAGE:
                    Glide.with(getApplicationContext())
                            .loadFromMediaStore(data.getData())
                            .asBitmap()
                            .into(new SimpleTarget<Bitmap>(75, 75) {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    mCache.put(Constant.AVATAR_BITMAP, ACache.Utils.Bitmap2Bytes(resource), ACache.TIME_DAY * 120);
                                }
                            });
                    break;
                case CAMERA_WITH_DATA:
                    Bundle bundle = data.getExtras();
                    Bitmap bitMap = (Bitmap) bundle.get("data");
                    if (bitMap != null)
                        bitMap.recycle();
                    bitMap = (Bitmap) data.getExtras().get("data");
                    int width = bitMap.getWidth();
                    int height = bitMap.getHeight();

                    // 设置想要的大小
                    int newWidth = 75;
                    int newHeight = 75;
                    // 计算缩放比例
                    float scaleWidth = ((float) newWidth) / width;
                    float scaleHeight = ((float) newHeight) / height;
                    // 取得想要缩放的matrix参数
                    Matrix matrix = new Matrix();
                    matrix.postScale(scaleWidth, scaleHeight);
                    // 得到新的图片
                    bitMap = Bitmap.createBitmap(bitMap, 0, 0, width, height, matrix,
                            true);
                    mCache.put(Constant.AVATAR_BITMAP, bitMap, ACache.TIME_DAY * 120);
                    break;
                default:
                    break;
            }
        }
    }
}
