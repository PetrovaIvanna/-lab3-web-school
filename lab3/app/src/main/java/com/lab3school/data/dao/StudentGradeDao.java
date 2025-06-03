package com.lab3school.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.lab3school.data.entity.StudentGrade;

import java.util.List;

@Dao
public interface StudentGradeDao extends BaseDao<StudentGrade> {
    @Query("SELECT * FROM student_grades WHERE registration_id = :registrationId")
    List<StudentGrade> getGradesForRegistration(int registrationId);

    @Query("SELECT * FROM student_grades WHERE registration_id = :registrationId")
    LiveData<List<StudentGrade>> getGradesForRegistrationLiveData(int registrationId);

    @Query("DELETE FROM student_grades WHERE registration_id = :registrationId")
    int deleteGradesForRegistration(int registrationId);
}
