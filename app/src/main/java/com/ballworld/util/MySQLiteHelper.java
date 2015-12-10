package com.ballworld.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

/**
 * Created by duocai at 15:10 on 2015/11/29.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "BallWorld";
    public static final String TABLE_PLAYER = "player";
    public static final int DATABASE_VERSION = 1;

    /**
     * 创建数据库
     */
    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * 建表，现有player，
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_PLAYER +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + FOOD + " INTEGER,"
                + WOOD + " INTEGER,"
                + MINE + " INTEGER,"
                + HP + " INTEGER,"
                + LEVEL + " INTEGER,"
                + BUILDING_LEVEL[0] + " INTEGER,"
                + BUILDING_LEVEL[1] + " INTEGER,"
                + BUILDING_LEVEL[2] + " INTEGER,"
                + BUILDING_LEVEL[3] + " INTEGER,"
                + BUILDING_LEVEL[4] + " INTEGER,"
                + BUILDING_LEVEL[5] + " INTEGER,"
                + DAMAGE + " INTEGER,"
                + DEFENSE + " INTEGER,"
                + LEVEL_ID + " INTEGER,"
                + WEAPON_NAME + " STRING,"
                + WEAPON_ATTACK + " INTEGER,"
                + WEAPON_DEFENSE + " INTEGER,"
                + DEFENSE_NAME + " STRING,"
                + DEFENSE_ATTACK + " INTEGER,"
                + DEFENSE_DEFENSE + " INTEGER)");
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     * <p/>
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("ALTER TABLE staff ADD COLUMN other STRING");
    }
}
