package com.mobilelearning.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mobilelearning.R;
import com.mobilelearning.bean.Course;

import java.util.List;

public class CourseAdapter extends BaseAdapter {
    
    private Context context;
    private List<Course> courseList;
    private LayoutInflater inflater;
    
    public CourseAdapter(Context context, List<Course> courseList) {
        this.context = context;
        this.courseList = courseList;
        this.inflater = LayoutInflater.from(context);
    }
    
    @Override
    public int getCount() {
        return courseList != null ? courseList.size() : 0;
    }
    
    @Override
    public Object getItem(int position) {
        return courseList.get(position);
    }
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_course, parent, false);
            holder = new ViewHolder();
            holder.ivCover = convertView.findViewById(R.id.iv_course_cover);
            holder.tvName = convertView.findViewById(R.id.tv_course_name);
            holder.tvDesc = convertView.findViewById(R.id.tv_course_desc);
            holder.tvDuration = convertView.findViewById(R.id.tv_course_duration);
            holder.tvViews = convertView.findViewById(R.id.tv_course_views);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        Course course = courseList.get(position);
        holder.tvName.setText(course.getCourseName());
        holder.tvDesc.setText(course.getCourseDesc());
        holder.tvDuration.setText(course.getDuration() + "分钟");
        holder.tvViews.setText(course.getViewCount() + "次浏览");
        
        if (course.getCoverImage() != null && !course.getCoverImage().isEmpty()) {
            Glide.with(context)
                    .load(course.getCoverImage())
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(holder.ivCover);
        }
        
        return convertView;
    }
    
    static class ViewHolder {
        ImageView ivCover;
        TextView tvName;
        TextView tvDesc;
        TextView tvDuration;
        TextView tvViews;
    }
}
