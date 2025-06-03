package com.lab3school.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Objects;
@Entity(tableName = "student_grades",
        foreignKeys = @ForeignKey(entity = Registration.class,
                parentColumns = "id",
                childColumns = "registration_id",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index(value = "registration_id")})
public class StudentGrade {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "registration_id")
    private int registrationId;
    @ColumnInfo(name = "grade_value")
    private int gradeValue;

    public StudentGrade() {
    }

    public StudentGrade(int registrationId, int gradeValue) {
        if (gradeValue < 1 || gradeValue > 12) throw new IllegalArgumentException("Grade must be between 1 and 12");
        this.registrationId = registrationId;
        this.gradeValue = gradeValue;
    }

    public StudentGrade(int id, int registrationId, int gradeValue) {
        this.id = id;
        this.registrationId = registrationId;
        this.gradeValue = gradeValue;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(int registrationId) {
        this.registrationId = registrationId;
    }

    public int getGradeValue() {
        return gradeValue;
    }

    public void setGradeValue(int gradeValue) {
        this.gradeValue = gradeValue;
    }

    @Override
    public String toString() {
        return "StudentGrade{" + "id=" + id + ", registrationId=" + registrationId + ", gradeValue=" + gradeValue + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        StudentGrade that = (StudentGrade) o;
        return id == that.id && registrationId == that.registrationId && gradeValue == that.gradeValue;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, registrationId, gradeValue);
    }
}