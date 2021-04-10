package com.android_studio.styler.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android_studio.styler.R;
import com.android_studio.styler.activity.LoginActivity;
import com.android_studio.styler.activity.MyPostActivity;
import com.android_studio.styler.activity.VersionActivity;
import com.android_studio.styler.listener.OnLoaderListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import static com.android_studio.styler.Util.isStorageUrl;
import static com.android_studio.styler.Util.showToast;

public class Frag5 extends Fragment {
    private static final String TAG = "MainActivity-Frag5";
    private View view;
    private FirebaseAuth user;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageRef;
    private OnLoaderListener onLoaderListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag5, container, false);

        user = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        view.findViewById(R.id.btn_Version).setOnClickListener(onClickListener);
        view.findViewById(R.id.btn_MyPost).setOnClickListener(onClickListener);
        view.findViewById(R.id.btn_Logout).setOnClickListener(onClickListener);
        view.findViewById(R.id.btn_Delete).setOnClickListener(onClickListener);

        return view;
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_Version:
                    myStartActivity(VersionActivity.class);
                    break;
                case R.id.btn_MyPost:
                    myStartActivity(MyPostActivity.class);
                    break;
                case R.id.btn_Logout:
                    FirebaseAuth.getInstance().signOut();
                    closeActivity();
                    break;
                case R.id.btn_Delete:
                    AlertDialog.Builder alert_confirm = new AlertDialog.Builder(getActivity());
                    alert_confirm.setMessage("정말 계정을 삭제할까요?\n(사용자 정보와 게시글이 모두 삭제됩니다.)").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    deletePost();
                                }
                            }
                    );
                    alert_confirm.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //
                        }
                    });
                    alert_confirm.show();
                    break;

            }
        }
    };

    public void setOnLoaderListener(OnLoaderListener onLoaderListener) {
        this.onLoaderListener = onLoaderListener;
    }

    private void deletePost() {
        onLoaderListener.loadingOn();
        firebaseFirestore.collection("posts")
                .whereEqualTo("publisher", user.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                final String postID = document.getId();
                                final String extension = document.getData().get("extension").toString();
                                final String content = document.getData().get("content").toString();
                                if (isStorageUrl(content)) {
                                    storageRef.child("posts/" + postID + "." + extension).delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    firebaseFirestore.collection("posts").document(postID).delete()
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    firebaseFirestore.collection("posts")
                                                                            .document(postID).delete()
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    showToast(getActivity(), "게시글 삭제중...");
                                                                                }
                                                                            })
                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {
                                                                                    onLoaderListener.loadingOff();
                                                                                    Log.e(TAG, "Posts store delete ERROR with " + e);
                                                                                }
                                                                            });

                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    onLoaderListener.loadingOff();
                                                                    Log.e(TAG, "Posts storage delete ERROR with " + e);
                                                                }
                                                            });
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    onLoaderListener.loadingOff();
                                                    Log.e(TAG, "Posts find ERROR with " + e);
                                                }
                                            });
                                }
                            }
                            deleteAccount();
                        }
                    }
                });
    }

    private void deleteAccount() {
        final String userID = user.getUid();
        user.getCurrentUser().delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        firebaseFirestore.collection("users").document(userID).delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        onLoaderListener.loadingOff();
                                        showToast(getActivity(), "계정이 삭제되었습니다.");
                                        closeActivity();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        onLoaderListener.loadingOff();
                                        showToast(getActivity(), "계정을 삭제하지 못했습니다.");
                                        Log.e(TAG, "User Info delete ERROR with " + e);
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        onLoaderListener.loadingOff();
                        showToast(getActivity(), "계정을 삭제하지 못했습니다.");
                        Log.e(TAG, "User Account delete ERROR with " + e);
                    }
                });
    }

    private void myStartActivity(Class c){
        Intent intent = new Intent(getActivity(), c);
        startActivity(intent);
    }

    private void closeActivity() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        getActivity().finish();
        startActivity(intent);
    }
}
