package model;

import org.bson.types.ObjectId;
import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import other.ObjectIdJaxbAdapter;
import rest.CourseResource;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.LinkedList;

/**
 * Created by impresyjna on 19.04.2016.
 */
@XmlRootElement
@Entity
public class Course {
    @InjectLinks({
            @InjectLink(resource = CourseResource.class, method = "getOneCourse", style = InjectLink.Style.ABSOLUTE,
                    bindings = @Binding(name = "courseId", value = "${instance.courseId}"), rel = "self"),
            @InjectLink(resource = CourseResource.class, method = "getGrades", style = InjectLink.Style.ABSOLUTE,
                    bindings = {
                            @Binding(name = "courseId", value = "${instance.courseId}"),
                            @Binding(name = "direction", value = ""),
                            @Binding(name = "note", value = "")
                    }, rel = "grades")
    })
    @XmlElement(name = "link")
    @XmlElementWrapper(name = "links")
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    List<Link> links;

    @XmlJavaTypeAdapter(ObjectIdJaxbAdapter.class)
    @Id
    ObjectId courseId;

    @NotNull
    String name;
    @NotNull
    String teacher;
    @Embedded
    private List<Grade> grades = new ArrayList<>();

    public Course() {
    }

    public Course(String name, String teacher) {
        this.name = name;
        this.teacher = teacher;
        this.courseId = new ObjectId();
    }

    @XmlTransient
    public ObjectId getCourseId() {
        return courseId;
    }

    public void setCourseId(ObjectId courseId) {
        this.courseId = courseId;
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

    public List<Grade> getStudentGradesList(long index) {
        return grades.stream().filter(grade -> grade.getStudent().getIndex() == index).collect(Collectors.toList());
    }

    public Map<Integer, Grade> getStudentGradesMape(long index) {
        return grades.stream().filter(grade -> grade.getStudent().getIndex() == index).
                collect(Collectors.toMap(Grade::getGradeId, Function.identity()));
    }
}
