package com.pulse.app.activities;

import android.os.Bundle;
import android.os.Handler;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.pulse.app.R;
import com.pulse.app.adapters.MessageAdapter;
import com.pulse.app.database.DatabaseHelper;
import com.pulse.app.models.Message;
import com.pulse.app.models.User;
import com.pulse.app.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView rvMessages;
    private EditText etMessage;
    private MessageAdapter adapter;
    private DatabaseHelper db;
    private SessionManager session;
    private int partnerId, myId;
    private Handler handler = new Handler();
    private Runnable refreshRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        db = DatabaseHelper.getInstance(this);
        session = new SessionManager(this);
        myId = session.getUserId();
        partnerId = getIntent().getIntExtra("partnerId", -1);
        User partner = db.getUserById(partnerId);

        rvMessages = findViewById(R.id.rvMessages);
        etMessage = findViewById(R.id.etMessage);
        TextView tvName = findViewById(R.id.tvName);
        TextView tvAvatar = findViewById(R.id.tvAvatar);
        TextView tvStatus = findViewById(R.id.tvStatus);

        if (partner != null) {
            tvName.setText(partner.displayName != null && !partner.displayName.isEmpty() ? partner.displayName : partner.username);
            tvAvatar.setText(partner.getInitial());
            tvStatus.setText(partner.isOnline ? "Online" : "Offline");
        }

        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessageAdapter(new ArrayList<>(), myId);
        rvMessages.setAdapter(adapter);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnSend).setOnClickListener(v -> sendMessage());

        db.markAsRead(partnerId, myId);
        loadMessages();

        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                loadMessages();
                handler.postDelayed(this, 2000);
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.post(refreshRunnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(refreshRunnable);
    }

    private void loadMessages() {
        List<Message> messages = db.getMessages(myId, partnerId);
        adapter.setMessages(messages);
        if (!messages.isEmpty()) rvMessages.scrollToPosition(messages.size() - 1);
    }

    private void sendMessage() {
        String text = etMessage.getText().toString().trim();
        if (text.isEmpty()) return;
        db.sendMessage(myId, partnerId, text);
        etMessage.setText("");
        loadMessages();
    }
}
