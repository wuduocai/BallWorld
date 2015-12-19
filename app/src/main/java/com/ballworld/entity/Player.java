package com.ballworld.entity;


/**
 * Created by duocai at 18:23 on 2015/11/14.
 */
public class Player {
    public static final Player NIL = null;
    public static final String FOOD = "food";
    public static final String WOOD = "wood";
    public static final String MINE = "mine";
    public static final String HP = "hp";
    public static final String LEVEL = "level";
    public static final String[] BUILDING_LEVEL = {"level0", "level1", "level2", "level3", "level4", "level5"};
    public static final String DAMAGE = "damage";
    public static final String DEFENSE = "defense";
    public static final String LEVEL_ID = "levelId";
    public static final String WEAPON_NAME = "weaponname";
    public static final String WEAPON_ATTACK = "weaponattack";
    public static final String WEAPON_DEFENSE = "weapondefense";
    public static final String DEFENSE_NAME = "defensename";
    public static final String DEFENSE_ATTACK = "defenseattack";
    public static final String DEFENSE_DEFENSE = "defensedefense";
    public static final String EXP="exp";
    public static final String[] UNDERBUILD={"build0","build1","build2","build3","build4","build5"};
    public static final String[] RESTTIME={"time0","time1","time2","time3","time4","time5"};
    //关卡
    private int levelId;
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
    //记录玩家的装备,equitments[0]记录武器，equitments[1]记录防具
    private Equitment[] equitments;
    //攻击力
    private int damage;
    //防御力
    private int defense;
    //hp上限
    private int[] maxhp = {5, 10, 10, 15, 15, 20, 30, 30, 30, 35};
    //记录玩家自身无装备情况下的防御力
    private int[] ownDen = {0, 1, 2, 2, 3, 3, 3, 4, 4, 5};
    //记录玩家现有的exp
    private int exp;
    //记录玩家升级所需要的exp
    private int[] expneeded={10,30,70,120,200,300,500,800,1200};

    public Player() {

    }

    public Player(int food, int wood, int mine) {
        this.food = food;
        this.wood = wood;
        this.mine = mine;
        this.building = new Buildings[6];
        for (int i = 0; i < this.building.length; i++) {
            building[i] = new Buildings(i, 0);
        }
        this.equitments = new Equitment[2];
        equitments[0]=new Equitment("新手木剑",1,0,true);
        equitments[1]=new Equitment("新手装甲",0,1,false);
        this.level = 1;
        this.hp = maxhp[0];
        this.exp=0;
    }


    //所有房屋的资源的增长量
    public int[] produce() {
        int[] output = new int[3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                output[i] = output[i] + this.getBuilding()[j].produce()[i];
            }
        }
        return output;
    }

    //根据敌人，自己的防御力来进行扣血
    public int harm(int levelId) {
        int harm = (int) ((levelId + 1) * 6.5);
        int actualharm = 0;
        int defense = this.getDefense();
        if (0 < defense && defense <= 4) {
            actualharm = Math.max(harm - (int) (defense * 0.35), 1);
        } else if (defense == 0) {
            actualharm = (int) (harm * 1.5);
        } else if (defense < 0) {
            actualharm = (int) (harm * (1 + (-1) * defense));
        } else if (defense <= 15) {
            actualharm = Math.max(harm - (int) (defense * 0.65), 1);
        } else {
            actualharm = Math.max(Math.min(harm - (int) (defense * 0.65), harm - (defense * (defense / 17))), 1);
        }
        this.setHp(this.getHp() - actualharm);
        return actualharm;
    }

    //调用的升级的方法
    public void update(int i,boolean win){
        int exp=0;
        switch(i){
            case 0:
                exp=8;
                break;
            case 1:
                exp=24;
                break;
            case 2:
                exp=56;
                break;
            case 3:
                exp=220;
                break;
            case 4:
                exp=400;
                break;
            case 5:
                exp=1000;
                break;
            default:
                break;
        }
        if(!win){
            exp=exp/4;
        }
        this.setExp(this.getExp()+exp);
        if(this.level==10){
            return;
        }
        while(this.level!=10&&this.exp>=this.expneeded[this.level-1]){
            this.setExp(this.exp-this.expneeded[this.level-1]);
            this.setLevel(this.getLevel() + 1);
        }
    }

    //set与get方法
    public int getFood() {
        return this.food;
    }

    public void setFood(int food) {
        this.food = food;
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

    public void setBuilding(Buildings[] building, Buildings renew) {
        building[renew.getType()] = renew;
    }

    public int getDamage() {
        int output = 2 + this.getWeaponAttack() + this.getDenAttack();
        return output;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getDefense() {
        int output = this.getOwnDen(this.getLevel()) + this.getWeaponDefense() + this.getDenDenfense();
        return output;
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

    public Equitment[] getEquitments() {
        return this.equitments;
    }

    public void setEquitments(Equitment[] equitments) {
        this.equitments = equitments;
    }

    public void setWeapon(Equitment equit) {
        this.equitments[0] = equit;
    }

    public void setDefn(Equitment equit) {
        this.equitments[1] = equit;
    }

    public int gethpMax(int level) {
        return maxhp[level - 1];
    }

    public int getOwnDen(int level) {
        return ownDen[level - 1];
    }

    public int getWeaponAttack() {
        if (equitments[0] == null) {
            return 0;
        }
        return equitments[0].getAttack();
    }

    public int getWeaponDefense() {
        if (equitments[0] == null) {
            return 0;
        }
        return equitments[0].getDefense();
    }

    public int getDenAttack() {
        if (equitments[1] == null) {
            return 0;
        }
        return equitments[1].getAttack();
    }

    public int getDenDenfense() {
        if (equitments[1] == null) {
            return 0;
        }
        return equitments[1].getDefense();
    }

    public int getExp() {
        return this.exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int[] getExpneeded() {
        return this.expneeded;
    }
}
