package com.ballworld.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ballworld.entity.Buildings;
import com.ballworld.entity.Equitment;
import com.ballworld.entity.Player;

import static com.ballworld.entity.Player.BUILDING_LEVEL;
import static com.ballworld.entity.Player.DAMAGE;
import static com.ballworld.entity.Player.DEFENSE;
import static com.ballworld.entity.Player.DEFENSE_ATTACK;
import static com.ballworld.entity.Player.DEFENSE_DEFENSE;
import static com.ballworld.entity.Player.DEFENSE_NAME;
import static com.ballworld.entity.Player.FOOD;
import static com.ballworld.entity.Player.HP;
import static com.ballworld.entity.Player.LEVEL;
import static com.ballworld.entity.Player.LEVEL_ID;
import static com.ballworld.entity.Player.MINE;
import static com.ballworld.entity.Player.WEAPON_ATTACK;
import static com.ballworld.entity.Player.WEAPON_DEFENSE;
import static com.ballworld.entity.Player.WEAPON_NAME;
import static com.ballworld.entity.Player.WOOD;
import static com.ballworld.util.MySQLiteHelper.TABLE_PLAYER;

/**
 * Created by duocai at 14:40 on 2015/11/29.
 */
public class SQLiteUtil {
    private MySQLiteHelper helper;
    private SQLiteDatabase db;

    public SQLiteUtil(Context context) {
        //获得数据库助手
        helper = new MySQLiteHelper(context);
    }

    /**
     * 加入玩家信息
     * 该函数不提供外部调用，外部统一使用update函数
     * @param player
     */
    private void insertPlayer(Player player) {
        db = helper.getWritableDatabase();
        //开始事物
        db.beginTransaction();

        //开始操作
        try {
            db.execSQL("INSERT INTO "+TABLE_PLAYER+" VALUES(null,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", new Object[]{
                    player.getFood(), player.getWood(), player.getMine(), player.getHp(), player.getLevel(),
                    player.getBuilding()[0].getLevel(), player.getBuilding()[1].getLevel(),
                    player.getBuilding()[2].getLevel(), player.getBuilding()[3].getLevel(),
                    player.getBuilding()[4].getLevel(), player.getBuilding()[5].getLevel(),
                    player.getDamage(),player.getDefense(),player.getLevelId(),player.getEquitments()[0].getName(),
                    player.getEquitments()[0].getAttack(),player.getEquitments()[0].getDefense(),
                    player.getEquitments()[1].getName(),player.getEquitments()[1].getAttack(),
                    player.getEquitments()[1].getDefense()
            });

            //成功完成事物
            db.setTransactionSuccessful();
        } finally {
            //结束事物
            db.endTransaction();
            db.close();
        }
    }

    /**
     * 更新玩家信息
     * 使用updatePlayer(player,1).
     * @param player
     */
    public void updatePlayer(Player player, int id) {
        if (queryPlayer(id) == null) {//还没有记录
            insertPlayer(player);
        } else {//有记录，更新记录
            db = helper.getWritableDatabase();//获得链接

            //记录要更改的信息
            ContentValues cv = new ContentValues();
            cv.put(FOOD, player.getFood());
            cv.put(WOOD, player.getWood());
            cv.put(MINE, player.getMine());
            cv.put(HP, player.getHp());
            cv.put(LEVEL, player.getLevel());
            cv.put(DAMAGE,player.getDamage());
            cv.put(DEFENSE,player.getDefense());
            cv.put(LEVEL_ID,player.getLevelId());
            for (int i=0;i<6;i++){
                cv.put(BUILDING_LEVEL[i],player.getBuilding()[i].getLevel());
            }
            cv.put(WEAPON_NAME,player.getEquitments()[0].getName());
            cv.put(WEAPON_ATTACK,player.getEquitments()[0].getAttack());
            cv.put(WEAPON_DEFENSE,player.getEquitments()[0].getDefense());
            cv.put(DEFENSE_NAME, player.getEquitments()[1].getName());
            cv.put(DEFENSE_ATTACK, player.getEquitments()[1].getAttack());
            cv.put(DEFENSE_DEFENSE, player.getEquitments()[1].getDefense());
            //更改
            db.update(TABLE_PLAYER, cv, "_id = ?", new String[]{id+""});

            //关闭连接
            db.close();
        }
    }

    /**
     * 读取一个玩家记录
     * 使用query函数时，用queryPlayer(1).
     * @param id
     * @return - 若返回null，则玩家第一次玩，使用默认数据
     */
    public Player queryPlayer(int id) {
        db = helper.getReadableDatabase();
        Player player = null;
        Buildings[] buildings = new Buildings[6];
        Equitment[] equitments=new Equitment[2];
        //query
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_PLAYER + " WHERE _id = ?", new String[]{id + ""});
        //有结果的话封装对象
        if (c!=null&&c.moveToNext()) {
            player = new Player();
            player.setFood(c.getInt(c.getColumnIndex(FOOD)));
            player.setWood(c.getInt(c.getColumnIndex(WOOD)));
            player.setMine(c.getInt(c.getColumnIndex(MINE)));
            player.setHp(c.getInt(c.getColumnIndex(HP)));
            player.setLevel(c.getInt(c.getColumnIndex(LEVEL)));
            player.setDamage(c.getInt(c.getColumnIndex(DAMAGE)));
            player.setDefense(c.getInt(c.getColumnIndex(DEFENSE)));
            player.setLevelId(c.getInt(c.getColumnIndex(LEVEL_ID)));
            //获取建筑信息
            for (int i = 0; i < 6; i++) {
                buildings[i] = new Buildings(i, c.getInt(c.getColumnIndex(BUILDING_LEVEL[i])));
            }
            player.setBuilding(buildings);
            //获取装备信息
            equitments[0]=new Equitment(c.getString(c.getColumnIndex(WEAPON_NAME)),
                                        c.getInt(c.getColumnIndex(WEAPON_ATTACK)),
                                        c.getInt(c.getColumnIndex(WEAPON_DEFENSE)));
            equitments[1]=new Equitment(c.getString(c.getColumnIndex(DEFENSE_NAME)),
                    c.getInt(c.getColumnIndex(DEFENSE_ATTACK)),
                    c.getInt(c.getColumnIndex(DEFENSE_DEFENSE)));
            player.setEquitments(equitments);
        }
        c.close();
        db.close();
        return player;
    }


    /**
     * 访问所有玩家记录，返回cursor
     *
     * @return
     */
    public Cursor queryPlayerCursor() {
        db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_PLAYER, null);
        return c;
    }
}
