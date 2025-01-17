package org.dieschnittstelle.mobile.android.skeleton;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.dieschnittstelle.mobile.android.skeleton.model.User;
import org.dieschnittstelle.mobile.android.skeleton.model.impl.RetrofitRemoteToDoItemCRUDOperationsImpl;


public class LoginActivity extends AppCompatActivity {

    private TextInputEditText email, pwd;
    private TextInputLayout emailInput, pwdInput, buttonWrapper;
    private User user = new User();
    private Button loginButton;
    private Activity uiThreadProvider;
    private ProgressDialog progressDialog;
    private boolean pwdCorrect = false;
    private boolean emailCorrect = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!((ToDoItemApplication) getApplication()).isServerAvailable()) {
            startActivity(new Intent(this, MainActivity.class));
            return;
        }

        setContentView(R.layout.activity_login);
        loginButton = findViewById(R.id.loginButton);
        buttonWrapper = findViewById(R.id.buttonWrapper);

        emailInput = findViewById(R.id.emailWrapper);
        email = findViewById(R.id.email);
        email.setOnFocusChangeListener(this::onFocusEmailChange);
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                emailInput.setError(null);
                if (!TextUtils.isEmpty(s) && Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
                    emailCorrect = true;
                    user.setEmail(s.toString());
                    if (emailCorrect && pwdCorrect) {
                        loginButton.setEnabled(true);
                    }
                } else {
                    emailCorrect = false;
                    loginButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        pwdInput = findViewById(R.id.passwordWrapper);
        pwd = findViewById(R.id.password);
        pwd.setOnFocusChangeListener(this::onFocusPwdChange);
        pwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                pwdInput.setError(null);
                if (!TextUtils.isEmpty(s) && s.length() == 6) {
                    pwdCorrect = true;
                    user.setPwd(s.toString());
                    if (emailCorrect && pwdCorrect) {
                        loginButton.setEnabled(true);
                    }
                } else {
                    pwdCorrect = false;
                    loginButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    protected void onFocusEmailChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            String email = ((EditText) v).getText().toString();
            if (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInput.setError(null);
                user.setEmail(email);
                this.emailCorrect = true;
            } else if (TextUtils.isEmpty(email)) {
                emailInput.setError("Bitte gib eine E-Mail Adresse ein");
                this.emailCorrect = false;
            } else {
                emailInput.setError("Bitte gib eine gültige E-Mail Adresse ein");
                this.emailCorrect = false;
            }
        }
    }

    protected void onFocusPwdChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            String password = ((EditText) v).getText().toString();
            if (!TextUtils.isEmpty(password) && password.length() == 6) {
                pwdInput.setError(null);
                user.setPwd(password);
                this.pwdCorrect = true;
            } else if (TextUtils.isEmpty(password)) {
                pwdInput.setError("Bitte gib ein Passwort ein");
                this.pwdCorrect = false;
            } else {
                pwdInput.setError("Bitte gib ein Passwort mit genau 6 Zeichen ein");
                this.pwdCorrect = false;
            }
        }
    }

    public void login(View view) {
        uiThreadProvider = this;
        progressDialog = new ProgressDialog(LoginActivity.this);
        new Thread(() -> {
            this.uiThreadProvider.runOnUiThread(() -> {
                progressDialog.setMessage("Login-Daten werden überprüft...");
                progressDialog.show();

                new CountDownTimer(2000, 1000) {

                    public void onTick(long millisUntilFinished) {

                    }

                    public void onFinish() {
                        progressDialog.dismiss();
                    }
                }.start();

            });

            RetrofitRemoteToDoItemCRUDOperationsImpl retrofit = new RetrofitRemoteToDoItemCRUDOperationsImpl();
            boolean userAuthenticated = retrofit.authenticateUser(user);

            if (userAuthenticated) {
                startActivity(new Intent(this, MainActivity.class));
            } else {
                this.uiThreadProvider.runOnUiThread(() -> {
                    buttonWrapper.setError("Login fehlgeschlagen. Versuche es noch einmal.");
                });
            }
        }).start();
    }

}
