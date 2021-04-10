package com.android_studio.styler.activity;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.android_studio.styler.R;
import com.android_studio.styler.adapter.PostAdapter;
import com.android_studio.styler.listener.OnPostListener;
import com.android_studio.styler.view.PostInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;

import static com.android_studio.styler.Util.addList;
import static com.android_studio.styler.Util.isStorageUrl;
import static com.android_studio.styler.Util.showToast;

public class MyPostActivity extends AppCompatActivity {
    private static final String TAG = "MyPostActivity";
    private FirebaseAuth user;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageRef;
    private RecyclerView recyclerView;
    private RelativeLayout updateLayout;
    private PostAdapter postAdapter;
    private ArrayList<PostInfo> postList;
    private boolean heartUpdate = false;
    private boolean heartFlag;
    private boolean updateFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_post);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("내 게시글");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.before);

        user = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        recyclerView = findViewById(R.id.recyclerPost);
        updateLayout = findViewById(R.id.updateLayout);

        postList = new ArrayList<>();
        postAdapter = new PostAdapter(MyPostActivity.this, postList);
        postAdapter.setOnPostListener(onPostListener);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MyPostActivity.this));
        recyclerView.setAdapter(postAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();

                if (totalItemCount != 0 && totalItemCount - 5 <= lastVisibleItemPosition && !updateFlag) {
                    postAdd();
                }
            }
        });
        postUpdate();
    }

    private OnPostListener onPostListener = new OnPostListener() {
        @Override
        public void onDelete(final int position) {
            final String postID = postList.get(position).getPostID();
            String content = postList.get(position).getContent();
            String extension = postList.get(position).getExtension();

            if (isStorageUrl(content)) {
                updateLayout.setVisibility(View.VISIBLE);
                storageRef.child("posts/" + postID + "." + extension).delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                firebaseFirestore.collection("posts").document(postID).delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                showToast(MyPostActivity.this, "게시글을 삭제했습니다.");
                                                postList.remove(position);
                                                postAdapter.notifyDataSetChanged();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                showToast(MyPostActivity.this, "게시글을 삭제하지 못했습니다.");
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                showToast(MyPostActivity.this, "게시글을 삭제하지 못했습니다.");
                            }
                        });
                updateLayout.setVisibility(View.GONE);
            } else {
                showToast(MyPostActivity.this, "게시글을 삭제하지 못했습니다.");
            }
        }

        @Override
        public void onModify(int position) {
            Intent intent = new Intent(MyPostActivity.this, EditPostActivity.class);
            intent.putExtra("postInfo", postList.get(position));
            startActivityForResult(intent, 0);
        }

        @Override
        public void onHeart(int position) {
            if (!heartUpdate) {
                heartUpdate = true;
                String postID = postList.get(position).getPostID();
                ArrayList<String> heartID = postList.get(position).getHeartID();
                int heartCount = heartID.size();
                heartFlag = true;
                for (int i = 0; i < heartID.size(); i++) {
                    if (heartID.get(i).equals(user.getUid())) {
                        heartFlag = false;
                        heartID.remove(i);
                        heartCount--;
                        break;
                    }
                }
                if (heartFlag) {
                    heartCount++;
                    heartID.add(user.getUid());
                }

                firebaseFirestore.collection("posts").document(postID)
                        .update(
                                "heartCount", heartCount,
                                "heartID", heartID
                        )
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                postAdapter.notifyDataSetChanged();
                                heartUpdate = false;
                                if (heartFlag) {
                                    final RelativeLayout heartLayout = findViewById(R.id.heartLayout);
                                    final LottieAnimationView heartLottie = findViewById(R.id.heartLottie);
                                    heartLayout.setVisibility(View.VISIBLE);
                                    heartLottie.setVisibility(View.VISIBLE);
                                    heartLottie.playAnimation();
                                    heartLottie.addAnimatorListener(new Animator.AnimatorListener() {
                                        @Override
                                        public void onAnimationStart(Animator animator) {
                                        }

                                        @Override
                                        public void onAnimationEnd(Animator animator) {
                                            heartLottie.setVisibility(View.GONE);
                                            heartLayout.setVisibility(View.GONE);
                                        }

                                        @Override
                                        public void onAnimationCancel(Animator animator) {
                                        }

                                        @Override
                                        public void onAnimationRepeat(Animator animator) {
                                        }
                                    });
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                heartUpdate = false;
                            }
                        });
            }
        }
    };

    private void postUpdate() {
        if (!updateFlag) {
            updateFlag = true;
            updateLayout.setVisibility(View.VISIBLE);
            final ArrayList<PostInfo> tempList = new ArrayList<>();
            firebaseFirestore.collection("posts")
                    .whereEqualTo("publisher", user.getUid())
                    .orderBy("postedAt", Query.Direction.DESCENDING)
                    .whereLessThan("postedAt", new Date()).limit(10)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    addList(document, tempList);
                                    postAdapter.notifyDataSetChanged();
                                }
                                postList.clear();
                                postList.addAll(tempList);
                            } else {
                                Log.e(TAG, "Error getting document: ", task.getException());
                            }
                            updateFlag = false;
                            updateLayout.setVisibility(View.GONE);
                        }
                    });
        }
    }

    private void postAdd() {
        if (!updateFlag) {
            updateFlag = true;
            firebaseFirestore.collection("posts")
                    .whereEqualTo("publisher", user.getUid())
                    .orderBy("postedAt", Query.Direction.DESCENDING)
                    .whereLessThan("postedAt", postList.get(postList.size() - 1).getPostedAt()).limit(10)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    addList(document, postList);
                                    postAdapter.notifyDataSetChanged();
                                }
                            } else {
                                Log.e(TAG, "Error getting document: ", task.getException());
                            }
                            updateFlag = false;
                        }
                    });
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
}
