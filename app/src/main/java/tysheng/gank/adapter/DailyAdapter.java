package tysheng.gank.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import tysheng.gank.R;
import tysheng.gank.bean.GankResult;

/**
 * Created by shengtianyang on 16/5/2.
 */
public class DailyAdapter extends BaseQuickAdapter<GankResult> {


    public DailyAdapter(List<GankResult> data) {
        super(R.layout.item_gank_daily, data);
    }

    public String[] getYMD(int pos) {
        String str = getItem(pos).publishedAt.substring(0, 10);
        return str.split("-");
    }

    public String getUrl(int pos) {
        return getItem(pos).url;
    }

    @Override
    protected void convert(BaseViewHolder holder, GankResult gankResult) {
        holder.setText(R.id.textView, gankResult.formatPublish())
                .setOnClickListener(R.id.imageView, new OnItemChildClickListener())
                .setOnClickListener(R.id.textView, new OnItemChildClickListener());
        Glide.with(mContext).load(gankResult.url).crossFade().into((ImageView) holder.getView(R.id.imageView));
    }
}
