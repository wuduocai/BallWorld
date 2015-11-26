package com.ballworld.entity;

/**
 * Created by duocai at 18:23 on 2015/11/14.
 */
public class Player {
    //食物的数量
    private int food;
    //木材的数量
    private int wood;
    //铁矿的数量
    private int mine;
    //hp
    private int hp;
    //level
    private int level;
    //记录as

    public Player(){

    }

    public Player(int food,int wood,int mine){
        this.food=food;
        this.wood=wood;
        this.mine=mine;
    }

    //set与get方法
    public int getFood(){
        return this.food;
    }

    public void setFood(int food){
        this.food=food;
    }

    public int getHp() {
        return this.hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getMine() {
        return this.mine;
    }

    public void setMine(int mine) {
        this.mine = mine;
    }

    public int getWood() {
        return this.wood;
    }

    public void setWood(int wood) {
        this.wood = wood;
    }

}
