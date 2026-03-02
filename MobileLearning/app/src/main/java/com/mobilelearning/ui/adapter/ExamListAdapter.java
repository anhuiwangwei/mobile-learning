package com.mobilelearning.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mobilelearning.R;
import com.mobilelearning.bean.ExamPaper;

import java.util.List;

public class ExamListAdapter extends BaseAdapter {
    
    private Context context;
    private List<ExamPaper> examList;
    private LayoutInflater inflater;
    
    public ExamListAdapter(Context context, List<ExamPaper> examList) {
        this.context = context;
        this.examList = examList;
        this.inflater = LayoutInflater.from(context);
    }
    
    @Override
    public int getCount() {
        return examList != null ? examList.size() : 0;
    }
    
    @Override
    public Object getItem(int position) {
        return examList.get(position);
    }
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_exam_list, parent, false);
            holder = new ViewHolder();
            holder.tvExamName = convertView.findViewById(R.id.tv_exam_name);
            holder.tvTotalScore = convertView.findViewById(R.id.tv_total_score);
            holder.tvPassScore = convertView.findViewById(R.id.tv_pass_score);
            holder.tvDuration = convertView.findViewById(R.id.tv_duration);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        ExamPaper exam = examList.get(position);
        holder.tvExamName.setText(exam.getPaperName());
        holder.tvTotalScore.setText("总分：" + exam.getTotalScore());
        holder.tvPassScore.setText("及格：" + exam.getPassScore());
        holder.tvDuration.setText(exam.getDuration() + "分钟");
        
        return convertView;
    }
    
    static class ViewHolder {
        TextView tvExamName;
        TextView tvTotalScore;
        TextView tvPassScore;
        TextView tvDuration;
    }
}
