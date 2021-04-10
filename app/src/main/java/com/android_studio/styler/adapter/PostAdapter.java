package com.android_studio.styler.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android_studio.styler.R;
import com.android_studio.styler.listener.OnPostListener;
import com.android_studio.styler.view.PostInfo;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private ArrayList<PostInfo> mDataset;
    private Activity activity;
    private FirebaseUser user;
    private OnPostListener onPostListener;
    private boolean waitDouble = true;
    private static final int DOUBLE_CLICK_TIME = 200;

    static class PostViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;

        PostViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public PostAdapter(Activity activity, ArrayList<PostInfo> myDataset) {
        this.mDataset = myDataset;
        this.activity = activity;
        this.user = FirebaseAuth.getInstance().getCurrentUser();
    }

    public void setOnPostListener(OnPostListener onPostListener) {
        this.onPostListener = onPostListener;
    }

    @NonNull
    @Override
    public PostAdapter.PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        final PostViewHolder postViewHolder = new PostViewHolder(cardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (postViewHolder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                    if (waitDouble) {
                        waitDouble = false;
                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                try {
                                    sleep(DOUBLE_CLICK_TIME);
                                    if (!waitDouble) {
                                        waitDouble = true;
                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        thread.start();
                    } else {
                        waitDouble = true;
                        onPostListener.onHeart(postViewHolder.getAdapterPosition());
                    }
                }
            }
        });

        cardView.findViewById(R.id.postMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(postViewHolder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                    showPopup(view, postViewHolder.getAdapterPosition());
                }
            }
        });

        return postViewHolder;
    }

    private static class TIME_MAXIMUM{
        public static final int SEC = 60;
        public static final int MIN = 60;
        public static final int HOUR = 24;
        public static final int DAY = 30;
        public static final int MONTH = 12;
    }

    @Override
    public void onBindViewHolder(@NonNull final PostViewHolder holder, int position) {
        CardView cardView = holder.cardView;

        // 계정 확인 작업
        if (mDataset.get(position).getPublisher().equals(user.getUid())) {
            cardView.findViewById(R.id.postMenu).setVisibility(View.VISIBLE);
        } else {
            cardView.findViewById(R.id.postMenu).setVisibility(View.INVISIBLE);
        }

        TextView tv_Name = cardView.findViewById(R.id.tv_Name);
        tv_Name.setText(mDataset.get(position).getName());

        ImageView postView = cardView.findViewById(R.id.postView);
        Glide.with(activity).load(mDataset.get(position).getContent()).override(1300).thumbnail(0.1f).into(postView);

        LinearLayout hashtagLayout = cardView.findViewById(R.id.hashtagLayout);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.rightMargin = 50;
        ArrayList<String> hashtagList = mDataset.get(position).getHashtag();
        if (hashtagLayout.getTag() == null || !hashtagLayout.getTag().equals(hashtagList)) {
            hashtagLayout.setTag(hashtagList);
            hashtagLayout.removeAllViews();
            for (int i = 0; i < hashtagList.size(); i++) {
                String hashtag = hashtagList.get(i);
                TextView textView = new TextView(activity);
                textView.setLayoutParams(layoutParams);
                textView.setTextSize(18);
                textView.setText(hashtag);
                hashtagLayout.addView(textView);
            }
        }

        TextView tv_PostedAt = cardView.findViewById(R.id.tv_PostedAt);
        long curTime = System.currentTimeMillis();
        long regTime = mDataset.get(position).getPostedAt().getTime();
        long diffTime = (curTime - regTime) / 1000;
        String msg;
        if (diffTime < TIME_MAXIMUM.SEC) {
            msg = "방금 전";
        } else if ((diffTime /= TIME_MAXIMUM.SEC) < TIME_MAXIMUM.MIN) {
            msg = diffTime + "분 전";
        } else if ((diffTime /= TIME_MAXIMUM.MIN) < TIME_MAXIMUM.HOUR) {
            msg = (diffTime) + "시간 전";
        } else if ((diffTime /= TIME_MAXIMUM.HOUR) < TIME_MAXIMUM.DAY) {
            msg = (diffTime) + "일 전";
        } else if ((diffTime /= TIME_MAXIMUM.DAY) < TIME_MAXIMUM.MONTH) {
            msg = (diffTime) + "달 전";
        } else {
            msg = (diffTime) + "년 전";
        }
        tv_PostedAt.setText(msg);

        ArrayList<String> heartID = mDataset.get(position).getHeartID();
        ImageView heartView = cardView.findViewById(R.id.heartView);
        boolean heartFlag = true;
        for (int i = 0; i < heartID.size(); i++) {
            if (heartID.get(i).equals(user.getUid())) {
                heartFlag = false;
                heartView.setImageResource(R.drawable.heart_2);
            }
        }
        if (heartFlag) {
            heartView.setImageResource(R.drawable.heart_1);
        }

        TextView heartCount = cardView.findViewById(R.id.heartCount);
        heartCount.setText("" + heartID.size());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    private void showPopup(View view, final int position) {
        PopupMenu popup = new PopupMenu(activity, view);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.modify:
                        onPostListener.onModify(position);
                        return true;
                    case R.id.delete:
                        onPostListener.onDelete(position);
                        return true;
                    default:
                        return false;
                }
            }
        });
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.post_menu, popup.getMenu());
        popup.show();
    }
}