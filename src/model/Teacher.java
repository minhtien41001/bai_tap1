package model;

import java.time.LocalDate;

public class Teacher extends Person {
    private String specialize;

    public Teacher() {
    }

    public Teacher(long id, String fullName, String email, LocalDate dateOfBirth, String specialize) {
        super(id, fullName, email, dateOfBirth);
        this.specialize = specialize;
    }

    public String getSpecialize() {
        return specialize;
    }

    public void setSpecialize(String specialize) {
        this.specialize = specialize;
    }

    public String getInfo(){
        return String.format("%s,%s,%s,%s,%s\n",this.getId(), this.getFullName(), this.getEmail(), this.getDateOfBirth(), this.getSpecialize());
    }

    @Override
    public String toString() {
        return"Teacher {" +
                "id=" + getId() +
                ", fullName='" + getFullName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", dateOfBirth=" + getDateOfBirth() +
                ", className='" + specialize + '\'' +
                '}';
    }
}
