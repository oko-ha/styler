package com.android_studio.styler.view;

public class UserInfo {
    private String name;
    private String age ;
    private String sex;

    public UserInfo(String name, String age, String sex) {
        this.name = name;
        this.age = age;
        this.sex = sex;
    }

    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }

    public String getAge() { return this.age; }
    public void setAge(String age) { this.age = age; }

    public String getSex() { return this.sex; }
    public void setSex(String sex) { this.sex = sex; }
}
