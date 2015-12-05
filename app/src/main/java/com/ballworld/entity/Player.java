package com.ballworld.entity;

/**
 * Created by duocai at 18:23 on 2015/11/14.
 */
public class Player {
    public static final String FOOD = "food";
    public static final String WOOD = "wood";
    public static final String MINE = "mine";
    public static final String HP = "hp";
    public static final String LEVEL = "level";
    public static final String[] BUILDING_LEVEL = {"level0","level1","level2","level3","level4","level5"};
    public static final String DAMAGE = "damage";
    public static final String DEFENSE = "defense";
    public static final String LEVEL_ID = "levelId";//关卡

    private  int levelId;
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
    //记录玩家的建筑
    private Buildings[] building;
    //攻击力
    private int damage;
    //防御力
    private int defense;

    public Player(){

    }

    public Player(int food,int wood,int mine){
        this.food=food;
        this.wood=wood;
        this.mine=mine;
        this.building=new Buildings[6];
        for(int i=0;i<this.building.length;i++){
            building[i]=new Buildings(i,0);
        }
    }


    //所有房屋的资源的增长量
    public int[] produce(){
        int[] output=new int[3];
        for(int i=0;i<3;i++){
            for(int j=0;j<4;j++){
                output[i]=output[i]+this.getBuilding()[j].produce()[i];
            }
        }
        return output;
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

    public Buildings[] getBuilding() {
        return this.building;
    }

    public void setBuilding(Buildings[] building) {
        this.building = building;
    }

    public void setBuilding(Buildings[] building,Buildings renew){
        building[renew.getType()]=renew;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public int getLevelId() {
        return levelId;
    }

    public void setLevelId(int levelId) {
        this.levelId = levelId;
    }
}
