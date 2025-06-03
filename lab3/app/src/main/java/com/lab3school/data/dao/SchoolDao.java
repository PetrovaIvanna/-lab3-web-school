package com.lab3school.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.lab3school.data.entity.School;

import java.util.List;

@Dao
public interface SchoolDao extends BaseDao<School> {
    @Query("SELECT * FROM schools WHERE id = :schoolId")
    School getSchoolById(int schoolId);

    @Query("SELECT * FROM schools WHERE id = :schoolId")
    LiveData<School> getSchoolByIdLiveData(int schoolId);

    @Query("SELECT * FROM schools ORDER BY name ASC")
    List<School> getAllSchools();

    @Query("SELECT * FROM schools ORDER BY name ASC")
    LiveData<List<School>> getAllSchoolsLiveData();

    @Query("SELECT COUNT(*) FROM schools")
    int getSchoolCount();
}
