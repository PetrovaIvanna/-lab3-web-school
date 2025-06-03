package com.lab3school.data.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.lab3school.data.entity.Registration;
import com.lab3school.data.entity.StudentGrade;
import com.lab3school.data.entity.Subject;

import java.util.List;

public class RegistrationWithDetails {
    @Embedded
    public Registration registration;

    @Relation(
            parentColumn = "subject_id",
            entityColumn = "id"
    )
    public Subject subject;

    @Relation(
            parentColumn = "id",
            entityColumn = "registration_id"
    )
    public List<StudentGrade> grades;

    public String getSubjectName() {
        return subject != null ? subject.getName() : "N/A";
    }

    public String getGradesAsString() {
        if (grades == null || grades.isEmpty()) {
            return "No grades";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < grades.size(); i++) {
            sb.append(grades.get(i).getGradeValue());
            if (i < grades.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Registration: " + (subject != null ? subject.getName() : "Unknown Subject") +
                ", Grades: [" + getGradesAsString() + "]";
    }
}
