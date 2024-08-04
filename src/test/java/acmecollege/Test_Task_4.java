/********************************************************************************************************2*4*w*
 * File:  Test_Task_4.java
 * @author Fereshteh Rohani
 */

package acmecollege;

import static acmecollege.utility.MyConstants.APPLICATION_API_VERSION;
import static acmecollege.utility.MyConstants.APPLICATION_CONTEXT_ROOT;
import static acmecollege.utility.MyConstants.DEFAULT_ADMIN_USER;
import static acmecollege.utility.MyConstants.DEFAULT_ADMIN_USER_PASSWORD;
import static acmecollege.utility.MyConstants.DEFAULT_USER;
import static acmecollege.utility.MyConstants.DEFAULT_USER_PASSWORD;
import static acmecollege.utility.MyConstants.STUDENT_RESOURCE_NAME;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.net.URI;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import acmecollege.entity.Student;

public class Test_Task_4 {
    private static final Logger logger = LogManager.getLogger(Test_Task_4.class);

    static final String HTTP_SCHEMA = "http";
    static final String HOST = "localhost";
    static final int PORT = 8080;

    static URI uri;
    static HttpAuthenticationFeature adminAuth;
    static HttpAuthenticationFeature userAuth;

    @BeforeAll
    public static void oneTimeSetUp() throws Exception {
        logger.debug("oneTimeSetUp");
        uri = UriBuilder
            .fromUri(APPLICATION_CONTEXT_ROOT + APPLICATION_API_VERSION)
            .scheme(HTTP_SCHEMA)
            .host(HOST)
            .port(PORT)
            .build();
        adminAuth = HttpAuthenticationFeature.basic(DEFAULT_ADMIN_USER, DEFAULT_ADMIN_USER_PASSWORD);
        userAuth = HttpAuthenticationFeature.basic(DEFAULT_USER, DEFAULT_USER_PASSWORD);
    }

    protected WebTarget webTarget;

    @BeforeEach
    public void setUp() {
        Client client = ClientBuilder.newClient(
            new ClientConfig().register(new LoggingFeature()));
        webTarget = client.target(uri);

        // Add test student record before each test
        Student newStudent = new Student();
        newStudent.setId(1); // Set ID if applicable
        newStudent.setFirstName("Test Student");
        Response response = webTarget
            .register(adminAuth)
            .path(STUDENT_RESOURCE_NAME)
            .request()
            .post(Entity.json(newStudent));
        assertThat(response.getStatus(), is(201));
    }

    @AfterEach
    public void tearDown() {
        // Delete test student record after each test
        Response response = webTarget
            .register(adminAuth)
            .path(STUDENT_RESOURCE_NAME + "/1") // Assuming student ID is 1
            .request()
            .delete();
        assertThat(response.getStatus(), is(204));
    }

