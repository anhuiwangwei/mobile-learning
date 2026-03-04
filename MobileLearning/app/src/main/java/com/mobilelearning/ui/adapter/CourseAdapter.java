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
import java.util.Map;

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
            holder.tvTeacherName = convertView.findViewById(R.id.tv_teacher_name);
            holder.tvOrderLearning = convertView.findViewById(R.id.tv_order_learning);
            holder.tvPageTurnTime = convertView.findViewById(R.id.tv_page_turn_time);
            holder.tvProgress = convertView.findViewById(R.id.tv_progress);
            holder.tvSectionIcons = convertView.findViewById(R.id.tv_section_icons);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        Course course = courseList.get(position);
        holder.tvName.setText(course.getCourseName());
        holder.tvDesc.setText(course.getCourseDesc());
        
        if (course.getTeacherName() != null && !course.getTeacherName().isEmpty()) {
            holder.tvTeacherName.setText("教师：" + course.getTeacherName());
        } else {
            holder.tvTeacherName.setText("");
        }
        
        if (course.getIsOrderLearning() != null && course.getIsOrderLearning() == 1) {
            holder.tvOrderLearning.setVisibility(View.VISIBLE);
            holder.tvOrderLearning.setText("🔒 顺序学习");
        } else {
            holder.tvOrderLearning.setVisibility(View.GONE);
        }
        
        if (course.getPageTurnTime() != null && course.getPageTurnTime() > 0) {
            holder.tvPageTurnTime.setVisibility(View.VISIBLE);
            holder.tvPageTurnTime.setText("⏱ 翻页：" + course.getPageTurnTime() + "秒");
        } else {
            holder.tvPageTurnTime.setVisibility(View.GONE);
        }
        
        Map<String, Object> progress = course.getProgress();
        if (progress != null) {
            Integer percentage = (Integer) progress.get("percentage");
            Integer totalVideo = (Integer) progress.get("totalVideo");
            Integer totalPdf = (Integer) progress.get("totalPdf");
            Integer totalExam = (Integer) progress.get("totalExam");
            
            if (percentage != null) {
                holder.tvProgress.setText("进度：" + percentage + "%");
            } else {
                holder.tvProgress.setText("进度：0%");
            }
            
            StringBuilder icons = new StringBuilder();
            if (totalVideo != null && totalVideo > 0) {
                icons.append("📹").append(totalVideo).append(" ");
            }
            if (totalPdf != null && totalPdf > 0) {
                icons.append("📄").append(totalPdf).append(" ");
            }
            if (totalExam != null && totalExam > 0) {
                icons.append("📝").append(totalExam);
            }
            holder.tvSectionIcons.setText(icons.toString());
        } else {
            holder.tvProgress.setText("进度：0%");
            holder.tvSectionIcons.setText("");
        }
        
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
        TextView tvTeacherName;
        TextView tvOrderLearning;
        TextView tvPageTurnTime;
        TextView tvProgress;
        TextView tvSectionIcons;
    }
}
