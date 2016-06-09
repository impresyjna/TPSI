package rest;

import com.mongodb.WriteResult;
import model.Course;
import model.Grade;
import org.bson.types.ObjectId;
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
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by impresyjna on 20.04.2016.
 */
@Path("/courses")
public class CourseResource {
    DbSingleton dbSingleton = DbSingleton.getInstance();

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<Course> getAllCourses(@QueryParam("teacher") String teacher) {
        System.out.println("Courses index");
        Query<Course> q = dbSingleton.getDs().createQuery(Course.class);
        List<Course> courses = q.asList();
        if (teacher != null) {
            courses = courses.stream().filter(course -> course.getTeacher().equals(teacher)).
                    collect(Collectors.toList());
        }
        return courses;
    }

    @GET
    @Path("/{courseId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getOneCourse(@PathParam("courseId") final ObjectId courseId) {
        Course returnedCourse = null;
        returnedCourse = dbSingleton.getDs().get(Course.class, courseId);
        if (returnedCourse == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("404 Not found").build());
        }

        return Response.ok(returnedCourse).build();
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createCourse(@Valid Course course, @Context UriInfo uriInfo) {
        course.setGrades(new ArrayList<>());
        dbSingleton.getDs().save(course);

        URI uri = uriInfo.getAbsolutePathBuilder().path(course.getCourseId().toString()).build();
        return Response.created(uri).entity(course).build();
    }

    @PUT
    @Path("/{courseId}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateCourse(@PathParam("courseId") final ObjectId courseId, @Valid Course course) {
        Query<Course> q = dbSingleton.getDs().createQuery(Course.class).filter("_id =", courseId);
        UpdateOperations<Course> ops;
        ops = dbSingleton.getDs().createUpdateOperations(Course.class).set("name", course.getName());
        dbSingleton.getDs().update(q, ops);
        ops = dbSingleton.getDs().createUpdateOperations(Course.class).set("teacher", course.getTeacher());
        dbSingleton.getDs().update(q, ops);

        return Response.status(Response.Status.OK).entity(q.get()).build();
    }

    @Path("/{courseId}")
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteStudent(@PathParam("courseId") final ObjectId courseId) {
        Response response = null;
        WriteResult wr = dbSingleton.getDs().delete(dbSingleton.getDs().get(Course.class, courseId));
        if (wr.getN() == 1) {
            response = Response.ok("Course  " + courseId + " removed").build();
        }
        if (response == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("404 Not found").build());
        }

        return response;
    }

    @Path("/{courseId}/grades")
    @GET
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<Grade> getGrades(@PathParam("courseId") final ObjectId courseId,
                                 @DefaultValue("1") @QueryParam("direction") int direction, @QueryParam("note") Double note) {
        Course course = dbSingleton.getDs().get(Course.class, courseId);
        if (course == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Not found").build());
        }

        List<Grade> grades = course.getGrades();
        if (note != null) {
            if (Grade.validateNoteWithValue(note)) {
                switch (direction) {
                    case -1:
                        grades = grades.stream().filter(grade -> grade.getGradeValue() <= note).collect(Collectors.toList());
                        break;
                    case 1:
                        grades = grades.stream().filter(grade -> grade.getGradeValue() >= note).collect(Collectors.toList());
                        break;
                    default:
                        break;
                }
            }
        }

        return grades;
    }

}
