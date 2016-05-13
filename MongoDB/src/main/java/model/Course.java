package model;

import org.bson.types.ObjectId;
import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import other.ObjectIdJaxbAdapter;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by impresyjna on 19.04.2016.
 */
@XmlRootElement
@Entity
public class Course {
    /* @InjectLinks({
            @InjectLink(resource = rest.CourseResource.class, rel = "parent_course", method = "getAllCourses", style = InjectLink.Style.ABSOLUTE),
            @InjectLink(resource = rest.CourseResource.class, rel = "self_course", method = "getOneCourse", style = InjectLink.Style.ABSOLUTE,
                    bindings = {@Binding(name = "courseId", value = "${instance.id}")})
    }) */
    @XmlElement(name = "link")
    @XmlElementWrapper(name = "links")
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    List<Link> links;

    @XmlJavaTypeAdapter(ObjectIdJaxbAdapter.class)
    @Id
    ObjectId id;

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
        this.id = new ObjectId();
    }

    @XmlTransient
    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
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
