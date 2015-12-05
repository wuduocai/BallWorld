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
    //为房屋升级,最多为3级
    public void addLevel(){
        if(this.getLevel()!=3){
            this.setLevel(this.getLevel()+1);
        }
    }

    //根据房屋的种类与等级，决定下一次升级的费用
    //返回的数组包括3个数，分别为食物，木材，铁块
    public int[] cost(){
        int[] output=new int[3];
        int[] food=new int[3];
        int[] wood=new int[3];
        int[] mine=new int[3];
        switch (this.getType()){
            //房屋的建造消耗
            case 0:
                food=new int[]{20,100,240};
                wood=new int[]{30,120,300};
                mine=new int[]{5,30,60};
                break;
            //农场的建造消耗
            case 1:
                food=new int[]{40,150,340};
                wood=new int[]{30,120,300};
                mine=new int[]{10,35,80};
                break;
            //伐木场的建造消耗
            case 2:
                food=new int[]{20,100,240};
                wood=new int[]{50,160,400};
                mine=new int[]{10,35,80};
                break;
            //矿场的建造消耗
            case 3:
                food=new int[]{20,100,240};
                wood=new int[]{30,120,300};
                mine=new int[]{20,50,100};
                break;
            //铁匠铺的建造消耗
            case 4:
                food=new int[]{80,200,440};
                wood=new int[]{90,220,400};
                mine=new int[]{50,90,160};
                break;
            //医院的建造消耗
            case 5:
                food=new int[]{20,100,240};
                wood=new int[]{30,120,300};
                mine=new int[]{15,40,70};
                break;
            default:;
        }
        output[0]=food[this.getLevel()];
        output[1]=wood[this.getLevel()];
        output[2]=mine[this.getLevel()];
        return output;
    }
    //根据房屋的种类与等级，决定额外的资源增长的数量
    //共有4种建筑可以额外增加资源：房屋，农场，伐木场，矿场
    public int[] produce(){
        int[] output=new int[3];
        int[] food=new int[4];
        int[] wood=new int[4];
        int[] mine=new int[4];
        switch (this.getType()){
            //房屋的资源增长
            case 0:
                food=new int[]{0,2,6,10};
                wood=new int[]{0,2,6,10};
                mine=new int[]{0,1,3,5};
                break;
            //农场的资源增长
            case 1:
                food=new int[]{0,10,15,30};
                wood=new int[]{0,0,0,0};
                mine=new int[]{0,0,0,0};
                break;
            //伐木场的资源增长
            case 2:
                food=new int[]{0,0,0,0};
                wood=new int[]{0,12,20,30};
                mine=new int[]{0,0,0,0};
                break;
            //矿场的资源增长
            case 3:
                food=new int[]{0,0,0,0};
                wood=new int[]{0,0,0,0};
                mine=new int[]{0,5,10,20};
                break;
            default:;
        }
        output[0]=food[this.getLevel()];
        output[1]=wood[this.getLevel()];
        output[2]=mine[this.getLevel()];
        return output;
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