    // Test: admin_role can populate all students
    @Test
    public void test01_populate_all_students_with_adminrole() {
        Response response = webTarget
            .register(adminAuth)
            .path(STUDENT_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        List<Student> students = response.readEntity(new GenericType<List<Student>>(){});
        assertFalse(students.isEmpty(), "Students list should not be empty");
    }

    // Test: user_role cannot populate all students
    @Test
    public void test02_populate_all_students_with_userrole() {
        Response response = webTarget
            .register(userAuth)
            .path(STUDENT_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(403));
    }

    // Test: admin_role can populate a student by id
    @Test
    public void test03_populate_student_by_id_with_adminrole() {
        Response response = webTarget
            .register(adminAuth)
            .path(STUDENT_RESOURCE_NAME + "/1") // Assuming student ID is 1
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        Student student = response.readEntity(Student.class);
        assertThat(student.getId(), is(1));
    }

    // Test: user_role can populate their own student details
    @Test
    public void test04_populate_student_by_id_with_userrole() {
        Response response = webTarget
            .register(userAuth)
            .path(STUDENT_RESOURCE_NAME + "/1") // Assuming student ID is 1
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
    }

    // Test: user_role cannot populate student details of another user
    @Test
    public void test05_populate_another_user_student_by_id_with_userrole() {
        Response response = webTarget
            .register(userAuth)
            .path(STUDENT_RESOURCE_NAME + "/2") // Assuming student ID 2 belongs to another user
            .request()
            .get();
        assertThat(response.getStatus(), is(403));
    }

    // Test: admin_role can add a new student
    @Test
    public void test06_add_student_with_adminrole() {
        Student newStudent = new Student();
        newStudent.setFirstName("New Student");
        Response response = webTarget
            .register(adminAuth)
            .path(STUDENT_RESOURCE_NAME)
            .request()
            .post(Entity.json(newStudent));
        assertThat(response.getStatus(), is(201));
    }

    // Test: user_role cannot add a new student
    @Test
    public void test07_add_student_with_userrole() {
        Student newStudent = new Student();
        newStudent.setFirstName("New Student");
        Response response = webTarget
            .register(userAuth)
            .path(STUDENT_RESOURCE_NAME)
            .request()
            .post(Entity.json(newStudent));
        assertThat(response.getStatus(), is(403));
    }

    // Test: admin_role can delete a student
    @Test
    public void test08_delete_student_with_adminrole() {
        Response response = webTarget
            .register(adminAuth)
            .path(STUDENT_RESOURCE_NAME + "/1") // Assuming student ID is 1
            .request()
            .delete();
        assertThat(response.getStatus(), is(204));
    }

    // Test: user_role cannot delete a student
    @Test
    public void test09_delete_student_with_userrole() {
        Response response = webTarget
            .register(userAuth)
            .path(STUDENT_RESOURCE_NAME + "/1") // Assuming student ID is 1
            .request()
            .delete();
        assertThat(response.getStatus(), is(403));
    }

    // Test: admin_role can update a student
    @Test
    public void test10_update_student_with_adminrole() {
        Student updatedStudent = new Student();
        updatedStudent.setFirstName("Updated Student");
        Response response = webTarget
            .register(adminAuth)
            .path(STUDENT_RESOURCE_NAME + "/1") // Assuming student ID is 1
            .request()
            .put(Entity.json(updatedStudent));
        assertThat(response.getStatus(), is(200));
    }

    // Test: user_role cannot update a student
    @Test
    public void test11_update_student_with_userrole() {
        Student updatedStudent = new Student();
        updatedStudent.setFirstName("Updated Student");
        Response response = webTarget
            .register(userAuth)
            .path(STUDENT_RESOURCE_NAME + "/1") // Assuming student ID is 1
            .request()
            .put(Entity.json(updatedStudent));
        assertThat(response.getStatus(), is(403));
    }

    // Test: admin_role can get list of all club memberships
    @Test
    public void test12_all_club_memberships_with_adminrole() {
        Response response = webTarget
            .register(adminAuth)
            .path("clubMemberships")
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
    }

    // Test: user_role cannot get list of all club memberships
    @Test
    public void test13_all_club_memberships_with_userrole() {
        Response response = webTarget
            .register(userAuth)
            .path("clubMemberships")
            .request()
            .get();
        assertThat(response.getStatus(), is(403));
    }

    // Test: admin_role can associate a Peer Tutor with a student
    @Test
    public void test14_associate_peer_tutor_with_student() {
        Response response = webTarget
            .register(adminAuth)
            .path(STUDENT_RESOURCE_NAME + "/1/peerTutor/1") // Assuming student ID 1 and peer tutor ID 1
            .request()
            .post(null);
        assertThat(response.getStatus(), is(200));
    }

    // Test: user_role cannot associate a Peer Tutor with a student
    @Test
    public void test15_associate_peer_tutor_with_student_with_userrole() {
        Response response = webTarget
            .register(userAuth)
            .path(STUDENT_RESOURCE_NAME + "/1/peerTutor/1") // Assuming student ID 1 and peer tutor ID 1
            .request()
            .post(null);
        assertThat(response.getStatus(), is(403));
    }
}
