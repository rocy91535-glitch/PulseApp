package com.pulse.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.pulse.app.R;
import com.pulse.app.models.User;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserHolder> {
    private List<User> users;
    private OnUserClickListener listener;

    public interface OnUserClickListener {
        void onUserClick(User user);
    }

    public UserAdapter(List<User> users, OnUserClickListener listener) {
        this.users = users;
        this.listener = listener;
    }

    @Override
    public UserHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserHolder(v);
    }

    @Override
    public void onBindViewHolder(UserHolder holder, int position) {
        User u = users.get(position);
        holder.tvAvatar.setText(u.getInitial());
        holder.tvDisplayName.setText(u.displayName != null && !u.displayName.isEmpty() ? u.displayName : u.username);
        holder.tvUsername.setText("@" + u.username);
        holder.onlineDot.setVisibility(u.isOnline ? View.VISIBLE : View.INVISIBLE);
        holder.itemView.setOnClickListener(v -> listener.onUserClick(u));
    }

    @Override
    public int getItemCount() { return users.size(); }

    public void setUsers(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    static class UserHolder extends RecyclerView.ViewHolder {
        TextView tvAvatar, tvDisplayName, tvUsername;
        View onlineDot;
        UserHolder(View v) {
            super(v);
            tvAvatar = v.findViewById(R.id.tvAvatar);
            tvDisplayName = v.findViewById(R.id.tvDisplayName);
            tvUsername = v.findViewById(R.id.tvUsername);
            onlineDot = v.findViewById(R.id.onlineDot);
        }
    }
}
