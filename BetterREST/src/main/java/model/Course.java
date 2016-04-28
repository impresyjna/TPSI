package model;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Path;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by impresyjna on 19.04.2016.
 */
@XmlRootElement
public class Course {
    int id;
    @NotNull
    String name;
    @NotNull
    String teacher;
    List<Grade> grades = new ArrayList<>();

    public Course() {
    }

    public Course(int id, String name, String teacher) {
        this.id = id;
        this.name = name;
        this.teacher = teacher;
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

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public List<Grade> getGrades() {
        return grades;
    }

    public void setGrades(List<Grade> grades) {
        this.grades = grades;
    }
}
