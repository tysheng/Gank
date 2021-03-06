package tysheng.gank.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tysheng.gank.R;
import tysheng.gank.bean.GankDaily;
import tysheng.gank.bean.GankResult;
import tysheng.gank.utils.StringUtil;
import tysheng.gank.widget.SectionsDecoration;


/**
 * Created by shengtianyang on 16/1/28.
 */
public class GankDailyDetailAdapter
        extends RecyclerView.Adapter<GankDailyDetailAdapter.MyViewHolder>
        implements SectionsDecoration.Adapter<GankDailyDetailAdapter.SectionHeaderView> {


    private List<GankResult> data;
    private OnItemClickListener onItemClickListener;
    private Context context;


    public GankDailyDetailAdapter(Context context, List<GankResult> data) {
        this.data = data;
        this.context = context;
    }

    public void add(GankDaily daily) {
        if (daily.results.androidList != null) data.addAll(daily.results.androidList);
        if (daily.results.iOSList != null) data.addAll(daily.results.iOSList);
        if (daily.results.前端List != null) data.addAll(daily.results.前端List);
        if (daily.results.appList != null) data.addAll(daily.results.appList);
        if (daily.results.拓展资源List != null) data.addAll(daily.results.拓展资源List);
        if (daily.results.瞎推荐List != null) data.addAll(daily.results.瞎推荐List);
        if (daily.results.休息视频List != null) data.addAll(0, daily.results.休息视频List);
        notifyDataSetChanged();
    }

    public GankResult getDataItem(int pos) {
        return data.get(pos);
    }

    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }

    @Override
    public long getHeaderId(int position) {
        if (data.get(position) != null)
            return data.get(position).type.hashCode();
        return 0;
    }

    @Override
    public SectionHeaderView onCreateHeaderViewHolder(ViewGroup parent) {
        return new SectionHeaderView(LayoutInflater.from(context).inflate(R.layout.item_gank_section_header, parent, false));
    }

    @Override
    public void onBindHeaderViewHolder(SectionHeaderView viewHolder, int position) {
        if (data.get(position) != null) {
            viewHolder.tv.setText(data.get(position).type);
        }
    }

    public String formatTime(String str) {
        String[] strings = str.split("-");
        return strings[0].replace("0", "") + "月" + strings[1].replace("0", "") + "日";
    }

    public interface OnItemClickListener {
        void onClickListener(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_gank_detail, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder mHolder, final int position) {
        mHolder.who.setText(StringUtil.getGankStyleStr(data.get(position).who == null ? "None" : data.get(position).who,
                data.get(position).desc));

        if (onItemClickListener != null) {
            mHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onClickListener(v, mHolder.getLayoutPosition());
                }
            });
        }
    }



    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.who)
        TextView who;


        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    static class SectionHeaderView extends RecyclerView.ViewHolder {
        @BindView(R.id.tv)
        TextView tv;

        public SectionHeaderView(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
