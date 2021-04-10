package com.android_studio.styler.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android_studio.styler.R;
import com.android_studio.styler.view.PostInfo;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import static com.android_studio.styler.Util.UrlToExtension;
import static com.android_studio.styler.Util.showToast;

public class AddPostActivity extends AppCompatActivity {
    private RelativeLayout loaderLayout;

    private String name;
    private String sex;
    private String age;
    private ImageView contentView;

    private ArrayList<String> hashtag = new ArrayList<>();
    private int tagCount;

    private FirebaseUser user;
    private String imagePath;

    private ArrayList<String> category = new ArrayList<>();
    private int categoryCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("게시글 작성");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.before);

        loaderLayout = findViewById(R.id.loaderLayout);

        contentView = findViewById(R.id.contentView);
        contentView.setOnClickListener(onClickListener);

        findViewById(R.id.btn_Gallery).setOnClickListener(onClickListener);
        findViewById(R.id.btn_Post).setOnClickListener(onClickListener);

        findViewById(R.id.cb_Daily).setOnClickListener(onClickListener);
        findViewById(R.id.cb_Casual).setOnClickListener(onClickListener);
        findViewById(R.id.cb_Formal).setOnClickListener(onClickListener);
        findViewById(R.id.cb_Retro).setOnClickListener(onClickListener);
        findViewById(R.id.cb_Date).setOnClickListener(onClickListener);
        findViewById(R.id.cb_Street).setOnClickListener(onClickListener);
        findViewById(R.id.cb_Dandy).setOnClickListener(onClickListener);
        findViewById(R.id.cb_Lovely).setOnClickListener(onClickListener);
        findViewById(R.id.cb_Modern).setOnClickListener(onClickListener);

        findViewById(R.id.id0).setOnClickListener(onClickListener);
        findViewById(R.id.id1).setOnClickListener(onClickListener);
        findViewById(R.id.id2).setOnClickListener(onClickListener);
        findViewById(R.id.id3).setOnClickListener(onClickListener);
        findViewById(R.id.id4).setOnClickListener(onClickListener);
        findViewById(R.id.id5).setOnClickListener(onClickListener);
        findViewById(R.id.id6).setOnClickListener(onClickListener);
        findViewById(R.id.id7).setOnClickListener(onClickListener);
        findViewById(R.id.id8).setOnClickListener(onClickListener);
        findViewById(R.id.id9).setOnClickListener(onClickListener);
        findViewById(R.id.id10).setOnClickListener(onClickListener);
        findViewById(R.id.id11).setOnClickListener(onClickListener);
        findViewById(R.id.id12).setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_Gallery:
                    myStartActivity(GalleryActivity.class, 0);
                    break;
                case R.id.btn_Post:
                    storageUpload();
                    break;
                // check box click
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
                case R.id.contentView:
                    myStartActivity(GalleryActivity.class, 1);
                    break;

                //category
                case R.id.id0:
                    onCategory(R.id.id0);
                    break;
                case R.id.id1:
                    onCategory(R.id.id1);
                    break;
                case R.id.id2:
                    onCategory(R.id.id2);
                    break;
                case R.id.id3:
                    onCategory(R.id.id3);
                    break;
                case R.id.id4:
                    onCategory(R.id.id4);
                    break;
                case R.id.id5:
                    onCategory(R.id.id5);
                    break;
                case R.id.id6:
                    onCategory(R.id.id6);
                    break;
                case R.id.id7:
                    onCategory(R.id.id7);
                    break;
                case R.id.id8:
                    onCategory(R.id.id8);
                    break;
                case R.id.id9:
                    onCategory(R.id.id9);
                    break;
                case R.id.id10:
                    onCategory(R.id.id10);
                    break;
                case R.id.id11:
                    onCategory(R.id.id11);
                    break;
                case R.id.id12:
                    onCategory(R.id.id12);
                    break;
            }
        }
    };

    //check box click method
    private void hashtagging(int id) {
        CheckBox checkBox = findViewById(id);
        if (checkBox.isChecked()) {
            if (tagCount >= 3) {
                showToast(AddPostActivity.this, "태그는 3개까지 선택 가능합니다.");
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

    // category method
    private void onCategory(int id) {
        CheckBox checkBox = findViewById(id);
        if (checkBox.isChecked()) {
            category.add(checkBox.getText().toString());
            categoryCount++;
        } else {
            category.remove(checkBox.getText().toString());
            categoryCount--;
        }
    }

    //image 파일 firebase storage에 upload
    private void storageUpload() {
        if (imagePath != null) {
            if (tagCount > 0) {
                if (categoryCount > 0) {
                    loaderLayout.setVisibility(View.VISIBLE);

                    user = FirebaseAuth.getInstance().getCurrentUser();
                    final FirebaseStorage storage = FirebaseStorage.getInstance();
                    final StorageReference storageRef = storage.getReference();
                    final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

                    firebaseFirestore.collection("users").document(user.getUid()).get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        name = task.getResult().getData().get("name").toString();
                                        sex = task.getResult().getData().get("sex").toString();
                                        age = task.getResult().getData().get("age").toString();

                                        final DocumentReference documentReference = firebaseFirestore.collection("posts").document();
                                        final String postID = documentReference.getId();
                                        final String extension = UrlToExtension(imagePath);
                                        final StorageReference mountainImageRef =
                                                storageRef.child("posts/" + postID + "." + extension);
                                        try {
                                            InputStream stream = new FileInputStream(new File(imagePath));
                                            UploadTask uploadTask = mountainImageRef.putStream(stream);
                                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    loaderLayout.setVisibility(View.GONE);
                                                    showToast(AddPostActivity.this, "게시글을 업로드하지 못했습니다.");
                                                }
                                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    mountainImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri uri) {
                                                            String content = uri.toString();
                                                            ArrayList<String> arrlist = new ArrayList<>();
                                                            PostInfo postInfo = new PostInfo(name, content, extension, hashtag, user.getUid(), new Date(), postID, arrlist, 0, category, sex, age);
                                                            storeUpload(documentReference, postInfo);
                                                        }
                                                    });
                                                }
                                            });
                                        } catch (FileNotFoundException e) {
                                            showToast(AddPostActivity.this, "게시글을 업로드하지 못했습니다.");
                                            loaderLayout.setVisibility(View.GONE);
                                            finish();
                                        }
                                    }
                                }
                            });
                } else {
                    showToast(AddPostActivity.this, "카테고리를 1개 이상 선택하세요.");
                }
            } else {
                showToast(AddPostActivity.this, "태그를 1개 이상 선택하세요.");
            }
        } else {
            showToast(AddPostActivity.this, "먼저 사진을 선택하세요.");
        }
    }

    //database store에 upload
    private void storeUpload(DocumentReference documentReference, PostInfo postInfo) {
        documentReference.set(postInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showToast(AddPostActivity.this, "게시글을 업로드 했습니다.");
                        setResult(Activity.RESULT_OK);
                        loaderLayout.setVisibility(View.GONE);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast(AddPostActivity.this, "게시글을 업로드하지 못했습니다.");
                        loaderLayout.setVisibility(View.GONE);
                        finish();
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == Activity.RESULT_OK) {
                    imagePath = data.getStringExtra("image");

                    contentView.setVisibility(View.VISIBLE);
                    Glide.with(this).load(imagePath).override(1280).into(contentView);
                    findViewById(R.id.hashtagRow).setVisibility(View.VISIBLE);
                    findViewById(R.id.categoryRow).setVisibility(View.VISIBLE);
                    findViewById(R.id.btn_Gallery).setVisibility(View.GONE);
                }
                break;
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    imagePath = data.getStringExtra("image");
                    Glide.with(this).load(imagePath).override(1280).into(contentView);
                }
                break;
        }
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

    private void myStartActivity(Class c, int requestCode) {
        Intent intent = new Intent(this, c);
        startActivityForResult(intent, requestCode);
    }
}
