package com.example.smartcommunity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class UserLogsAdapter extends RecyclerView.Adapter<UserLogsAdapter.LogViewHolder> {

    ArrayList<UserLogModel> logList;

    public UserLogsAdapter(ArrayList<UserLogModel> logList) {
        this.logList = logList;
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_log_item, parent, false);
        return new LogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        UserLogModel log = logList.get(position);

        holder.tvLogAction.setText(log.getAction());
        holder.tvLogDetails.setText(log.getDetails());

        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault());
        holder.tvLogTime.setText(sdf.format(new Date(log.getTimestamp())));
    }

    @Override
    public int getItemCount() {
        return logList.size();
    }

    public static class LogViewHolder extends RecyclerView.ViewHolder {
        TextView tvLogAction, tvLogDetails, tvLogTime;

        public LogViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLogAction = itemView.findViewById(R.id.tvLogAction);
            tvLogDetails = itemView.findViewById(R.id.tvLogDetails);
            tvLogTime = itemView.findViewById(R.id.tvLogTime);
        }
    }
}