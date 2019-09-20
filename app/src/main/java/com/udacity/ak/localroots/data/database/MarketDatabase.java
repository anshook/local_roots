package com.udacity.ak.localroots.data.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Market.class}, version = 1)
public abstract class MarketDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "market";
    private static volatile MarketDatabase dbInstance;

    public static MarketDatabase getDatabase(final Context context) {
        if (dbInstance == null) {
            synchronized (MarketDatabase.class) {
                if (dbInstance == null)
                    dbInstance = Room.databaseBuilder(context.getApplicationContext(),
                            MarketDatabase.class, DATABASE_NAME)
                            .build();
            }
        }
        return dbInstance;
    }

    public abstract FavoriteMarketDao favoriteMarketDao();
}
