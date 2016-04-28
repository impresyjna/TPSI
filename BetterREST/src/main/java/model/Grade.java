package model;

import other.GradeValue;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * Created by impresyjna on 19.04.2016.
 */
@XmlRootElement
public class Grade {
    int id;
    @NotNull
    double gradeValue;
    Date date = new Date();
    Student student;

    private static final double[] noteScale = new double[]{2.0, 3.0, 3.5, 4.0, 4.5, 5.0};

    public boolean validateNote() {
        boolean result = false;

        for(double element: noteScale) {
            if(this.gradeValue == element) {
                result = true;
            }
        }

        return result;
    }

    public Grade() {
    }

    public Grade(double gradeValue, Student student, int id) {
        this.gradeValue = gradeValue;
        this.id = id;
        this.student = student;
        this.date = new Date();
    }

    public double getGradeValue() {
        return gradeValue;
    }

    public void setGradeValue(double gradeValue) {
        this.gradeValue = gradeValue;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
