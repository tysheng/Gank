package tysheng.gank.utils;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by shengtianyang on 16/5/2.
 */
public class SnackbarUtil {
    public static void showSnackbar(View view, String msg) {
        Snackbar snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_SHORT);
        ViewGroup viewGroup = (ViewGroup) snackbar.getView();
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View v = viewGroup.getChildAt(i);
            if (v instanceof TextView)
                ((TextView) v).setTextColor(Color.WHITE);
        }
        snackbar.show();
    }
}
