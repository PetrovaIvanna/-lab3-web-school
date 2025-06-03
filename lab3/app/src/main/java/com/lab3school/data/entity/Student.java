package com.lab3school.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.time.LocalDate;
import java.util.Objects;

@Entity(tableName = "students",
        foreignKeys = @ForeignKey(entity = School.class,
                parentColumns = "id",
                childColumns = "school_id",
                onDelete = ForeignKey.SET_NULL),
        indices = {@Index(value = "school_id")})
public class Student {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "first_name")
    private String firstName;
    @ColumnInfo(name = "last_name")
    private String lastName;
    @ColumnInfo(name = "date_of_birth")
    private LocalDate dateOfBirth;
    @ColumnInfo(name = "school_id")
    private int schoolId;

    @ColumnInfo(name = "class_year")
    private int classYear;

    @ColumnInfo(name = "class_letter")
    private String classLetter;
    @Ignore
    private School school;

    public Student() {
    }

    public Student(int id, String firstName, String lastName, LocalDate dateOfBirth, int schoolId, int classYear, String classLetter) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.schoolId = schoolId;
        this.classYear = classYear;
        this.classLetter = classLetter;
    }

    public Student(String firstName, String lastName, LocalDate dateOfBirth, int schoolId, int classYear, String classLetter) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.schoolId = schoolId;
        this.classYear = classYear;
        this.classLetter = classLetter;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public int getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(int schoolId) {
        if (this.schoolId != schoolId) {
            this.schoolId = schoolId;
            if (this.school != null && this.school.getId() != schoolId) {
                this.school = null;
            }
        }
    }

    public int getClassYear() {
        return classYear;
    }

    public void setClassYear(int classYear) {
        this.classYear = classYear;
    }

    public String getClassLetter() {
        return classLetter;
    }

    public void setClassLetter(String classLetter) {
        this.classLetter = classLetter;
    }

    @Ignore
    public School getSchool() {
        return school;
    }

    @Ignore
    public void setSchool(School school) {
        this.school = school;
        this.schoolId = (school != null) ? school.getId() : 0;
    }

    @Override
    public String toString() {
        return "Student{" + "id=" + id + ", firstName='" + firstName + '\'' + ", lastName='" + lastName + '\''
                + ", dateOfBirth=" + dateOfBirth + ", schoolId=" + schoolId + ", schoolName="
                + (school != null ? school.getName() : "N/A") + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Student student = (Student) o;
        return id == student.id &&
                schoolId == student.schoolId &&
                classYear == student.classYear &&
                Objects.equals(firstName, student.firstName) &&
                Objects.equals(lastName, student.lastName) &&
                Objects.equals(dateOfBirth, student.dateOfBirth) &&
                Objects.equals(classLetter, student.classLetter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, dateOfBirth, schoolId, classYear, classLetter);
    }
}
