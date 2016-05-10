
import org.junit.*;

import static org.junit.Assert.assertEquals;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

import models.Task;
import play.libs.Json;
import play.libs.ws.*;
import play.test.WithServer;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.concurrent.CompletionStage;
import javax.inject.Inject;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;

public class ControllersTest extends WithServer {

    final long id = 123456;
    final String name = "TaskTest";
    final boolean done = true;
    @Inject
    WSClient ws;

    @Test
    public void testAddValidUser() {
        Task taskTest = new Task();

        taskTest.id = id;
        taskTest.name = name;
        taskTest.done = done;
        taskTest.save();

        Task c =  Task.find.byId(id);

        // assertEquals(200, response.getStatus());

        assertEquals(taskTest.id, c.id);
        assertEquals("TaskTest", c.name);
        assertEquals(true, c.done);

    }

//    @Test
//    public void testAddInvalidUser() {
//        Task taskTest = new Task();
//
//        taskTest.id = id;
//        taskTest.name = "a";
//        taskTest.done = done;
//        taskTest.save();
//
//        assertEquals("Name must be at least 3 characters or a maximum of 100 characters", JsPath.json.findPath("errors").findPath("name").asText());
//
//    }

    @Test
    public  void  testValidPostTask(){
//        running(fakeApplication(), new Runnable() {
//                    public void run() {
                        JsonNode task = Json.newObject()
                                .put("id", id)
                                .put("name", "Task test json")
                                .put("done", true);
//                        ws.url("http://localhost:9000/json/task").post(task);

//                        Task c = Task.find.byId(id);
//
//                        assertEquals("Task test json", c.name);
//                        assertEquals(true, c.done);
//                    }
//                });
//        WSResponse response = (WSResponse) ws.url("http://www.google.com").get();
//        ws.url("http://localhost:9000/json/task").post(task);
//        WSResponse response = (WSResponse) ws.url("http://localhost:9000/json/task").get();
//        JsonNode json = Json.parse(response.getBody());

//        ws.url("http://localhost:9000/json/task").post(task);
//        WSRequest request = ws.url("http://example.com");
//
//        Task c =  Task.find.byId(id);
////
////       assertEquals(200, response.getStatus());
//
//        assertEquals("Task test json", c.name);
//        assertEquals(true, c.done);
//        assertEquals(1,1);
    }



}
