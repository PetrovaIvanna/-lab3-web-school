package com.lab3school.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import android.util.Log;

import com.lab3school.data.database.AppDatabase;
import com.lab3school.data.dao.StudentDao;
import com.lab3school.data.dao.RegistrationDao;
import com.lab3school.data.dao.StudentGradeDao;
import com.lab3school.data.entity.Student;
import com.lab3school.data.entity.Registration;
import com.lab3school.data.entity.StudentGrade;
import com.lab3school.data.entity.Subject;
import com.lab3school.data.relation.StudentWithDetails;
import com.lab3school.data.relation.RegistrationWithDetails;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class StudentRepository {
    private static final String TAG = "StudentRepository";

    private StudentDao studentDao;
    private RegistrationDao registrationDao;
    private StudentGradeDao studentGradeDao;

    private LiveData<List<StudentWithDetails>> allStudentsWithDetails;

    public StudentRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        studentDao = db.studentDao();
        registrationDao = db.registrationDao();
        studentGradeDao = db.studentGradeDao();
        allStudentsWithDetails = studentDao.getAllStudentsWithDetailsLiveData();
    }

    public LiveData<List<StudentWithDetails>> getAllStudentsWithDetails() {
        return allStudentsWithDetails;
    }

    public LiveData<StudentWithDetails> getStudentWithDetailsById(int studentId) {
        return studentDao.getStudentWithDetailsByIdLiveData(studentId);
    }

    public LiveData<List<RegistrationWithDetails>> getRegistrationsForStudent(int studentId) {
        return registrationDao.getRegistrationsWithDetailsForStudentLiveData(studentId);
    }

    public void insertStudentWithRegistrations(Student student, List<Subject> selectedSubjects) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            long studentId = studentDao.insert(student);
            if (studentId > 0) {
                if (selectedSubjects != null && !selectedSubjects.isEmpty()) {
                    List<Registration> registrationsToInsert = new ArrayList<>();
                    for (Subject subject : selectedSubjects) {
                        registrationsToInsert.add(new Registration((int) studentId, subject.getId()));
                    }
                    registrationDao.insertAll(registrationsToInsert);
                    Log.d(TAG, "Inserted " + registrationsToInsert.size() + " registrations for student ID: " + studentId);
                }
            } else {
                Log.e(TAG, "Failed to insert student: " + student.getFirstName());
            }
        });
    }

    public void updateStudentWithRegistrations(Student student, List<Subject> selectedSubjects) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            int updatedStudentRows = studentDao.update(student);
            if (updatedStudentRows > 0) {
                Log.d(TAG, "Student details updated for ID: " + student.getId());
                List<Registration> currentRegistrations = registrationDao.getRegistrationsForStudentSync(student.getId());
                Map<Integer, Registration> currentSubjectIdToRegistrationMap = currentRegistrations.stream()
                        .collect(Collectors.toMap(Registration::getSubjectId, reg -> reg));
                Set<Integer> newSelectedSubjectIds;
                if (selectedSubjects != null) {
                    newSelectedSubjectIds = selectedSubjects.stream()
                            .map(Subject::getId)
                            .collect(Collectors.toSet());
                } else {
                    newSelectedSubjectIds = new HashSet<>();
                }

                List<Registration> registrationsToDelete = new ArrayList<>();
                for (Registration currentReg : currentRegistrations) {
                    if (!newSelectedSubjectIds.contains(currentReg.getSubjectId())) {
                        registrationsToDelete.add(currentReg);
                    }
                }

                if (!registrationsToDelete.isEmpty()) {
                    for (Registration regToDelete : registrationsToDelete) {
                        registrationDao.delete(regToDelete);
                    }
                    Log.d(TAG, "Deleted " + registrationsToDelete.size() + " old registrations for student ID: " + student.getId());
                }

                List<Registration> registrationsToInsert = new ArrayList<>();
                for (Integer newSubjectId : newSelectedSubjectIds) {
                    if (!currentSubjectIdToRegistrationMap.containsKey(newSubjectId)) {
                        registrationsToInsert.add(new Registration(student.getId(), newSubjectId));
                    }
                }

                if (!registrationsToInsert.isEmpty()) {
                    registrationDao.insertAll(registrationsToInsert);
                    Log.d(TAG, "Inserted " + registrationsToInsert.size() + " new registrations for student ID: " + student.getId());
                }

                if (registrationsToDelete.isEmpty() && registrationsToInsert.isEmpty()) {
                    Log.d(TAG, "No changes in registrations for student ID: " + student.getId());
                }

            } else {
                Log.e(TAG, "Failed to update student or student not found: " + student.getFirstName() + " (ID: " + student.getId() + ")");
            }
        });
    }

    public void deleteStudent(Student student) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            studentDao.delete(student);
            Log.d(TAG, "Deleted student ID: " + student.getId());
        });
    }

    public LiveData<List<StudentGrade>> getGradesForRegistration(int registrationId) {
        return studentGradeDao.getGradesForRegistrationLiveData(registrationId);
    }

    public void insertGrade(StudentGrade grade) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            studentGradeDao.insert(grade);
            Log.d(TAG, "Inserted grade for registration ID: " + grade.getRegistrationId());
        });
    }

    public void updateGrade(StudentGrade grade) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            studentGradeDao.update(grade);
            Log.d(TAG, "Updated grade ID: " + grade.getId());
        });
    }

    public void deleteGrade(StudentGrade grade) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            studentGradeDao.delete(grade);
            Log.d(TAG, "Deleted grade ID: " + grade.getId());
        });
    }

    public void deleteGradesForRegistration(int registrationId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            studentGradeDao.deleteGradesForRegistration(registrationId);
            Log.d(TAG, "Deleted all grades for registration ID: " + registrationId);
        });
    }
}
