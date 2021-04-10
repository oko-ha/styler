package com.android_studio.styler.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android_studio.styler.R;
import com.android_studio.styler.view.UserInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.android_studio.styler.Util.showToast;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private RelativeLayout loaderLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        loaderLayout = findViewById(R.id.loaderLayout);
        findViewById(R.id.btn_Register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Register();
            }
        });
    }

    private void Register() {
        String email = ((EditText) findViewById(R.id.et_Email)).getText().toString();
        String password = ((EditText) findViewById(R.id.et_Password)).getText().toString();
        String passwordCheck = ((EditText) findViewById(R.id.et_PasswordCheck)).getText().toString();
        final String name = ((EditText) findViewById(R.id.et_Name)).getText().toString();
        final String age = ((EditText) findViewById(R.id.et_Age)).getText().toString();
        RadioGroup rGroup = findViewById(R.id.rGroup_Sex);
        final String sex;
        switch (rGroup.getCheckedRadioButtonId()) {
            case R.id.rdo_Male:
                sex = "남성";
                break;
            case R.id.rdo_Female:
                sex = "여성";
                break;
            default:
                sex = "";
        }

        if (email.length() <= 0) {
            showToast(RegisterActivity.this, "이메일을 입력해주세요.");
        } else if (password.length() <= 0) {
            showToast(RegisterActivity.this, "비밀번호를 입력해주세요.");
        } else if (passwordCheck.length() <= 0) {
            showToast(RegisterActivity.this, "비밀번호 확인을 입력해주세요.");
        } else if (name.length() <= 0) {
            showToast(RegisterActivity.this, "이름을 입력해주세요.");
        } else if (age.length() <= 0) {
            showToast(RegisterActivity.this, "나이를 입력해주세요.");
        } else if (sex.length() <= 0) {
            showToast(RegisterActivity.this, "성별을 선택해주세요.");
        } else {
            if (password.equals(passwordCheck)) {
                final RelativeLayout loaderLayout = findViewById(R.id.loaderLayout);
                loaderLayout.setVisibility(View.VISIBLE);
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        profileUpdate(name, age, sex, user);
                                    } else {
                                        loaderLayout.setVisibility(View.GONE);
                                        showToast(RegisterActivity.this, "회원가입을 실패했습니다.");
                                    }
                                } else {
                                    loaderLayout.setVisibility(View.GONE);
                                    showToast(RegisterActivity.this, "회원가입을 실패했습니다.");
                                    Log.e(TAG, "로그 : ", task.getException());
                                }
                            }
                        });
            } else {
                showToast(RegisterActivity.this, "비밀번호가 일치하지 않습니다.");
            }
        }
    }

    private void profileUpdate(String name, String age, String sex, FirebaseUser user) {
        UserInfo userInfo = new UserInfo(name, age, sex);
        firebaseFirestore.collection("users").document(user.getUid()).set(userInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        loaderLayout.setVisibility(View.GONE);
                        showToast(RegisterActivity.this, "회원가입을 성공했습니다.");
                        myStartActivity(MainActivity.class);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loaderLayout.setVisibility(View.GONE);
                        showToast(RegisterActivity.this, "회원가입을 실패했습니다.");
                        Log.e(TAG, "로그 : ", e);
                    }
                });
    }

    public void onBackPressed() {
        myStartActivity(LoginActivity.class);
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        finish();
        startActivity(intent);
    }
}
