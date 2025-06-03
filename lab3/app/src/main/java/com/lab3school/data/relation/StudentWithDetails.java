package com.lab3school.data.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.lab3school.data.entity.Registration;
import com.lab3school.data.entity.School;
import com.lab3school.data.entity.Student;

import java.util.List;

public class StudentWithDetails {
    @Embedded
    public Student student;

    @Relation(
            parentColumn = "school_id",
            entityColumn = "id"
    )
    public School school;

    @Relation(
            entity = Registration.class,
            parentColumn = "id",
            entityColumn = "student_id"
    )
    public List<RegistrationWithDetails> registrationDetails;

    public String getSchoolName() {
        return school != null ? school.getName() : "N/A";
    }
}
