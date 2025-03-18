package model;

import java.time.LocalDate;

public class Student extends Person {
    private String className;
    private double points;

    public Student() {
    }

    public Student(long id, String fullName, String email, LocalDate dateOfBirth, String className, double points) {
        super(id, fullName, email, dateOfBirth);
        this.className = className;
        this.points = points;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public double getPoints() {
        return points;
    }

    public void setPoints(double points) {
        this.points = points;
    }

    public String getInfo(){
        return String.format("%s,%s,%s,%s,%s,%s\n", this.getId(), this.getFullName(), this.getEmail(), this.getDateOfBirth(), this.getClassName(), this.getPoints());
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + getId() +
                ", fullName='" + getFullName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", dateOfBirth=" + getDateOfBirth() +
                ", className='" + className + '\'' +
                ", points=" + points +
                '}';
    }
}
