package com.android_studio.styler.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.android_studio.styler.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import static com.android_studio.styler.Util.showToast;

public class LoginActivity extends BasicActivity {
    private static final String TAG = "LoginActivity";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        findViewById(R.id.btn_Register).setOnClickListener(onClickListener);
        findViewById(R.id.btn_Login).setOnClickListener(onClickListener);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_Login:
                    Login();
                    break;
                case R.id.btn_Register:
                    myStartActivity(RegisterActivity.class);
                    break;
            }
        }
    };

    private void Login() {
        String email = ((EditText) findViewById(R.id.et_Email)).getText().toString();
        String password = ((EditText) findViewById(R.id.et_Password)).getText().toString();

        if (email.length() <= 0) {
            showToast(LoginActivity.this, "이메일을 입력해주세요.");
        } else if (password.length() <= 0) {
            showToast(LoginActivity.this, "비밀번호를 입력해주세요.");
        } else {
            final RelativeLayout loaderLayout = findViewById(R.id.loaderLayout);
            loaderLayout.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            loaderLayout.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                showToast(LoginActivity.this, "로그인에 성공했습니다.");
                                myStartActivity(MainActivity.class);
                            } else {
                                showToast(LoginActivity.this, "로그인에 실패했습니다.");
                                Log.e(TAG, "로그 : ", task.getException());
                            }
                        }
                    });
        }
    }
}
