package rest;

import model.Course;
import model.Grade;
import model.GradeIterator;
import model.Student;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import other.DbSingleton;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by impresyjna on 20.04.2016.
 */
@Path("/students/{index}/courses/{courseId}/grades")
public class GradeResource {
    DbSingleton dbSingleton = DbSingleton.getInstance();

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<Grade> getAllGrades(@PathParam("index") final long index, @PathParam("courseId") final ObjectId courseId,
                                    @DefaultValue("more") @QueryParam("direction") String direction, @QueryParam("grade_value") Double gradeValue) {
        Course returnedCourse = null;
        returnedCourse = dbSingleton.getDs().get(Course.class, courseId);
        Student returnedStudent = null;
        Query q = dbSingleton.getDs().createQuery(Student.class).filter("index =", index);
        returnedStudent = (Student) q.get();
        if (returnedCourse == null || returnedStudent == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("404 Not found").build());
        }
        List<Grade> grades = new ArrayList<>();
        for (Grade grade : returnedCourse.getGrades()) {
            if (grade.getStudent().getIndex() == index) {
                grades.add(grade);
            }
        }
        if (gradeValue != null) {
            if (Grade.validateNoteWithValue(gradeValue)) {
                if (direction.equals("more")) {
                    grades = grades.stream().filter(grade -> grade.getGradeValue() <= gradeValue).collect(Collectors.toList());
                }
                if (direction.equals("less")) {
                    grades = grades.stream().filter(grade -> grade.getGradeValue() > gradeValue).collect(Collectors.toList());
                }
            }
        }

        return grades;
    }

    @GET
    @Path("/{gradeId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getOneGrade(@PathParam("index") final long index, @PathParam("courseId") final ObjectId courseId, @PathParam("gradeId") final int gradeId) {
        Course returnedCourse = null;
        returnedCourse = dbSingleton.getDs().get(Course.class, courseId);
        List<Grade> grades = new ArrayList<>();
        for (Grade grade : returnedCourse.getGrades()) {
            if (grade.getStudent().getIndex() == index) {
                grades.add(grade);
            }
        }
        Grade returnedGrade = null;
        for (Grade grade : grades) {
            if (grade.getGradeId() == gradeId) {
                returnedGrade = grade;
                break;
            }
        }

        if (returnedGrade == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("404 Not found").build());
        }

        return Response.ok(returnedGrade).build();
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createGrade(@PathParam("index") final long index, @PathParam("courseId") final ObjectId courseId, @Valid Grade grade, @Context UriInfo uriInfo) {
        Course choosenCourse = null;
        choosenCourse = dbSingleton.getDs().get(Course.class, courseId);
        if (grade.validateNote()) {
            Student studentForGrade = null;
            studentForGrade = dbSingleton.getDs().createQuery(Student.class).filter("index =", index).get();
            grade.setStudent(studentForGrade);
            GradeIterator gradeIterator = dbSingleton.getDs().createQuery(GradeIterator.class).get();
            grade.setGradeId(gradeIterator.getValue());
            grade.setCourseId(choosenCourse.getCourseId().toString());
            Query<GradeIterator> qPom = dbSingleton.getDs().createQuery(GradeIterator.class);
            UpdateOperations<GradeIterator> pomOps;
            pomOps = dbSingleton.getDs().createUpdateOperations(GradeIterator.class).set("value", gradeIterator.getValue() + 1);
            dbSingleton.getDs().update(qPom, pomOps);
            choosenCourse.getGrades().add(grade);
            Query<Course> q = dbSingleton.getDs().createQuery(Course.class).filter("_id =", courseId);
            UpdateOperations<Course> ops;
            ops = dbSingleton.getDs().createUpdateOperations(Course.class).set("grades", choosenCourse.getGrades());
            dbSingleton.getDs().update(q, ops);

            URI uri = uriInfo.getAbsolutePathBuilder().path(Integer.toString(grade.getGradeId())).build();
            return Response.created(uri).entity(grade).build();
        }
        return Response.status(Response.Status.NOT_MODIFIED).entity(grade).build();
    }

    @PUT
    @Path("/{gradeId}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateGrade(@PathParam("index") final long index, @PathParam("courseId") final ObjectId courseId, @Valid Grade grade, @PathParam("gradeId") final int gradeId) {
        if(!grade.validateNote()) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).
                    entity("Note is not valid").build());
        }
        Datastore datastore = dbSingleton.getDs();
        Course course = datastore.find(Course.class).field("_id").equal(courseId).get();
        Student student = datastore.find(Student.class).field("index").equal(index).get();
        if(course == null || student == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Not found").build());
        }

        Grade gradeToUpdate = course.getStudentGradesMape(index).get(gradeId);
        if(gradeToUpdate == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Not found").build());
        } else {
            grade.setGradeId(gradeId);
            grade.setStudent(gradeToUpdate.getStudent());
            grade.setCourseId(courseId.toString());
        }
        Collections.replaceAll(course.getGrades(), gradeToUpdate, grade);
        datastore.save(course);

        return Response.ok(grade).build();
    }

    @Path("/{gradeId}")
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteGrade(@PathParam("index") final long index, @PathParam("courseId") final ObjectId courseId, @PathParam("gradeId") final int gradeId) {
        Response response = null;
        Course choosenCourse = null;
        choosenCourse = dbSingleton.getDs().get(Course.class, courseId);
        if (choosenCourse != null) {
            List<Grade> grades = choosenCourse.getGrades();
            for (Grade grade1 : grades) {
                if (grade1.getGradeId() == gradeId) {
                    grades.remove(grade1);
                    response = Response.ok("Grade  " + gradeId + " removed").build();
                    Query<Course> q = dbSingleton.getDs().createQuery(Course.class).filter("_id =", courseId);
                    UpdateOperations<Course> ops;
                    ops = dbSingleton.getDs().createUpdateOperations(Course.class).set("grades", choosenCourse.getGrades());
                    dbSingleton.getDs().update(q, ops);
                    break;
                }
            }
        }

        if (response == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("404 Not found").build());
        }

        return response;
    }
}
