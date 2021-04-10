package com.android_studio.styler.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android_studio.styler.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.android_studio.styler.Util.showToast;

public class IntroActivity extends AppCompatActivity {
    private ConnectivityManager connectivityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        checkInternetState();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finishAffinity();
        System.runFinalization();
        System.exit(0);
    }

    private void checkInternetState() {
        assert connectivityManager != null;
        if (!(connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected())) {
            new AlertDialog.Builder(this)
                    .setMessage("인터넷과 연결되어 있지 않습니다.")
                    .setCancelable(false)
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finishAffinity();
                        }
                    }).show();
        } else {

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                myStartActivity(LoginActivity.class);
            } else {
                DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(user.getUid());
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                if (document.exists()) {
                                    showToast(IntroActivity.this, document.getData().get("name").toString() + "님이 접속 중");
                                    myStartActivity(MainActivity.class);
                                } else {
                                    myStartActivity(LoginActivity.class);
                                }
                            }
                        } else {
                            myStartActivity(LoginActivity.class);
                        }
                    }
                });
            }
        }
    }

    protected void myStartActivity(Class c) {
        finish();
        Intent intent = new Intent(this, c);
        startActivity(intent);
        this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
