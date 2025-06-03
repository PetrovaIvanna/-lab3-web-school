package com.lab3school.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.lab3school.data.database.AppDatabase;
import com.lab3school.data.dao.SchoolDao;
import com.lab3school.data.entity.School;

import java.util.List;

public class SchoolRepository {
    private SchoolDao schoolDao;
    private LiveData<List<School>> allSchools;

    public SchoolRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        schoolDao = db.schoolDao();
        allSchools = schoolDao.getAllSchoolsLiveData();
    }

    public LiveData<List<School>> getAllSchools() {
        return allSchools;
    }

    public LiveData<School> getSchoolById(int schoolId) {
        return schoolDao.getSchoolByIdLiveData(schoolId);
    }

    public void insert(School school) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            schoolDao.insert(school);
        });
    }

    public void update(School school) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            schoolDao.update(school);
        });
    }

    public void delete(School school) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            schoolDao.delete(school);
        });
    }
}
