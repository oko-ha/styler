package com.android_studio.styler.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;

import com.android_studio.styler.R;

import java.util.ArrayList;

import static com.android_studio.styler.Util.showToast;

public class SearchActivity extends Activity {
    private int tagCount;
    private ArrayList<String> hashtag = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search);

        findViewById(R.id.btn_Cancel).setOnClickListener(onClickListener);
        findViewById(R.id.btn_OK).setOnClickListener(onClickListener);

        findViewById(R.id.cb_Daily).setOnClickListener(onClickListener);
        findViewById(R.id.cb_Casual).setOnClickListener(onClickListener);
        findViewById(R.id.cb_Formal).setOnClickListener(onClickListener);
        findViewById(R.id.cb_Retro).setOnClickListener(onClickListener);
        findViewById(R.id.cb_Date).setOnClickListener(onClickListener);
        findViewById(R.id.cb_Street).setOnClickListener(onClickListener);
        findViewById(R.id.cb_Dandy).setOnClickListener(onClickListener);
        findViewById(R.id.cb_Lovely).setOnClickListener(onClickListener);
        findViewById(R.id.cb_Modern).setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_Cancel:
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    break;
                case R.id.btn_OK:
                    if (tagCount > 0) {
                        Intent intent = new Intent();
                        intent.putExtra("hashtag", hashtag);
                        setResult(RESULT_OK, intent);
                        finish();
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    } else {
                        showToast(SearchActivity.this, "태그를 1개 이상 선택하세요.");
                    }
                    break;

                case R.id.cb_Daily:
                    hashtagging(R.id.cb_Daily);
                    break;
                case R.id.cb_Casual:
                    hashtagging(R.id.cb_Casual);
                    break;
                case R.id.cb_Formal:
                    hashtagging(R.id.cb_Formal);
                    break;
                case R.id.cb_Retro:
                    hashtagging(R.id.cb_Retro);
                    break;
                case R.id.cb_Date:
                    hashtagging(R.id.cb_Date);
                    break;
                case R.id.cb_Street:
                    hashtagging(R.id.cb_Street);
                    break;
                case R.id.cb_Dandy:
                    hashtagging(R.id.cb_Dandy);
                    break;
                case R.id.cb_Lovely:
                    hashtagging(R.id.cb_Lovely);
                    break;
                case R.id.cb_Modern:
                    hashtagging(R.id.cb_Modern);
                    break;
            }
        }
    };

    private void hashtagging(int id) {
        CheckBox checkBox = findViewById(id);
        if (checkBox.isChecked()) {
            if (tagCount >= 3) {
                showToast(SearchActivity.this, "태그는 3개까지 선택 가능합니다.");
                checkBox.setChecked(false);
            } else {
                hashtag.add(checkBox.getText().toString());
                tagCount++;
            }
        } else {
            hashtag.remove(checkBox.getText().toString());
            tagCount--;
        }
    }

    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
            return event.getAction()!=MotionEvent.ACTION_OUTSIDE;
    }
}