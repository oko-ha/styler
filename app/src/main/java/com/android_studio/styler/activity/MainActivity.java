package com.android_studio.styler.activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.android_studio.styler.R;
import com.android_studio.styler.fragment.Frag1;
import com.android_studio.styler.fragment.Frag2;
import com.android_studio.styler.fragment.Frag3;
import com.android_studio.styler.fragment.Frag4;
import com.android_studio.styler.fragment.Frag5;
import com.android_studio.styler.listener.OnLoaderListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends BasicActivity {
    private Frag1 frag1;
    private Frag2 frag2;
    private Frag3 frag3;
    private Frag4 frag4;
    private Frag5 frag5;
    private int fragPosition = 0;
    private RelativeLayout loaderLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView;
        loaderLayout = findViewById(R.id.loaderLayout);

        frag1 = new Frag1();
        frag2 = new Frag2();
        frag3 = new Frag3();
        frag4 = new Frag4();
        frag5 = new Frag5();
        setFrag(0); // 첫 프레그먼트 화면을 지정

        bottomNavigationView = findViewById(R.id.bottom_Navi);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_home:
                        setFrag(0);
                        if (fragPosition == 0) {
                            onFrag(0);
                        }
                        fragPosition = 0;
                        break;
                    case R.id.action_recommend:
                        setFrag(1);
                        if (fragPosition == 1) {
                            onFrag(1);
                        }
                        fragPosition = 1;
                        break;
                    case R.id.action_explore:
                        setFrag(2);
                        if (fragPosition == 2) {
                            onFrag(2);
                        }
                        fragPosition = 2;
                        break;
                    case R.id.action_favorite:
                        setFrag(3);
                        if (fragPosition == 3) {
                            onFrag(3);
                        }
                        fragPosition = 3;
                        break;
                    case R.id.action_menu:
                        setFrag(4);
                        if (fragPosition == 4) {
                            onFrag(4);
                        }
                        fragPosition = 4;
                        break;
                }

                return true;
            }
        });

        frag2.setOnLoaderListener(onLoaderListener);
        frag5.setOnLoaderListener(onLoaderListener);
    }


    private OnLoaderListener onLoaderListener = new OnLoaderListener() {
        @Override
        public void loadingOn() {
            loaderLayout.setVisibility(View.VISIBLE);
        }
        public void loadingOff() {
            loaderLayout.setVisibility(View.GONE);
        }
    };

    // 프레그먼트 교체
    private void setFrag(int n) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        switch (n) {
            case 0:
                ft.replace(R.id.main_frame, frag1);
                ft.commit();
                break;
            case 1:
                ft.replace(R.id.main_frame, frag2);
                ft.commit();
                break;
            case 2:
                ft.replace(R.id.main_frame, frag3);
                ft.commit();
                break;
            case 3:
                ft.replace(R.id.main_frame, frag4);
                ft.commit();
                break;
            case 4:
                ft.replace(R.id.main_frame, frag5);
                ft.commit();
                break;
        }
    }

    private void onFrag(int n) {
        switch (n) {
            case 0:
                frag1.scrollupUpdate();
                break;
            case 1:
                //
                break;
            case 2:
                frag3.scrollupUpdate();
                break;
            case 3:
                frag4.scrollupUpdate();
                break;
            case 4:
                //
        }
    }
}
