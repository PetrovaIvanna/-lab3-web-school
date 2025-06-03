package com.lab3school.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "schools")
public class School {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "address")
    private String address;

    public School() {
    }

    public School(int id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }

    public School(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        School school = (School) o;
        return id == school.id && Objects.equals(name, school.name) && Objects.equals(address, school.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, address);
    }
}
