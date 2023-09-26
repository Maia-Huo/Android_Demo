package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.News;

import java.util.List;

public class NewsAdapter extends ArrayAdapter<News> {

    private List<News> mNewsData;
    private Context mContext;
    private int resourceId;

    public NewsAdapter(Context context, int resourceId, List<News> data) {
        super(context, resourceId, data);
        this.mContext = context;
        this.mNewsData = data;
        this.resourceId = resourceId;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        News news = getItem(position);
        View view ;

        //创建每个列表的视图，将布局资源和新闻数据联系起来
        //这里的resourceId就是R.layout.list_item
        //这里通过LayoutInflater创建视图
        view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);

        TextView tvTitle  = view.findViewById(R.id.tv_title);
        TextView tvAuthor = view.findViewById(R.id.tv_subtitle);
        ImageView ivImage = view.findViewById(R.id.iv_image);

        //将新闻数据填充到每个视图中
        tvTitle.setText(news.getTitle());
        tvAuthor.setText(news.getAuthor());
        ivImage.setImageResource(news.getImageResourceId());
        return view;
    }
}