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
    //判断是武器还是装甲
    private boolean weapon;

    public Equitment(){

    }

    public Equitment(String name,int atack,int denfense,boolean weapon){
        this.name=name;
        this.attack=atack;
        this.defense=denfense;
        this.weapon=weapon;
    }

    //根据需求随机生成装备
    //i为1为raw，2为ordinary，3为shiny
    public Equitment(int i){
        switch (i){
            case 1:
                String[] names1={"试做型","粗糙的","简陋的","量产型"};
                String[] weapons1={"木剑","铁剑","石剑","大刀"};
                String[] defenses1={"护心镜","头盔","护腕","绑腿"};
                int random=(int)(Math.random()*2);
                if(random==0){
                    String name=names1[(int)(Math.random()*4)]+weapons1[(int)(Math.random()*4)];
                    this.attack=(int)(Math.random()*5)+1;
                    this.defense=(int)(Math.random()*2);
                    this.name=name;
                    this.weapon=true;
                }
                else{
                    String name=names1[(int)(Math.random()*4)]+defenses1[(int)(Math.random()*4)];
                    this.attack=(int)(Math.random()*2);
                    this.defense=(int)(Math.random()*7)+1;
                    this.name=name;
                    this.weapon=false;
                }
                break;
            case 2:
                String[] names2={"优良的","实用型","优秀的","精致的"};
                String[] weapons2={"铁剑","钢剑","铁矛","大刀"};
                String[] defenses2={"护心镜","头盔","护腕","盔甲"};
                random=(int)(Math.random()*2);
                if(random==0){
                    String name=names2[(int)(Math.random()*4)]+weapons2[(int)(Math.random()*4)];
                    this.attack=(int)(Math.random()*11)+2;
                    this.defense=(int)(Math.random()*3);
                    this.name=name;
                    this.weapon=true;
                }
                else{
                    String name=names2[(int)(Math.random()*4)]+defenses2[(int)(Math.random()*4)];
                    this.attack=(int)(Math.random()*2);
                    this.defense=(int)(Math.random()*11)+2;
                    this.name=name;
                    this.weapon=false;
                }
                break;
            case 3:
                String[] names3={"可怖的","闪光型","强力的","特异型"};
                String[] weapons3={"钻石剑","钢剑","铁矛","谜之武器"};
                String[] defenses3={"护心镜","头盔","护腕","盔甲"};
                random=(int)(Math.random()*2);
                if(random==0){
                    String name=names3[(int)(Math.random()*4)]+weapons3[(int)(Math.random()*4)];
                    this.attack=(int)(Math.random()*30)+1;
                    this.defense=(int)(Math.random()*9);
                    this.name=name;
                    this.weapon=true;
                }
                else{
                    String name=names3[(int)(Math.random()*4)]+defenses3[(int)(Math.random()*4)];
                    this.attack=(int)(Math.random()*7);
                    this.defense=(int)(Math.random()*20)+1;
                    this.name=name;
                    this.weapon=false;
                }
                break;
            default:;
        }
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

    public boolean isWeapon() {
        return this.weapon;
    }

    public void setWeapon(boolean weapon) {
        this.weapon = weapon;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }
}
