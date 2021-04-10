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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import static com.android_studio.styler.Util.UrlToExtension;
import static com.android_studio.styler.Util.isStorageUrl;
import static com.android_studio.styler.Util.showToast;

public class EditPostActivity extends AppCompatActivity {
    private PostInfo postInfo;

    private RelativeLayout loaderLayout;

    private String postID;
    private StorageReference storageRef;
    private FirebaseFirestore firebaseFirestore;

    private ImageView contentView;

    private ArrayList<String> hashtag = new ArrayList<>();
    private int tagCount;
    private CheckBox tagDaily, tagCasual, tagFormal, tagRetro, tagDate, tagStreet, tagDandy, tagLovely, tagModern;

    private String imagePath;

    private boolean contentChange = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("게시글 수정");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.before);

        postInfo = (PostInfo) getIntent().getSerializableExtra("postInfo");

        loaderLayout = findViewById(R.id.loaderLayout);

        postID = postInfo.getPostID();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        contentView = findViewById(R.id.contentView);
        contentView.setOnClickListener(onClickListener);

        tagDaily = findViewById(R.id.cb_Daily);
        tagCasual = findViewById(R.id.cb_Casual);
        tagFormal = findViewById(R.id.cb_Formal);
        tagRetro = findViewById(R.id.cb_Retro);
        tagDate = findViewById(R.id.cb_Date);
        tagStreet = findViewById(R.id.cb_Street);
        tagDandy = findViewById(R.id.cb_Dandy);
        tagLovely = findViewById(R.id.cb_Lovely);
        tagModern = findViewById(R.id.cb_Modern);

        infoInit(postInfo);

        findViewById(R.id.btn_Post).setOnClickListener(onClickListener);

        tagDaily.setOnClickListener(onClickListener);
        tagCasual.setOnClickListener(onClickListener);
        tagFormal.setOnClickListener(onClickListener);
        tagRetro.setOnClickListener(onClickListener);
        tagDate.setOnClickListener(onClickListener);
        tagStreet.setOnClickListener(onClickListener);
        tagDandy.setOnClickListener(onClickListener);
        tagLovely.setOnClickListener(onClickListener);
        tagModern.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_Post:
                    storageUpdate();
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
                case R.id.contentView:
                    myStartActivity(GalleryActivity.class);
                    break;
            }
        }
    };

    private void infoInit(PostInfo postInfo) {
        Glide.with(this).load(postInfo.getContent()).override(1280).into(contentView);

        hashtag = postInfo.getHashtag();
        tagCount = hashtag.size();
        for (int i = 0; i < hashtag.size(); i++) {
            if (tagDaily.getText().toString().equals(hashtag.get(i))) {
                tagDaily.setChecked(true);
            } else if (tagCasual.getText().toString().equals(hashtag.get(i))) {
                tagCasual.setChecked(true);
            } else if (tagFormal.getText().toString().equals(hashtag.get(i))) {
                tagFormal.setChecked(true);
            } else if (tagRetro.getText().toString().equals(hashtag.get(i))) {
                tagRetro.setChecked(true);
            } else if (tagDate.getText().toString().equals(hashtag.get(i))) {
                tagDate.setChecked(true);
            } else if (tagStreet.getText().toString().equals(hashtag.get(i))) {
                tagStreet.setChecked(true);
            } else if (tagDandy.getText().toString().equals(hashtag.get(i))) {
                tagDandy.setChecked(true);
            } else if (tagLovely.getText().toString().equals(hashtag.get(i))) {
                tagLovely.setChecked(true);
            } else if (tagModern.getText().toString().equals(hashtag.get(i))) {
                tagModern.setChecked(true);
            }
        }
    }

    private void hashtagging(int id) {
        CheckBox checkBox = findViewById(id);
        if (checkBox.isChecked()) {
            if (tagCount >= 3) {
                showToast(EditPostActivity.this, "태그는 3개까지 선택 가능합니다.");
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


    private void storageUpdate() {
        if (tagCount > 0) {
            loaderLayout.setVisibility(View.VISIBLE);

            final String beforeContent = postInfo.getContent();
            final String beforeExtension = postInfo.getExtension();
            if (contentChange) {
                // 이전 이미지 Storage Data 삭제
                if (isStorageUrl(beforeContent)) {
                    storageRef.child("posts/" + postID + "." + beforeExtension).delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    // 새로운 이미지 Storage Data 추가
                                    final String extension = UrlToExtension(imagePath);
                                    final StorageReference mountainImageRef = storageRef.child("posts/" + postID + "." + extension);
                                    try {
                                        InputStream stream = new FileInputStream(new File(imagePath));
                                        UploadTask uploadTask = mountainImageRef.putStream(stream);
                                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                mountainImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        String content = uri.toString();
                                                        contentUpdate(content, extension);
                                                    }
                                                });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                showToast(EditPostActivity.this, "게시글을 수정하지 못했습니다.");
                                                finish();
                                            }
                                        });
                                    } catch (FileNotFoundException e) {
                                        showToast(EditPostActivity.this, "게시글을 수정하지 못했습니다.");
                                        finish();
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    showToast(EditPostActivity.this, "게시글을 수정하지 못했습니다.");
                                    finish();
                                }
                            });
                }
            } else {
                hashtagUpdate();
            }
        } else {
            showToast(EditPostActivity.this, "태그를 1개 이상 선택하세요.");
        }
    }

    private void contentUpdate(String content, String extension) {
        firebaseFirestore.collection("posts").document(postID)
                .update(
                        "content", content,
                        "extension", extension,
                        "hashtag", hashtag
                )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showToast(EditPostActivity.this, "게시글을 수정했습니다.");
                        loaderLayout.setVisibility(View.GONE);
                        setResult(Activity.RESULT_OK);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast(EditPostActivity.this, "게시글을 수정하지 못했습니다.");
                        finish();
                    }
                });
    }

    private void hashtagUpdate() {
        firebaseFirestore.collection("posts").document(postID)
                .update("hashtag", hashtag)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showToast(EditPostActivity.this, "게시글을 수정했습니다.");
                        setResult(Activity.RESULT_OK);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast(EditPostActivity.this, "게시글을 수정하지 못했습니다.");
                        finish();
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    contentChange = true;
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

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivityForResult(intent, 1);
    }
}
