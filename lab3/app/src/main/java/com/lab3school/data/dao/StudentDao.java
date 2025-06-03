package com.lab3school.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.lab3school.data.entity.Student;
import com.lab3school.data.relation.StudentWithDetails;

import java.util.List;

@Dao
public interface StudentDao extends BaseDao<Student> {

    @Transaction
    @Query("SELECT * FROM students WHERE id = :studentId")
    StudentWithDetails getStudentWithDetailsById(int studentId);

    @Transaction
    @Query("SELECT * FROM students WHERE id = :studentId")
    LiveData<StudentWithDetails> getStudentWithDetailsByIdLiveData(int studentId);

    @Transaction
    @Query("SELECT * FROM students ORDER BY last_name ASC, first_name ASC")
    List<StudentWithDetails> getAllStudentsWithDetails();

    @Transaction
    @Query("SELECT * FROM students ORDER BY last_name ASC, first_name ASC")
    LiveData<List<StudentWithDetails>> getAllStudentsWithDetailsLiveData();

    @Query("SELECT * FROM students WHERE id = :studentId")
    Student getStudentById(int studentId);

    @Query("SELECT * FROM students ORDER BY last_name ASC, first_name ASC")
    List<Student> getAllStudents();
}
