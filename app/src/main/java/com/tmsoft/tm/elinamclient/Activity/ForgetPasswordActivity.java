package com.tmsoft.tm.elinamclient.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.tmsoft.tm.elinamclient.R;

public class ForgetPasswordActivity extends AppCompatActivity {

    private EditText email;
    private TextView signUp;
    private Button btn;
    private ProgressDialog progressDialog;
    private Dialog dialog;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        email = findViewById(R.id.forgetPassword_emailAddress);
        signUp = findViewById(R.id.forgetPassword_Register);
        btn = findViewById(R.id.btn_resetPassword);
        progressDialog = new ProgressDialog(this);
        dialog = new Dialog(this, R.style.Theme_CustomDialog);

        mAuth = FirebaseAuth.getInstance();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String em = email.getText().toString();

                if (TextUtils.isEmpty(em))
                    Toast.makeText(ForgetPasswordActivity.this, "Enter your email address to reset password or Sign Up", Toast.LENGTH_SHORT).show();
                else
                    resetPassword(em);
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToSignUpActivity();
            }
        });
    }

    private void resetPassword(String em) {
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.show();
        mAuth.sendPasswordResetEmail(em).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    showForgetPasswordDialog();
                    Toast.makeText(ForgetPasswordActivity.this, "We send a link to your email. Please click to reset your password", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                } else {
                    String err = task.getException().getMessage();
                    Toast.makeText(ForgetPasswordActivity.this, "Failed to send email\n" + err + "\nPlease try again...", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                    sendUserToLogInActivity();
                }
            }
        });
    }

    private void showForgetPasswordDialog() {
        final Button ok;

        dialog.setContentView(R.layout.dialog_forget_password_send);
        ok = dialog.findViewById(R.id.forgetpassword_ok);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                sendUserToLogInActivity();
            }
        });

        dialog.show();
    }

    private void sendUserToLogInActivity() {
        Intent intent = new Intent(ForgetPasswordActivity.this, LogInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    private void sendUserToSignUpActivity() {
        Intent intent = new Intent (ForgetPasswordActivity.this, RegisterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
