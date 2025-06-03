package com.lab3school.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.lab3school.data.entity.Subject;

import java.util.List;

@Dao
public interface SubjectDao extends BaseDao<Subject> {
    @Query("SELECT * FROM subjects WHERE id = :subjectId")
    Subject getSubjectById(int subjectId);

    @Query("SELECT * FROM subjects WHERE id = :subjectId")
    LiveData<Subject> getSubjectByIdLiveData(int subjectId);

    @Query("SELECT * FROM subjects ORDER BY name ASC")
    List<Subject> getAllSubjects();

    @Query("SELECT * FROM subjects ORDER BY name ASC")
    LiveData<List<Subject>> getAllSubjectsLiveData();

    @Query("SELECT COUNT(*) FROM subjects")
    int getSubjectCount();
}
