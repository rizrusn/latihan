import org.junit.*;
import static play.test.Helpers.*;
import models.*;
import play.test.*;
import static org.junit.Assert.*;

public class ModelsTest {

	// final char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
 //            .toCharArray();

 //    final char[] alphanumeric = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789,@.:"
 //            .toCharArray();

 //    final char[] numbers = "0123456789"
 //            .toCharArray();



 //    @Test
 //    public void testCreateTask() {

 //        Task task = createTaskFull(
 //                "random",
 //                "random",
 //                "random"
 //        );

 //        assertNotNull(task);

 //        Task taskCreated = Task.findById(task.getId());

 //        assertEquals(task.getName(), taskCreated.getName());
 //        assertEquals(task.getDone(), taskCreated.getDone());

 //        Task.collections().remove(task.getId());
 //    }

    @Test
    public void testSaveUser() {
        running(fakeApplication(), new Runnable() {
            public void run() {
                User user = new User();
                user.name = "Apple";
                user.save();
                assertNotNull(user.getName());
                assertEquals("Apple", user.getName());
            }
        });
    }



}
