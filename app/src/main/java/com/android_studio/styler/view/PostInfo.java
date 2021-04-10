package com.android_studio.styler.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class PostInfo implements Serializable, Comparable<PostInfo> {
    private String name;
    private String content;
    private String extension;
    private ArrayList<String> hashtag;
    private String publisher;
    private Date postedAt;
    private String postID;
    private ArrayList<String> heartID;
    private int heartCount;
    private ArrayList<String> category;
    private String sex;
    private String age;

    public PostInfo(String name, String content, String extension, ArrayList<String> hashtag, String publisher, Date postedAt,
                    String postID, ArrayList<String> heartID, int heartCount, ArrayList<String> category, String sex, String age) {
        this.name = name;
        this.content = content;
        this.extension = extension;
        this.hashtag = hashtag;
        this.publisher = publisher;
        this.postedAt = postedAt;
        this.postID = postID;
        this.heartID = heartID;
        this.heartCount = heartCount;
        this.category = category;
        this.sex = sex;
        this.age = age;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExtension() {
        return this.extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public ArrayList<String> getHashtag() {
        return this.hashtag;
    }

    public void setHashtag(ArrayList<String> hashtag) {
        this.hashtag = hashtag;
    }

    public String getPublisher() {
        return this.publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Date getPostedAt() {
        return this.postedAt;
    }

    public void setPostedAt(Date postedAt) {
        this.postedAt = postedAt;
    }

    public String getPostID() {
        return this.postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public ArrayList<String> getHeartID() {
        return this.heartID;
    }

    public void setHeartID(ArrayList<String> heartID) {
        this.heartID = heartID;
    }

    public int getHeartCount() {
        return this.heartCount;
    }

    public void setHeartCount(int heartCount) {
        this.heartCount = heartCount;
    }

    public ArrayList<String> getCategory() { return this.category; }

    public void setCategory(ArrayList<String> category) { this.category = category; }

    public String getSex() {
        return this.sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAge() {
        return this.age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public int compareTo(PostInfo arg0) {
        if (this.heartCount < arg0.heartCount) return 1;
        else if (this.heartCount == arg0.heartCount) return 0;
        else return -1;
    }
}
