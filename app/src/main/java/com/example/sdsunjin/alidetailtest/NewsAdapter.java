package com.example.sdsunjin.alidetailtest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by sdsunjin on 16/7/13.
 */
public class NewsAdapter extends BaseAdapter {

    private List<NewsBean> mList;
    private LayoutInflater mInflater; //创建LayoutInflater对象用于转化layout布局作为每一个item
    private ImageLoader mImageLoader;

    public NewsAdapter(Context context, List<NewsBean> data) {
        mList = data;
        // 初始化inflater对象
        mInflater = LayoutInflater.from(context);
        mImageLoader = new ImageLoader();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /*
    * getView方法的功能就是创建一个存储了item布局View的对象，并将各个view上的数据填充后返回该item在ListView上展示
    * 如果convertView为空时，首先是吧item组件上的各个子组件view封装成一个布局对象viewHolder，然后把这个item布局对象通过setTag缓存到convertView
    * 如果convertView中不为空，则直接通过getTag取出其中的view缓存对象存储到viewHolder对象中
    * */

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        String url = null;

        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.item_layout, null); //将layout文件，即每一个item列表，转化成convertView，inflate()方法用于将xml布局文件生成View
            viewHolder = new ViewHolder();
            viewHolder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tv_content);
            /*
            * View中的setTag（Onbect）表示给View添加一个格外的数据，以后可以用getTag()将这个数据取出来。
            * ViewHolder只是将需要缓存的那些view封装好，convertView的setTag才是将这些缓存起来供下次调用
            * */
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.ivIcon.setImageResource(R.drawable.ic_launcher);
        url = mList.get(position).getNewsIcon();
        viewHolder.ivIcon.setTag(url); //将展示在当前的view上的图片url作为其tag标签，防止当前view获取convertView中缓存的图片数据出现错位显示的问题。
        /*
        * 创建了LruCache图片缓存机制后，该处就不能每次加载图片时都new一个新的对象，否则每次都要创建一个LreCache，而应该创建统一的对象，从而达到保留一个LreCache的目的
        * new ImageLoader().showImageByThread(viewHolder.ivIcon, url);
        * */
        mImageLoader.showImageByThread(viewHolder.ivIcon, url);
        viewHolder.tvTitle.setText(mList.get(position).newsTitle);  // mList.get(position)取出的每一个newsBean对象都包含三个元素
        viewHolder.tvContent.setText(mList.get(position).newsContent);

        return convertView;
    }

    /*
    * 创建ViewHolder类用于将item中的各个view封装成一个对象形式，方便直接调用
    * */
    class ViewHolder {
        public TextView tvTitle;
        public TextView tvContent;
        public ImageView ivIcon;
    }
}
