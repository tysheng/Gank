package tysheng.gank.utils;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;

/**
 * Created by shengtianyang on 16/5/2.
 */
public class StringUtil {
    public static SpannableString getGankStyleStr(String who, String desc) {
        String gankStr = desc + " @" + who;
        SpannableString spannableString = new SpannableString(gankStr);
        spannableString.setSpan(new RelativeSizeSpan(0.8f), desc.length() + 1, gankStr.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(Color.GRAY), desc.length() + 1, gankStr.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableString;
    }
}
