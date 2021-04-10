package com.android_studio.styler;

import android.app.Activity;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android_studio.styler.view.PostInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;

public class Util {

    public static void showToast(Activity activity, String msg) {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
    }

    public static boolean isStorageUrl(String url) {
        return Patterns.WEB_URL.matcher(url).matches() &&
                url.contains("https://firebasestorage.googleapis.com/v0/b/styler-30f29.appspot.com/o/posts");
    }

    public static String UrlToExtension(String url) {
        return url.split("\\.")[url.split("\\.").length - 1];
    }

    public static void addList(QueryDocumentSnapshot document, ArrayList<PostInfo> list) {
            list.add(new PostInfo(
                    document.getData().get("name").toString(),
                    document.getData().get("content").toString(),
                    document.getData().get("extension").toString(),
                    (ArrayList<String>) document.getData().get("hashtag"),
                    document.getData().get("publisher").toString(),
                    new Date(document.getDate("postedAt").getTime()),
                    document.getId(),
                    (ArrayList<String>) document.getData().get("heartID"),
                    Integer.parseInt(document.getData().get("heartCount").toString()),
                    (ArrayList<String>) document.getData().get("category"),
                    document.getData().get("sex").toString(),
                    document.getData().get("age").toString()
            ));
    }
}
