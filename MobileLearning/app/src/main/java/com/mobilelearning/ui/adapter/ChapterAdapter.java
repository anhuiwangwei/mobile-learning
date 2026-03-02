package com.mobilelearning.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mobilelearning.R;
import com.mobilelearning.bean.Chapter;

import java.util.List;

public class ChapterAdapter extends BaseAdapter {
    
    private Context context;
    private List<Chapter> chapterList;
    private LayoutInflater inflater;
    
    public ChapterAdapter(Context context, List<Chapter> chapterList) {
        this.context = context;
        this.chapterList = chapterList;
        this.inflater = LayoutInflater.from(context);
    }
    
    @Override
    public int getCount() {
        return chapterList != null ? chapterList.size() : 0;
    }
    
    @Override
    public Object getItem(int position) {
        return chapterList.get(position);
    }
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_chapter, parent, false);
            holder = new ViewHolder();
            holder.tvChapterName = convertView.findViewById(R.id.tv_chapter_name);
            holder.tvSectionCount = convertView.findViewById(R.id.tv_section_count);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        Chapter chapter = chapterList.get(position);
        holder.tvChapterName.setText(chapter.getChapterName());
        
        int sectionCount = chapter.getSections() != null ? chapter.getSections().size() : 0;
        holder.tvSectionCount.setText(sectionCount + "个小节");
        
        return convertView;
    }
    
    static class ViewHolder {
        TextView tvChapterName;
        TextView tvSectionCount;
    }
}
