package com.mobilelearning.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mobilelearning.R;
import com.mobilelearning.bean.Section;

import java.util.List;

public class SectionAdapter extends BaseAdapter {
    
    private Context context;
    private List<Section> sectionList;
    private LayoutInflater inflater;
    
    public SectionAdapter(Context context, List<Section> sectionList) {
        this.context = context;
        this.sectionList = sectionList;
        this.inflater = LayoutInflater.from(context);
    }
    
    @Override
    public int getCount() {
        return sectionList != null ? sectionList.size() : 0;
    }
    
    @Override
    public Object getItem(int position) {
        return sectionList.get(position);
    }
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_section, parent, false);
            holder = new ViewHolder();
            holder.tvSectionName = convertView.findViewById(R.id.tv_section_name);
            holder.tvSectionType = convertView.findViewById(R.id.tv_section_type);
            holder.tvDuration = convertView.findViewById(R.id.tv_duration);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        Section section = sectionList.get(position);
        holder.tvSectionName.setText(section.getSectionName());
        
        String type = "video".equals(section.getSectionType()) ? "视频" : "PDF";
        holder.tvSectionType.setText(type);
        
        if ("video".equals(section.getSectionType())) {
            holder.tvDuration.setText(section.getDuration() + "秒");
        } else {
            holder.tvDuration.setText(section.getDuration() + "页");
        }
        
        return convertView;
    }
    
    static class ViewHolder {
        TextView tvSectionName;
        TextView tvSectionType;
        TextView tvDuration;
    }
}
