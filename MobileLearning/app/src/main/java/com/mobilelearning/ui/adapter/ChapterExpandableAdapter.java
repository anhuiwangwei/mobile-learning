package com.mobilelearning.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.mobilelearning.R;
import com.mobilelearning.bean.Chapter;
import com.mobilelearning.bean.Section;

import java.util.List;

public class ChapterExpandableAdapter extends BaseExpandableListAdapter {
    
    private Context context;
    private List<Chapter> chapterList;
    private LayoutInflater inflater;
    
    public ChapterExpandableAdapter(Context context, List<Chapter> chapterList) {
        this.context = context;
        this.chapterList = chapterList;
        this.inflater = LayoutInflater.from(context);
    }
    
    @Override
    public int getGroupCount() {
        return chapterList != null ? chapterList.size() : 0;
    }
    
    @Override
    public int getChildrenCount(int groupPosition) {
        List<Section> sections = chapterList.get(groupPosition).getSections();
        return sections != null ? sections.size() : 0;
    }
    
    @Override
    public Object getGroup(int groupPosition) {
        return chapterList.get(groupPosition);
    }
    
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return chapterList.get(groupPosition).getSections().get(childPosition);
    }
    
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
    
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
    
    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_chapter_group, parent, false);
            holder = new GroupViewHolder();
            holder.tvChapterName = convertView.findViewById(R.id.tv_chapter_name);
            holder.tvSectionCount = convertView.findViewById(R.id.tv_section_count);
            convertView.setTag(holder);
        } else {
            holder = (GroupViewHolder) convertView.getTag();
        }
        
        Chapter chapter = chapterList.get(groupPosition);
        holder.tvChapterName.setText(chapter.getChapterName());
        
        int sectionCount = chapter.getSections() != null ? chapter.getSections().size() : 0;
        holder.tvSectionCount.setText(sectionCount + " 个小节");
        
        if (isExpanded) {
            holder.tvChapterName.setTextColor(context.getResources().getColor(R.color.purple_500));
        } else {
            holder.tvChapterName.setTextColor(Color.BLACK);
        }
        
        return convertView;
    }
    
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_section_child, parent, false);
            holder = new ChildViewHolder();
            holder.tvSectionName = convertView.findViewById(R.id.tv_section_name);
            holder.tvSectionIcon = convertView.findViewById(R.id.tv_section_icon);
            holder.tvDuration = convertView.findViewById(R.id.tv_duration);
            holder.tvSectionType = convertView.findViewById(R.id.tv_section_type);
            convertView.setTag(holder);
        } else {
            holder = (ChildViewHolder) convertView.getTag();
        }
        
        Section section = chapterList.get(groupPosition).getSections().get(childPosition);
        holder.tvSectionName.setText(section.getSectionName());
        
        if ("video".equals(section.getSectionType())) {
            holder.tvSectionIcon.setText("📹");
            holder.tvDuration.setText(section.getDuration() + "秒");
            holder.tvSectionType.setText("视频");
        } else if ("pdf".equals(section.getSectionType())) {
            holder.tvSectionIcon.setText("📄");
            holder.tvDuration.setText(section.getDuration() + "页");
            holder.tvSectionType.setText("PDF");
        } else if ("exam".equals(section.getSectionType())) {
            holder.tvSectionIcon.setText("📝");
            holder.tvDuration.setText("");
            holder.tvSectionType.setText("考试");
        } else {
            holder.tvSectionIcon.setText("");
            holder.tvDuration.setText("");
            holder.tvSectionType.setText("其他");
        }
        
        return convertView;
    }
    
    static class GroupViewHolder {
        TextView tvChapterName;
        TextView tvSectionCount;
    }
    
    static class ChildViewHolder {
        TextView tvSectionName;
        TextView tvSectionIcon;
        TextView tvDuration;
        TextView tvSectionType;
    }
        
        Section section = chapterList.get(groupPosition).getSections().get(childPosition);
        holder.tvSectionName.setText(section.getSectionName());
        
        String type;
        int typeColor;
        if ("video".equals(section.getSectionType())) {
            type = "📹 视频";
            typeColor = context.getResources().getColor(R.color.teal_200);
        } else if ("pdf".equals(section.getSectionType())) {
            type = "📄 PDF";
            typeColor = context.getResources().getColor(R.color.purple_200);
        } else if ("exam".equals(section.getSectionType())) {
            type = "📝 考试";
            typeColor = context.getResources().getColor(R.color.purple_500);
        } else {
            type = "其他";
            typeColor = Color.GRAY;
        }
        holder.tvSectionType.setText(type);
        holder.tvSectionType.setTextColor(typeColor);
        
        return convertView;
    }
    
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
    
    static class GroupViewHolder {
        TextView tvChapterName;
        TextView tvSectionCount;
    }
    
    static class ChildViewHolder {
        TextView tvSectionName;
        TextView tvSectionType;
    }
}
