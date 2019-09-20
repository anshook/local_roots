package com.udacity.ak.localroots.data.database;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface FavoriteMarketDao {

    @Query("SELECT * FROM favorite_market ORDER BY marketname")
    LiveData<List<Market>> getAllFavorites();

    @Query("SELECT id FROM favorite_market")
    LiveData<List<String>> getAllFavoriteIds();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertFavorite(Market favorite);

    @Delete
    void deleteFavorite(Market favorite);

    @Query("DELETE FROM favorite_market WHERE id = :id")
    void deleteById(String id);
}
