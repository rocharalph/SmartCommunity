package com.example.smartcommunity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdminUsersAdapter extends RecyclerView.Adapter<AdminUsersAdapter.UserViewHolder> {

    Context context;
    ArrayList<UserItemModel> userList;

    public AdminUsersAdapter(Context context, ArrayList<UserItemModel> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_user_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserItemModel user = userList.get(position);

        holder.tvUserName.setText(user.getFullName());
        holder.tvUserEmail.setText(user.getEmail());
        holder.tvUserRole.setText("Role: " + user.getRole());
        holder.tvUserStatus.setText("Status: " + user.getStatus());

        holder.btnOpenUser.setOnClickListener(v -> {
            Intent intent = new Intent(context, AdminUserDetailsActivity.class);
            intent.putExtra("userId", user.getUserId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvUserEmail, tvUserRole, tvUserStatus;
        Button btnOpenUser;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserEmail = itemView.findViewById(R.id.tvUserEmail);
            tvUserRole = itemView.findViewById(R.id.tvUserRole);
            tvUserStatus = itemView.findViewById(R.id.tvUserStatus);
            btnOpenUser = itemView.findViewById(R.id.btnOpenUser);
        }
    }
}