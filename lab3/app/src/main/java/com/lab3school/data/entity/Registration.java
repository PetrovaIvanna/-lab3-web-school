package com.lab3school.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity(tableName = "registrations",
        foreignKeys = {
                @ForeignKey(entity = Student.class,
                        parentColumns = "id",
                        childColumns = "student_id",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Subject.class,
                        parentColumns = "id",
                        childColumns = "subject_id",
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index(value = {"student_id", "subject_id"}, unique = true),
                @Index(value = "subject_id")})
public class Registration {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "student_id")
    private int studentId;
    @ColumnInfo(name = "subject_id")
    private int subjectId;

    @Ignore
    private Student student;
    @Ignore
    private Subject subject;
    @Ignore
    private List<StudentGrade> studentGrades;

    public Registration() {
        this.studentGrades = new ArrayList<>();
    }

    public Registration(int id, int studentId, int subjectId) {
        this.id = id;
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.studentGrades = new ArrayList<>();
    }

    public Registration(int studentId, int subjectId) {
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.studentGrades = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    @Ignore
    public Student getStudent() {
        return student;
    }

    @Ignore
    public void setStudent(Student student) {
        this.student = student;
    }

    @Ignore
    public Subject getSubject() {
        return subject;
    }

    @Ignore
    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    @Ignore
    public List<StudentGrade> getStudentGrades() {
        return studentGrades;
    }

    @Ignore
    public void setStudentGrades(List<StudentGrade> studentGrades) {
        this.studentGrades = studentGrades;
    }

    @Override
    public String toString() {
        return "Registration{" +
                "id=" + id +
                ", studentId=" + studentId +
                ", subjectId=" + subjectId +
                ", student=" + (student != null ? student.getFirstName() + " " + student.getLastName() : "null") +
                ", subject=" + (subject != null ? subject.getName() : "null") +
                ", studentGrades=" + studentGrades +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Registration that = (Registration) o;
        return id == that.id &&
                studentId == that.studentId &&
                subjectId == that.subjectId &&
                Objects.equals(studentGrades, that.studentGrades);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, studentId, subjectId, studentGrades);
    }
}
