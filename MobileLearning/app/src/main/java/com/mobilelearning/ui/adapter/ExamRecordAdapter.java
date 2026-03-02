package com.mobilelearning.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mobilelearning.R;
import com.mobilelearning.bean.ExamRecord;
import com.mobilelearning.utils.DateUtil;

import java.util.List;

public class ExamRecordAdapter extends BaseAdapter {
    
    private Context context;
    private List<ExamRecord> recordList;
    private LayoutInflater inflater;
    
    public ExamRecordAdapter(Context context, List<ExamRecord> recordList) {
        this.context = context;
        this.recordList = recordList;
        this.inflater = LayoutInflater.from(context);
    }
    
    @Override
    public int getCount() {
        return recordList != null ? recordList.size() : 0;
    }
    
    @Override
    public Object getItem(int position) {
        return recordList.get(position);
    }
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_exam_record, parent, false);
            holder = new ViewHolder();
            holder.tvExamName = convertView.findViewById(R.id.tv_exam_name);
            holder.tvScore = convertView.findViewById(R.id.tv_score);
            holder.tvStatus = convertView.findViewById(R.id.tv_status);
            holder.tvTime = convertView.findViewById(R.id.tv_time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        ExamRecord record = recordList.get(position);
        holder.tvExamName.setText("考试 #" + record.getId());
        holder.tvScore.setText(record.getScore() + "分");
        holder.tvStatus.setText(record.getStatus() == 1 ? "已完成" : "进行中");
        holder.tvTime.setText(DateUtil.format(record.getSubmitTime() != null ? record.getSubmitTime() : record.getStartTime()));
        
        return convertView;
    }
    
    static class ViewHolder {
        TextView tvExamName;
        TextView tvScore;
        TextView tvStatus;
        TextView tvTime;
    }
}
