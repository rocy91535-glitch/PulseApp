package com.pulse.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.pulse.app.R;
import com.pulse.app.adapters.ConversationAdapter;
import com.pulse.app.database.DatabaseHelper;
import com.pulse.app.models.User;
import com.pulse.app.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView rvConversations;
    private TextView tvEmpty;
    private ConversationAdapter adapter;
    private DatabaseHelper db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = DatabaseHelper.getInstance(this);
        session = new SessionManager(this);
        rvConversations = findViewById(R.id.rvConversations);
        tvEmpty = findViewById(R.id.tvEmpty);
        rvConversations.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ConversationAdapter(new ArrayList<>(), user -> openChat(user));
        rvConversations.setAdapter(adapter);
        findViewById(R.id.btnSearch).setOnClickListener(v -> startActivity(new Intent(this, SearchActivity.class)));
        findViewById(R.id.btnProfile).setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadConversations();
    }

    private void loadConversations() {
        int myId = session.getUserId();
        List<Integer> partners = db.getConversationPartners(myId);
        List<ConversationAdapter.Conversation> convs = new ArrayList<>();
        for (int partnerId : partners) {
            User partner = db.getUserById(partnerId);
            if (partner == null) continue;
            ConversationAdapter.Conversation conv = new ConversationAdapter.Conversation();
            conv.partner = partner;
            conv.lastMessage = db.getLastMessage(myId, partnerId);
            conv.unreadCount = db.getUnreadCount(partnerId, myId);
            convs.add(conv);
        }
        adapter.setConversations(convs);
        if (convs.isEmpty()) {
            rvConversations.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            rvConversations.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
        }
    }

    private void openChat(User user) {
        Intent i = new Intent(this, ChatActivity.class);
        i.putExtra("partnerId", user.id);
        startActivity(i);
    }
}
