package com.android_studio.styler.view;

public class CategoryInfo implements Comparable<CategoryInfo> {
    private int categoryID;
    private int categoryCount;

    public CategoryInfo(int categoryID, int categoryCount) {
        this.categoryID = categoryID;
        this.categoryCount = categoryCount;
    }

    public int getCategoryID() {
        return this.categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public int getCategoryCount() {
        return this.categoryCount;
    }

    public void setCategoryCount(int categoryCount) {
        this.categoryCount = categoryCount;
    }

    public int compareTo(CategoryInfo arg0) {
        if (this.categoryCount < arg0.categoryCount) return 1;
        else if (this.categoryCount == arg0.categoryCount) return 0;
        else return -1;
    }
}
