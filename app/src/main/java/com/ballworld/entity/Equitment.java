package com.ballworld.entity;

/**
 * Created by mac on 2015/12/5.
 */
public class Equitment {
    //装备的名字
    private String name;
    //装备的攻击力
    private int attack;
    //装备的防御力
    private int defense;

    public Equitment(){

    }

    public Equitment(String name,int atack,int denfense){
        this.name=name;
        this.attack=atack;
        this.defense=denfense;
    }

    public int getAttack() {
        return this.attack;
    }

    public int getDefense() {
        return this.defense;
    }

    public String getName() {
        return this.name;
    }
}
