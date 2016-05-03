package tysheng.gank.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by shengtianyang on 16/5/2.
 */
public class GankUtil {
    public static void share(Context context, String text, String title) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.putExtra(Intent.EXTRA_SUBJECT, title);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, title));
    }

//    public static void copyText(Context context, String text) {
//        ClipboardManager c = (ClipboardManager) context.getSystemService(context.CLIPBOARD_SERVICE);
//        c.setText(text);
//    }

    public static void openUrlByBrowser(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(intent);
    }
}
