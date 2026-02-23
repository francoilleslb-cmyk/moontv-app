package com.moontv.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.moontv.app.R;
import com.moontv.app.models.ApiResponse;
import com.moontv.app.models.AuthRequest;
import com.moontv.app.models.User;
import com.moontv.app.network.RetrofitClient;
import com.moontv.app.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    private TextView tvGoRegister, tvError;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = SessionManager.getInstance(this);

        // Si ya está logueado, saltar directo a MainActivity
        if (sessionManager.isLoggedIn()) {
            goToMain();
            return;
        }

        initViews();
    }

    private void initViews() {
        etEmail      = findViewById(R.id.et_email);
        etPassword   = findViewById(R.id.et_password);
        btnLogin     = findViewById(R.id.btn_login);
        progressBar  = findViewById(R.id.progress_bar);
        tvGoRegister = findViewById(R.id.tv_go_register);
        tvError      = findViewById(R.id.tv_error);

        btnLogin.setOnClickListener(v -> attemptLogin());

        tvGoRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    private void attemptLogin() {
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validaciones
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Ingresa tu correo");
            etEmail.requestFocus();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Correo no válido");
            etEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Ingresa tu contraseña");
            etPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            etPassword.setError("Mínimo 6 caracteres");
            etPassword.requestFocus();
            return;
        }

        setLoading(true);
        tvError.setVisibility(View.GONE);

        AuthRequest request = new AuthRequest(email, password);

        RetrofitClient.getInstance(this).getAuthService().login(request)
                .enqueue(new Callback<ApiResponse<User>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<User>> call,
                                           Response<ApiResponse<User>> response) {
                        setLoading(false);

                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {

                            User user = response.body().getData();
                            sessionManager.saveSession(user);

                            Toast.makeText(LoginActivity.this,
                                    "¡Bienvenido " + user.getName() + "! 🌙",
                                    Toast.LENGTH_SHORT).show();

                            goToMain();

                        } else {
                            String msg = response.body() != null
                                    ? response.body().getMessage()
                                    : "Credenciales incorrectas";
                            showError(msg != null ? msg : "Error al iniciar sesión");
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                        setLoading(false);
                        showError("Sin conexión al servidor.\nVerifica los Ajustes ⚙");
                    }
                });
    }

    private void goToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!loading);
        btnLogin.setText(loading ? "Entrando…" : "Iniciar sesión");
    }

    private void showError(String msg) {
        tvError.setText("⚠ " + msg);
        tvError.setVisibility(View.VISIBLE);
    }
}
