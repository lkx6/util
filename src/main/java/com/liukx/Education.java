package com.liukx;

/**
 * 枚举类样例
 **/
public enum Education {
    /**
     * 高中及以下
     */
    HIGHANDBELOW(1,"高中及以下"),
    /**
     * 大专
     */
    COLLEGE(3,"大专"),
    /**
     * 大学本科
     */
    UNIVERSITY(4,"大学本科"),
    /**
     * 硕士及以上
     */
    MASTERANDABOVE(6,"硕士及以上"),
    /**
     * 其他
     */
    OTHERS(9,"其他");


    private int id;
    private String edu;

    Education(int id, String edu) {
        this.id = id;
        this.edu = edu;
    }

    public int getId() {
        return id;
    }

    public String getEdu() {
        return edu;
    }
}
