package com.android_studio.styler.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android_studio.styler.listener.Callback;
import com.android_studio.styler.R;
import com.android_studio.styler.listener.OnLoaderListener;
import com.android_studio.styler.view.CategoryInfo;
import com.android_studio.styler.view.PostInfo;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

import static com.android_studio.styler.Util.addList;
import static com.android_studio.styler.Util.showToast;
import static java.lang.Integer.parseInt;

public class Frag2 extends Fragment {
    private static final String TAG = "MainActivity-Frag2";
    private View view;
    private FirebaseAuth user;
    private FirebaseFirestore firebaseFirestore;
    private TextView categoryText1;
    private TextView categoryText2;
    private ImageView recommendView;
    private Button btn_Reco;
    private Button btn_NextReco;
    private ArrayList<PostInfo> allPostList;
    private ArrayList<PostInfo> topHeartList;
    private ArrayList<PostInfo> myHeartList;
    private ArrayList<PostInfo> allList;
    private ArrayList<CategoryInfo> categoryList;
    private ArrayList<CategoryInfo> categoryPoint;
    private ArrayList<CategoryInfo> tempPoint;
    private Callee callee;
    private int nextWork;
    private int heartSize;
    private String sex;
    private int[] onePick, twoPick;
    private int count;
    private final String[] recoID = {"shirt, blouse", "top, t-shirt, sweatshirt", "sweater", "cardigan", "jacket", "vest",
            "pants", "shorts", "skirt", "coat", "dress", "jumpsuit", "cape"};
    private OnLoaderListener onLoaderListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag2, container, false);

        user = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        categoryText1 = view.findViewById(R.id.categoryText1);
        categoryText2 = view.findViewById(R.id.categoryText2);
        recommendView = view.findViewById(R.id.recommendView);
        btn_Reco = view.findViewById(R.id.btn_Reco);
        btn_NextReco = view.findViewById(R.id.btn_NextReco);

        allPostList = new ArrayList<>();
        topHeartList = new ArrayList<>();
        myHeartList = new ArrayList<>();
        allList = new ArrayList<>();
        categoryList = new ArrayList<>();
        categoryPoint = new ArrayList<>();
        tempPoint = new ArrayList<>();

        callee = new Callee();

        findSex();
        categoryText1.setVisibility(View.GONE);
        categoryText2.setVisibility(View.GONE);

        btn_Reco.setOnClickListener(onClickListener);
        btn_NextReco.setOnClickListener(onClickListener);

        return view;
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_Reco:
                    if(sex != null){
                        callee.categoryInit(mCallback);
                    } else {
                        showToast(getActivity(), "잠시 후 다시 시도해주세요.");
                    }
                    break;
                case R.id.btn_NextReco:
                    onLoaderListener.loadingOn();
                    callee.duplicatePoint(mCallback);
                    break;
            }
        }
    };

    public void setOnLoaderListener(OnLoaderListener onLoaderListener) {
        this.onLoaderListener = onLoaderListener;
    }

    private void findSex() {
        firebaseFirestore.collection("users")
                .document(user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                sex = document.getData().get("sex").toString();
                            } else {
                                Log.e(TAG, "No such document");
                            }
                        } else {
                            Log.e(TAG, "get failed with ", task.getException());
                        }
                    }
                });
    }

    // Callback
    private Callback mCallback = new Callback() {
        @Override
        public void CallbackInit() {
            Log.d("로그", "Callback Init");
            callee.categoryAllPost(mCallback);
        }

        @Override
        public void CallbackAllPost() {
            Log.d("로그", "Callback AllPost");
            callee.categoryWeight(mCallback, allPostList);
        }

        @Override
        public void CallbackTopHeartPost() {
            Log.d("로그", "Callback TopHeartPost");
            callee.categoryWeight(mCallback, topHeartList);
        }

        @Override
        public void CallbackMyHeartPost() {
            Log.d("로그", "Callback MyHeartPost");
            callee.categoryWeight(mCallback, myHeartList);
        }

        @Override
        public void CallbackWeight() {
            Log.d("로그", "Callback Weight");
            callee.categoryPoint(mCallback);
        }

        @Override
        public void CallbackPoint() {
            Log.d("로그", "Callback Point");
            switch (nextWork) {
                // TopHeartPost
                case 1:
                    Collections.sort(allPostList);
                    callee.categoryTopHeartPost(mCallback);
                    break;
                // MyHeartPost
                case 2:
                    callee.categoryMyHeartPost(mCallback);
                    break;
                case 3:
                    callee.addAllPostList(mCallback);
                    break;
                // Default
                default:
                    Log.e("로그", "Next Work ERROR!");
            }
        }

        @Override
        public void CallbackAddAllPostList() {
            Log.d("로그", "Callback AddPostList");
            callee.addMyHeartList(mCallback);
        }

        @Override
        public void CallbackAddMyHeartList() {
            Log.d("로그", "Callback AddMyHeartList");
            Collections.sort(allList);
            callee.duplicatePoint(mCallback);
        }

        @Override
        public void CallbackDuplicatePoint() {
            Log.d("로그", "Callback DuplicatePoint");
            switch(count){
                case 0:
                    callee.recommendOutput(mCallback);
                    break;
                case 1:
                case 2:
                    callee.nextOutput(mCallback);
                    break;
                default:
                    Log.e("로그", "Count ERROR!");
            }
        }

        @Override
        public void CallbackOutput() {
            Log.d("로그", "Callback RecommendOutput");
            tempPoint.clear();
            onLoaderListener.loadingOff();
        }
    };

    // Callee
    private class Callee {
        // Initialize
        private void categoryInit(Callback mCallback) {
            Log.d("로그", "Callee categoryInit() Log");
            onLoaderListener.loadingOn();
            nextWork = 0;
            heartSize = 0;
            count = 0;
            allList.clear();
            allPostList.clear();
            topHeartList.clear();
            myHeartList.clear();
            categoryPoint.clear();
            for (int i = 0; i < 13; i++) {
                categoryPoint.add(new CategoryInfo(i, 0));
            }
            mCallback.CallbackInit();
        }

        // 모든 게시글
        private void categoryAllPost(final Callback mCallback) {
            Log.d("로그", "Callee categoryAllPost() Log");
            nextWork = 1;
            //Calendar cal = Calendar.getInstance();
            //cal.setTime(new Date());
            //cal.add(Calendar.DATE, -1);
            firebaseFirestore.collection("posts")
                    .whereEqualTo("sex", sex)
                    .orderBy("postedAt", Query.Direction.DESCENDING)
                    //.whereGreaterThan("postedAt", cal)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    addList(document, allPostList);
                                }
                                mCallback.CallbackAllPost();
                            } else {
                                Log.e(TAG, "Error getting document: ", task.getException());
                            }
                        }
                    });
        }

        // 모든 게시글 중 좋아요 상위 10%
        private void categoryTopHeartPost(Callback mCallback) {
            nextWork = 2;
            Log.d("로그", "Callee categoryTopHeartPost() Log");
            for (int i = 0; i < allPostList.size() / 10; i++) {
                topHeartList.add(allPostList.get(i));
            }
            mCallback.CallbackTopHeartPost();
        }

        // 내가 좋아요를 한 게시글
        private void categoryMyHeartPost(final Callback mCallback) {
            nextWork = 3;
            Log.d("로그", "Callee categoryMyHeartPost() Log");
            firebaseFirestore.collection("posts")
                    .whereArrayContains("heartID", user.getUid())
                    .whereEqualTo("sex", sex)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    addList(document, myHeartList);
                                }
                                mCallback.CallbackMyHeartPost();
                            } else {
                                Log.e(TAG, "Error getting document: ", task.getException());
                            }
                        }
                    });
        }

        // 가중치 측정
        private void categoryWeight(Callback mCallback, ArrayList<PostInfo> list) {
            Log.d("로그", "Callee categoryWeight() Log");
            categoryList.clear();
            ArrayList<String> tempList = new ArrayList<>();
            int[] category = new int[13];
            for (int i = 0; i < list.size(); i++) {
                tempList.clear();
                tempList.addAll(list.get(i).getCategory());
                for (int j = 0; j < tempList.size(); j++) {
                    category[parseInt(tempList.get(j))]++;
                }
            }
            for (int i = 0; i < 13; i++) {
                categoryList.add(new CategoryInfo(i, category[i]));
            }
            Collections.sort(categoryList);
            mCallback.CallbackWeight();
        }

        // 가중치에 따른 점수 측정
        private void categoryPoint(Callback mCallback) {
            Log.d("로그", "Callee categoryPoint() Log");
            int pointSize = 13;

            for (int i = 0; i < 13; i++) {
                if (categoryList.get(i).getCategoryCount() == 0) {
                    pointSize = i;
                    break;
                }
            }
            heartSize += pointSize;
            for (int i = 0; i < pointSize; i++) {
                int index = categoryList.get(i).getCategoryID();
                int beforeCategoryID = categoryPoint.get(index).getCategoryID();
                int beforeCategoryCount = categoryPoint.get(index).getCategoryCount();
                if (nextWork == 3) {
                    categoryPoint.set(index, new CategoryInfo(beforeCategoryID, beforeCategoryCount + heartSize - i));
                } else {
                    categoryPoint.set(index, new CategoryInfo(beforeCategoryID, beforeCategoryCount + pointSize - i));
                }
            }
            mCallback.CallbackPoint();
        }

        // 2개의 데이터 셋 종합
        private void addAllPostList(Callback mCallback) {
            Log.d("로그", "Callee addAllPostList() Log");
            allList.addAll(allPostList);
            mCallback.CallbackAddAllPostList();
        }

        private void addMyHeartList(Callback mCallback) {
            Log.d("로그", "Callee addMyHeartList() Log");
            allList.addAll(myHeartList);
            mCallback.CallbackAddMyHeartList();
        }

        private void duplicatePoint(Callback mCallback){
            Log.d("로그", "Callee duplicatePoint() Log");
            Collections.sort(categoryPoint);
            tempPoint.addAll(categoryPoint);
            mCallback.CallbackDuplicatePoint();
        }

        // 추천 결과
        private void recommendOutput(Callback mCallback) {
            Log.d("로그", "Callee recommendOutput() Log");
            boolean isWhole = false;
            boolean isLower = false;
            boolean noData = false;
            onePick = new int[3];
            twoPick = new int[3];

            onePick[count] = tempPoint.get(0).getCategoryID();

            categoryText1.setVisibility(View.VISIBLE);
            categoryText1.setText(recoID[onePick[count]]);
            switch (onePick[count]) {
                case 6:
                case 7:
                case 8:
                    isLower = true;
                    break;
                case 10:
                case 11:
                    isWhole = true;
                    break;
                default:
                    isLower = false;
            }

            if (!isWhole) {
                tempPoint.set(0, new CategoryInfo(tempPoint.get(0).getCategoryID(), 0));
                if (!isLower) {
                    for (int i = 0; i < 6; i++) {
                        for (int j = 0; j < 13; j++){
                            if(tempPoint.get(j).getCategoryID() == i){
                                tempPoint.set(j, new CategoryInfo(tempPoint.get(j).getCategoryID(), 0));
                                break;
                            }
                        }
                    }
                    for (int i = 9; i < 13; i++) {
                        for (int j = 0; j < 13; j++){
                            if(tempPoint.get(j).getCategoryID() == i){
                                tempPoint.set(j, new CategoryInfo(tempPoint.get(j).getCategoryID(), 0));
                                break;
                            }
                        }
                    }
                } else {
                    for (int i = 6; i < 9; i++) {
                        for (int j = 0; j < 13; j++){
                            if(tempPoint.get(j).getCategoryID() == i){
                                tempPoint.set(j, new CategoryInfo(tempPoint.get(j).getCategoryID(), 0));
                                break;
                            }
                        }
                    }
                }

                Collections.sort(tempPoint);
                twoPick[count] = tempPoint.get(0).getCategoryID();

                categoryText2.setVisibility(View.VISIBLE);
                categoryText2.setText(recoID[twoPick[count]]);

                for (int i = 0; i < allList.size(); i++) {
                    if (allList.get(i).getCategory().contains("" + onePick[count])) {
                        if (allList.get(i).getCategory().contains("" + twoPick[count])) {
                            Glide.with(getActivity()).load(allList.get(i).getContent()).override(1300).thumbnail(0.1f).into(recommendView);
                            break;
                        }
                        if (i == allList.size() - 1) {
                            noData = true;
                        }
                    }
                }
            } else {
                for (int i = 0; i < allList.size(); i++) {
                    if (allList.get(i).getCategory().contains("" + onePick[count])) {
                        Glide.with(getActivity()).load(allList.get(i).getContent()).override(1300).thumbnail(0.1f).into(recommendView);
                        break;
                    }
                    if (i == allList.size() - 1) {
                        noData = true;
                    }
                }
            }
            if (noData) {
                recommendView.setImageResource(R.drawable.alert);
            }
            count++;
            btn_Reco.setText("다시 추천");
            btn_NextReco.setVisibility(View.VISIBLE);
            mCallback.CallbackOutput();
        }

        // 다음 추천
        private void nextOutput(Callback mCallback) {
            Log.d("로그", "Callee nextOutput() Log");
            boolean isWhole = false;
            boolean isLower = false;
            boolean noData = false;

            if (count < 3) {
                onePick[count] = tempPoint.get(count).getCategoryID();

                categoryText1.setText(recoID[onePick[count]]);
                switch (onePick[count]) {
                    case 6:
                    case 7:
                    case 8:
                        isLower = true;
                        break;
                    case 10:
                    case 11:
                        isWhole = true;
                        break;
                    default:
                        isLower = false;
                }

                if (!isWhole) {
                    tempPoint.set(0, new CategoryInfo(tempPoint.get(0).getCategoryID(), 0));
                    if (!isLower) {
                        for (int i = 0; i < 6; i++) {
                            for (int j = 0; j < 13; j++){
                                if(tempPoint.get(j).getCategoryID() == i){
                                    tempPoint.set(j, new CategoryInfo(tempPoint.get(j).getCategoryID(), 0));
                                    break;
                                }
                            }
                        }
                        for (int i = 9; i < 13; i++) {
                            for (int j = 0; j < 13; j++){
                                if(tempPoint.get(j).getCategoryID() == i){
                                    tempPoint.set(j, new CategoryInfo(tempPoint.get(j).getCategoryID(), 0));
                                    break;
                                }
                            }
                        }
                    } else {
                        for (int i = 6; i < 9; i++) {
                            for (int j = 0; j < 13; j++){
                                if(tempPoint.get(j).getCategoryID() == i){
                                    tempPoint.set(j, new CategoryInfo(tempPoint.get(j).getCategoryID(), 0));
                                    break;
                                }
                            }
                        }
                    }
                    Collections.sort(tempPoint);
                    twoPick[count] = tempPoint.get(0).getCategoryID();

                    categoryText2.setText(recoID[twoPick[count]]);

                    for (int i = 0; i < allList.size(); i++) {
                        if (allList.get(i).getCategory().contains("" + onePick[count])) {
                            if (allList.get(i).getCategory().contains("" + twoPick[count])) {
                                Glide.with(getActivity()).load(allList.get(i).getContent()).override(1300).thumbnail(0.1f).into(recommendView);
                                break;
                            }
                            if (i == allList.size() - 1) {
                                noData = true;
                            }
                        }
                    }
                } else {
                    for (int i = 0; i < allList.size(); i++) {
                        if (allList.get(i).getCategory().contains("" + onePick[count])) {
                            Glide.with(getActivity()).load(allList.get(i).getContent()).override(1300).thumbnail(0.1f).into(recommendView);
                            break;
                        }
                        if (i == allList.size() - 1) {
                            noData = true;
                        }
                    }
                }
                if (noData) {
                    recommendView.setImageResource(R.drawable.alert);
                }
                count++;
                if(count >= 3){
                    btn_NextReco.setVisibility(View.GONE);
                }

                mCallback.CallbackOutput();
            }
        }
    }
}

