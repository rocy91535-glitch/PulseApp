package com.pulse.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.pulse.app.R;
import com.pulse.app.models.Message;
import com.pulse.app.models.User;
import com.pulse.app.utils.TimeUtils;
import java.util.List;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConvHolder> {
    public static class Conversation {
        public User partner;
        public Message lastMessage;
        public int unreadCount;
    }

    private List<Conversation> conversations;
    private OnConvClickListener listener;

    public interface OnConvClickListener {
        void onConvClick(User partner);
    }

    public ConversationAdapter(List<Conversation> conversations, OnConvClickListener listener) {
        this.conversations = conversations;
        this.listener = listener;
    }

    @Override
    public ConvHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversation, parent, false);
        return new ConvHolder(v);
    }

    @Override
    public void onBindViewHolder(ConvHolder holder, int position) {
        Conversation conv = conversations.get(position);
        User u = conv.partner;
        holder.tvAvatar.setText(u.getInitial());
        holder.tvName.setText(u.displayName != null && !u.displayName.isEmpty() ? u.displayName : u.username);
        if (conv.lastMessage != null) {
            holder.tvLastMessage.setText(conv.lastMessage.content);
            holder.tvTime.setText(TimeUtils.format(conv.lastMessage.timestamp));
        }
        holder.onlineDot.setVisibility(u.isOnline ? View.VISIBLE : View.INVISIBLE);
        if (conv.unreadCount > 0) {
            holder.tvUnread.setVisibility(View.VISIBLE);
            holder.tvUnread.setText(String.valueOf(conv.unreadCount));
        } else {
            holder.tvUnread.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(v -> listener.onConvClick(u));
    }

    @Override
    public int getItemCount() { return conversations.size(); }

    public void setConversations(List<Conversation> conversations) {
        this.conversations = conversations;
        notifyDataSetChanged();
    }

    static class ConvHolder extends RecyclerView.ViewHolder {
        TextView tvAvatar, tvName, tvLastMessage, tvTime, tvUnread;
        View onlineDot;
        ConvHolder(View v) {
            super(v);
            tvAvatar = v.findViewById(R.id.tvAvatar);
            tvName = v.findViewById(R.id.tvName);
            tvLastMessage = v.findViewById(R.id.tvLastMessage);
            tvTime = v.findViewById(R.id.tvTime);
            tvUnread = v.findViewById(R.id.tvUnread);
            onlineDot = v.findViewById(R.id.onlineDot);
        }
    }
}
