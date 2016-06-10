package rest;

import com.mongodb.WriteResult;
import model.*;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import other.DbSingleton;
import other.Model;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Path("/students")
public class StudentResource {
    private DbSingleton dbSingleton = DbSingleton.getInstance();

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<Student> getAllStudents(@QueryParam("name") String name,
                                        @QueryParam("surname") String surname,
                                        @DefaultValue("equals") @QueryParam("direction") String direction,
                                        @QueryParam("date") Date date) {
        System.out.println("Students index");
        Query<Student> q = dbSingleton.getDs().createQuery(Student.class);
        List<Student> students = q.asList();
        if (name != null) {
            students = students.stream().filter(student -> student.getName().equals(name)).
                    collect(Collectors.toList());
        }
        if (surname != null) {
            students = students.stream().filter(student -> student.getSurname().equals(surname)).
                    collect(Collectors.toList());
        }
        if (date != null) {
            if (direction.equals("before")) {
                students = students.stream().filter(student -> student.getDateOfBirth().before(date))
                        .collect(Collectors.toList());
            }
            if (direction.equals("equals")) {
                students = students.stream().filter(student -> student.getDateOfBirth().equals(date))
                        .collect(Collectors.toList());
            }
            if (direction.equals("after")) {
                students = students.stream().filter(student -> student.getDateOfBirth().after(date))
                        .collect(Collectors.toList());
            }

        }
        return students;
    }

    @GET
    @Path("/{index}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getOneStudent(@PathParam("index") final long index) {
        Student returnedStudent = null;
        Query q = dbSingleton.getDs().createQuery(Student.class).filter("index =", index);
        returnedStudent = (Student) q.get();
        if (returnedStudent == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("404 Not found").build());
        }

        return Response
                .ok(returnedStudent)
                .build();
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createStudent(@Valid Student student, @Context UriInfo uriInfo) {
        StudentIterator studentIterator = dbSingleton.getDs().createQuery(StudentIterator.class).get();
        student.setIndex(studentIterator.getValue()+1);
        dbSingleton.getDs().save(student);
        Query<StudentIterator> qPom = dbSingleton.getDs().createQuery(StudentIterator.class);
        UpdateOperations<StudentIterator> pomOps;
        pomOps = dbSingleton.getDs().createUpdateOperations(StudentIterator.class).set("value", studentIterator.getValue() + 1);
        dbSingleton.getDs().update(qPom, pomOps);
        URI uri = uriInfo.getAbsolutePathBuilder().path(Long.toString(student.getIndex())).build();
        return Response.created(uri).entity(student).build();
    }

    @PUT
    @Path("/{index}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateStudent(@PathParam("index") final long index, @Valid Student student) {
        Query query = dbSingleton.getDs().createQuery(Student.class).filter("index =", index);
        UpdateOperations<Student> ops;
        ops = dbSingleton.getDs().createUpdateOperations(Student.class).set("index", student.getIndex());
        dbSingleton.getDs().update(query, ops);
        ops = dbSingleton.getDs().createUpdateOperations(Student.class).set("name", student.getName());
        dbSingleton.getDs().update(query, ops);
        ops = dbSingleton.getDs().createUpdateOperations(Student.class).set("surname", student.getSurname());
        dbSingleton.getDs().update(query, ops);
        ops = dbSingleton.getDs().createUpdateOperations(Student.class).set("dateOfBirth", student.getDateOfBirth());
        dbSingleton.getDs().update(query, ops);

        return Response.status(Response.Status.OK).entity(student).build();
    }

    @Path("/{index}")
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteStudent(@PathParam("index") final long index) {
        Student student = dbSingleton.getDs().find(Student.class).field("index").equal(index).get();

        if(student == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Not found").build());
        }

        List<Course> courses = dbSingleton.getDs().find(Course.class).asList();
        for(Course course: courses) {
            course.getGrades().removeAll(course.getStudentGradesList(index));
            dbSingleton.getDs().save(course);
        }
        dbSingleton.getDs().delete(student);

        return Response.ok("Student with index " + index + " removed").build();
    }


    @Path("/{index}/grades")
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<Grade> getGrades(@PathParam("index") final long index) {
        List<Course> courses = dbSingleton.getDs().find(Course.class).asList();

        List<Grade> grades = new ArrayList<>();
        courses.stream().forEach(course -> grades.addAll(course.getStudentGradesList(index)));

        return grades;
    }
}
