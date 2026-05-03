package com.example.smartcommunity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    EditText email, password;
    Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();

        //register module
        TextView registerText = findViewById(R.id.registerText);
        registerText.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        loginBtn = findViewById(R.id.loginBtn);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        loginBtn.setOnClickListener(view -> {
            String emailText = email.getText().toString().trim();
            String passwordText = password.getText().toString().trim();

            if (emailText.isEmpty() || passwordText.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            //user logs
                            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            createUserLog(userId, "Login", "User logged into the system");

                            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

                            usersRef.child(userId).child("role").get()
                                    .addOnCompleteListener(roleTask -> {
                                        if (roleTask.isSuccessful()) {

                                            String role = roleTask.getResult().getValue(String.class);

                                            Toast.makeText(LoginActivity.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();

                                            if ("Student".equals(role)) {
                                                startActivity(new Intent(LoginActivity.this, homepage.class));
                                                finish();
                                            }else if("Admin".equals(role)){
                                                startActivity(new Intent(LoginActivity.this, AdminDashboardActivity.class));
                                                finish();
                                            }
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Failed to get user role", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        } else {
                            Toast.makeText(this, "Wrong password or email", Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }
    private void createUserLog(String userId, String action, String details) {
        DatabaseReference logsRef = FirebaseDatabase.getInstance().getReference("user_logs");
        String logId = logsRef.push().getKey();

        if (logId == null) return;

        UserLogModel log = new UserLogModel(
                logId,
                userId,
                action,
                details,
                System.currentTimeMillis()
        );

        logsRef.child(logId).setValue(log);
    }
}
