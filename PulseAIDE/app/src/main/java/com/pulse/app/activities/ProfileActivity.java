package com.pulse.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.pulse.app.R;
import com.pulse.app.database.DatabaseHelper;
import com.pulse.app.models.User;
import com.pulse.app.utils.SessionManager;

public class ProfileActivity extends AppCompatActivity {
    private EditText etDisplayName, etBio;
    private DatabaseHelper db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        db = DatabaseHelper.getInstance(this);
        session = new SessionManager(this);
        etDisplayName = findViewById(R.id.etDisplayName);
        etBio = findViewById(R.id.etBio);
        TextView tvAvatar = findViewById(R.id.tvAvatar);
        TextView tvUsername = findViewById(R.id.tvUsername);

        User user = db.getUserById(session.getUserId());
        if (user != null) {
            tvAvatar.setText(user.getInitial());
            tvUsername.setText("@" + user.username);
            etDisplayName.setText(user.displayName);
            etBio.setText(user.bio);
        }

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnSave).setOnClickListener(v -> {
            db.updateProfile(session.getUserId(), etDisplayName.getText().toString().trim(), etBio.getText().toString().trim());
            Toast.makeText(this, "Salvato!", Toast.LENGTH_SHORT).show();
            finish();
        });
        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            db.setOnline(session.getUserId(), false);
            session.logout();
            startActivity(new Intent(this, AuthActivity.class));
            finishAffinity();
        });
    }
}
