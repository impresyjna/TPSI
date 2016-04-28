package rest;

import model.Course;
import model.Student;
import other.Model;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by impresyjna on 20.04.2016.
 */
@Path("/courses")
public class CourseResource {
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<Course> getAllCourses() {
        System.out.println("Courses index");
        List<Course> courses = Model.getInstance().getCourses();
        return courses;
    }

    @GET
    @Path("/{courseId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getOneCourse(@PathParam("courseId") final int courseId) {
        Course returnedCourse = null;
        for (Course course : Model.getInstance().getCourses()) {
            if (course.getId() == courseId) {
                returnedCourse = course;
                break;
            }
        }

        if (returnedCourse == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("404 Not found").build());
        }

        return Response.ok(returnedCourse).build();
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createCourse(@Valid Course course, @Context UriInfo uriInfo) {
        course.setId(Model.getInstance().getCourseIndex());
        if (course.getGrades() == null) {
            course.setGrades(new ArrayList<>());
        }
        Model.getInstance().getCourses().add(course);

        URI uri = uriInfo.getAbsolutePathBuilder().path(Integer.toString(course.getId())).build();
        return Response.created(uri).entity(course).build();
    }

    @PUT
    @Path("/{courseId}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateCourse(@PathParam("courseId") final int courseId, @Valid Course course) {
        for (int i = 0; i < Model.getInstance().getCourses().size(); i++) {
            Course tempCourse = Model.getInstance().getCourses().get(i);
            if (tempCourse.getId() == courseId) {
                if (course.getGrades().isEmpty()) {
                    course.setGrades(tempCourse.getGrades());
                }
                Model.getInstance().getCourses().set(i, course);
            }
        }

        return Response.status(Response.Status.OK).entity(course).build();
    }

    @Path("/{courseId}")
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteStudent(@PathParam("courseId") final int courseId) {
        Response response = null;
        for (Course course : Model.getInstance().getCourses()) {
            if (course.getId() == courseId) {
                Model.getInstance().getCourses().remove(course);
                response = Response.ok("Course  " + courseId + " removed").build();
                break;
            }
        }

        if (response == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("404 Not found").build());
        }

        return response;
    }

}
