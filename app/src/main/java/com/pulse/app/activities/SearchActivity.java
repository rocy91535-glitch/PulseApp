package com.pulse.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.pulse.app.R;
import com.pulse.app.adapters.UserAdapter;
import com.pulse.app.database.DatabaseHelper;
import com.pulse.app.models.User;
import com.pulse.app.utils.SessionManager;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    private RecyclerView rvUsers;
    private UserAdapter adapter;
    private DatabaseHelper db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        db = DatabaseHelper.getInstance(this);
        session = new SessionManager(this);
        rvUsers = findViewById(R.id.rvUsers);
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserAdapter(new ArrayList<>(), user -> openChat(user));
        rvUsers.setAdapter(adapter);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        EditText etSearch = findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search(s.toString());
            }
            public void afterTextChanged(Editable s) {}
        });
    }

    private void search(String query) {
        if (query.length() < 1) { adapter.setUsers(new ArrayList<>()); return; }
        adapter.setUsers(db.searchUsers(query, session.getUserId()));
    }

    private void openChat(User user) {
        Intent i = new Intent(this, ChatActivity.class);
        i.putExtra("partnerId", user.id);
        startActivity(i);
    }
}
