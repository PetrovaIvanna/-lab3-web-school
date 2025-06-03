package com.lab3school.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.lab3school.data.database.AppDatabase;
import com.lab3school.data.dao.SubjectDao;
import com.lab3school.data.entity.Subject;

import java.util.List;

public class SubjectRepository {
    private SubjectDao subjectDao;
    private LiveData<List<Subject>> allSubjects;

    public SubjectRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        subjectDao = db.subjectDao();
        allSubjects = subjectDao.getAllSubjectsLiveData();
    }

    public LiveData<List<Subject>> getAllSubjects() {
        return allSubjects;
    }

    public LiveData<Subject> getSubjectById(int subjectId) {
        return subjectDao.getSubjectByIdLiveData(subjectId);
    }


    public void insert(Subject subject) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            subjectDao.insert(subject);
        });
    }

    public void update(Subject subject) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            subjectDao.update(subject);
        });
    }

    public void delete(Subject subject) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            subjectDao.delete(subject);
        });
    }
}
