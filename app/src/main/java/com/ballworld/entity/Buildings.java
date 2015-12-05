package com.ballworld.entity;

/**
 * Created by mac on 2015/11/26.
 */
public class Buildings {
    /*
    * 记录房屋的种类
    * 0住房，1农场，2伐木场，3矿场，4铁匠铺，5医院
    * */
    private int type;
    //房屋的等级，如果为0说明还没有建造
    private int level;

    public Buildings(){

    }

    public Buildings(int type,int level){
        this.type=type;
        this.level=level;
    }

    //get与set方法

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}

