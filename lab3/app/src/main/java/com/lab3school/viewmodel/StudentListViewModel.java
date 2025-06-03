package com.lab3school.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.lab3school.data.relation.StudentWithDetails;
import com.lab3school.data.repository.StudentRepository;

import java.util.List;

public class StudentListViewModel extends AndroidViewModel {
    private StudentRepository studentRepository;
    private LiveData<List<StudentWithDetails>> allStudentsWithDetails;

    public StudentListViewModel(@NonNull Application application) {
        super(application);
        studentRepository = new StudentRepository(application);
        allStudentsWithDetails = studentRepository.getAllStudentsWithDetails();
    }

    public LiveData<List<StudentWithDetails>> getAllStudentsWithDetails() {
        return allStudentsWithDetails;
    }

    public void deleteStudent(StudentWithDetails studentWithDetails) {
        if (studentWithDetails != null && studentWithDetails.student != null) {
            studentRepository.deleteStudent(studentWithDetails.student);
        }
    }
}
