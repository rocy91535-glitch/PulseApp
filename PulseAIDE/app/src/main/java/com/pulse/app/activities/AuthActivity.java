package com.pulse.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.pulse.app.R;
import com.pulse.app.database.DatabaseHelper;
import com.pulse.app.models.User;
import com.pulse.app.utils.SessionManager;

public class AuthActivity extends AppCompatActivity {
    private boolean isLogin = true;
    private EditText etUsername, etPassword, etDisplayName;
    private TextView tabLogin, tabRegister, tvError;
    private Button btnSubmit;
    private DatabaseHelper db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        db = DatabaseHelper.getInstance(this);
        session = new SessionManager(this);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etDisplayName = findViewById(R.id.etDisplayName);
        tabLogin = findViewById(R.id.tabLogin);
        tabRegister = findViewById(R.id.tabRegister);
        btnSubmit = findViewById(R.id.btnSubmit);
        tvError = findViewById(R.id.tvError);

        tabLogin.setOnClickListener(v -> switchMode(true));
        tabRegister.setOnClickListener(v -> switchMode(false));
        btnSubmit.setOnClickListener(v -> submit());
    }

    private void switchMode(boolean login) {
        isLogin = login;
        etDisplayName.setVisibility(login ? View.GONE : View.VISIBLE);
        btnSubmit.setText(login ? "Accedi" : "Registrati");
        tabLogin.setBackgroundResource(login ? R.drawable.tab_selected_bg : R.drawable.tab_bg);
        tabRegister.setBackgroundResource(login ? R.drawable.tab_bg : R.drawable.tab_selected_bg);
        tabLogin.setTextColor(login ? getResources().getColor(R.color.white) : getResources().getColor(R.color.text2));
        tabRegister.setTextColor(login ? getResources().getColor(R.color.text2) : getResources().getColor(R.color.white));
        tvError.setVisibility(View.GONE);
    }

    private void submit() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        if (username.isEmpty() || password.isEmpty()) {
            showError("Compila tutti i campi");
            return;
        }
        if (isLogin) {
            User user = db.loginUser(username, password);
            if (user != null) {
                session.saveSession(user.id, user.username);
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                showError("Username o password errati");
            }
        } else {
            String displayName = etDisplayName.getText().toString().trim();
            if (displayName.isEmpty()) displayName = username;
            long id = db.registerUser(username, password, displayName);
            if (id != -1) {
                session.saveSession((int) id, username);
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                showError("Username già in uso");
            }
        }
    }

    private void showError(String msg) {
        tvError.setText(msg);
        tvError.setVisibility(View.VISIBLE);
    }
}
