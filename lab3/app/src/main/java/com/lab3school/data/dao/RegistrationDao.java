package com.lab3school.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.lab3school.data.entity.Registration;
import com.lab3school.data.relation.RegistrationWithDetails;

import java.util.List;

@Dao
public interface RegistrationDao extends BaseDao<Registration> {

    @Transaction
    @Query("SELECT * FROM registrations WHERE student_id = :studentId")
    List<RegistrationWithDetails> getRegistrationsWithDetailsForStudent(int studentId);

    @Transaction
    @Query("SELECT * FROM registrations WHERE student_id = :studentId")
    LiveData<List<RegistrationWithDetails>> getRegistrationsWithDetailsForStudentLiveData(int studentId);

    @Transaction
    @Query("SELECT * FROM registrations WHERE id = :registrationId")
    RegistrationWithDetails getRegistrationWithDetailsById(int registrationId);

    @Query("SELECT * FROM registrations WHERE student_id = :studentId AND subject_id = :subjectId LIMIT 1")
    Registration getRegistrationByStudentAndSubject(int studentId, int subjectId);

    @Query("DELETE FROM registrations WHERE student_id = :studentId")
    int deleteRegistrationsForStudent(int studentId);

    @Query("DELETE FROM registrations WHERE student_id = :studentId AND subject_id = :subjectId")
    int deleteRegistration(int studentId, int subjectId);

    @Query("SELECT * FROM registrations WHERE student_id = :studentId")
    List<Registration> getRegistrationsForStudentSync(int studentId);
}

