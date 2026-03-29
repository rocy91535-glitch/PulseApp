package com.pulse.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.pulse.app.R;
import com.pulse.app.models.Message;
import com.pulse.app.utils.TimeUtils;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_SENT = 1;
    private static final int VIEW_RECV = 2;
    private List<Message> messages;
    private int currentUserId;

    public MessageAdapter(List<Message> messages, int currentUserId) {
        this.messages = messages;
        this.currentUserId = currentUserId;
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).senderId == currentUserId ? VIEW_SENT : VIEW_RECV;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_SENT) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
            return new MsgHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_recv, parent, false);
            return new MsgHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message m = messages.get(position);
        MsgHolder h = (MsgHolder) holder;
        h.tvContent.setText(m.content);
        h.tvTime.setText(TimeUtils.format(m.timestamp));
    }

    @Override
    public int getItemCount() { return messages.size(); }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    static class MsgHolder extends RecyclerView.ViewHolder {
        TextView tvContent, tvTime;
        MsgHolder(View v) {
            super(v);
            tvContent = v.findViewById(R.id.tvContent);
            tvTime = v.findViewById(R.id.tvTime);
        }
    }
}
