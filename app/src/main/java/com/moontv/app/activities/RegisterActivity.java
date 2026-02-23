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

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword, etPasswordConfirm;
    private Button btnRegister;
    private ProgressBar progressBar;
    private TextView tvGoLogin, tvError;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        sessionManager = SessionManager.getInstance(this);
        initViews();
    }

    private void initViews() {
        etName            = findViewById(R.id.et_name);
        etEmail           = findViewById(R.id.et_email);
        etPassword        = findViewById(R.id.et_password);
        etPasswordConfirm = findViewById(R.id.et_password_confirm);
        btnRegister       = findViewById(R.id.btn_register);
        progressBar       = findViewById(R.id.progress_bar);
        tvGoLogin         = findViewById(R.id.tv_go_login);
        tvError           = findViewById(R.id.tv_error);

        btnRegister.setOnClickListener(v -> attemptRegister());

        tvGoLogin.setOnClickListener(v -> {
            // Volver al login sin crear nueva instancia
            finish();
        });
    }

    private void attemptRegister() {
        String name     = etName.getText().toString().trim();
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirm  = etPasswordConfirm.getText().toString().trim();

        // Validaciones
        if (TextUtils.isEmpty(name)) {
            etName.setError("Ingresa tu nombre");
            etName.requestFocus(); return;
        }
        if (name.length() < 2) {
            etName.setError("Nombre muy corto");
            etName.requestFocus(); return;
        }
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Ingresa tu correo");
            etEmail.requestFocus(); return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Correo no válido");
            etEmail.requestFocus(); return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Ingresa una contraseña");
            etPassword.requestFocus(); return;
        }
        if (password.length() < 6) {
            etPassword.setError("Mínimo 6 caracteres");
            etPassword.requestFocus(); return;
        }
        if (!password.equals(confirm)) {
            etPasswordConfirm.setError("Las contraseñas no coinciden");
            etPasswordConfirm.requestFocus(); return;
        }

        setLoading(true);
        tvError.setVisibility(View.GONE);

        AuthRequest request = new AuthRequest(name, email, password);

        RetrofitClient.getInstance(this).getAuthService().register(request)
                .enqueue(new Callback<ApiResponse<User>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<User>> call,
                                           Response<ApiResponse<User>> response) {
                        setLoading(false);

                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {

                            User user = response.body().getData();
                            sessionManager.saveSession(user);

                            Toast.makeText(RegisterActivity.this,
                                    "¡Cuenta creada! Bienvenido " + user.getName() + " 🌙",
                                    Toast.LENGTH_LONG).show();

                            // Ir a MainActivity limpiando el stack
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();

                        } else {
                            String msg = response.body() != null
                                    ? response.body().getMessage()
                                    : "Error al registrar";
                            showError(msg != null ? msg : "Error al crear la cuenta");
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                        setLoading(false);
                        showError("Sin conexión al servidor");
                    }
                });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnRegister.setEnabled(!loading);
        btnRegister.setText(loading ? "Creando cuenta…" : "Crear cuenta");
    }

    private void showError(String msg) {
        tvError.setText("⚠ " + msg);
        tvError.setVisibility(View.VISIBLE);
    }
}
