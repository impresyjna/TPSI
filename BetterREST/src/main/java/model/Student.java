package model;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by impresyjna on 19.04.2016.
 */
@XmlRootElement
public class Student {
    @NotNull
    long index;
    @NotNull
    String name;
    @NotNull
    String surname;
    @NotNull
    String birthDate;

    public Student() {
    }

    public Student(long index, String name, String surname, String birthDate) {
        this.index = index;
        this.name = name;
        this.surname = surname;
        this.birthDate = birthDate;
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }
}
