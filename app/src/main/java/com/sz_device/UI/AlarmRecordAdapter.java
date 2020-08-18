package com.sz_device.UI;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sz_device.Bean.ReUploadBean;
import com.sz_device.R;

import java.util.List;

public class AlarmRecordAdapter extends RecyclerView.Adapter<AlarmRecordAdapter.AlarmRecordViewHolder>{


    private Context mContext;

    private List<ReUploadBean> list;

    public AlarmRecordAdapter(Context mContext, List<ReUploadBean> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @Override
    public AlarmRecordAdapter.AlarmRecordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        AlarmRecordAdapter.AlarmRecordViewHolder holder = new AlarmRecordAdapter.AlarmRecordViewHolder(LayoutInflater.from(
                mContext).inflate(R.layout.layout_alarm_unit, parent,
                false));
        return holder;
    }


    @Override
    public void onBindViewHolder(AlarmRecordAdapter.AlarmRecordViewHolder holder, final int position) {
        holder.tv_alarmName.setText("周界红外报警");
        holder.tv_alarmTime.setText(list.get(position).getContent());


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class AlarmRecordViewHolder extends RecyclerView.ViewHolder {

        TextView tv_alarmName;
        TextView tv_alarmTime;


        public AlarmRecordViewHolder(View view) {
            super(view);
            tv_alarmName = (TextView) view.findViewById(R.id.tv_alarmName);
            tv_alarmTime = (TextView) view.findViewById(R.id.tv_alarmTime);



        }
    }
}
