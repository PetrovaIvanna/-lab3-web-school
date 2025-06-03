package com.lab3school.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Update;

import java.util.List;

@Dao
public interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(T obj);

    @Insert(onConflict = OnConflictStrategy.ABORT)
    long[] insertAll(List<T> objects);

    @Update
    int update(T obj);

    @Delete
    int delete(T obj);
}