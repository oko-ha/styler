package com.android_studio.styler.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android_studio.styler.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class VersionActivity extends AppCompatActivity {
    private final String currentVersion = "1.0";
    private String newestVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("내 게시글");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.before);

        // 최신 버전 확인
        FirebaseFirestore.getInstance().collection("version").document("newest").get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            newestVersion = task.getResult().getData().get("version").toString();
                        }
                        TextView newestView = findViewById(R.id.newestView);
                        if (currentVersion.equals(newestVersion)) {
                            newestView.setText("최신 버전을 사용 중입니다.");
                        } else {
                            String newestText = "최신 버전 " + newestVersion + "v";
                            newestView.setText(newestText);
                        }
                    }
                });

        TextView currentView = findViewById(R.id.currentView);
        String currentText = "현재 버전 " + currentVersion + "v";
        currentView.setText(currentText);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home :
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
