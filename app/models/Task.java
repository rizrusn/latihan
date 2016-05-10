package models;

import java.util.*;
import javax.persistence.*;

import com.avaje.ebean.Model;
import play.Play;
import play.data.format.*;

@Entity
@Table(name = "Task")
public class Task extends Model {

    @Id
    public long id;

    public String name;

    public boolean done;

    @Formats.DateTime(pattern = "dd/MM/yyyy")
    public Date dueDate = new Date();

    public static Finder<Long, Task> find = new Finder<Long, Task>(Long.class, Task.class);

    public  long getId(){
        return  id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
     }

    public void setName(String name) {
        this.name = name;
     }

    public boolean getDone() {
        return done;
     }

    public void setDone(boolean done) {
        this.done = done;
     }



    // public static Model.Finder<Long, Task> find = new Model.Finder<Long, Task>(Task.class);

    public String validate() {
        if (name.length() < 3 || name.length() > 100) {
            return "Name must be at least 3 characters or a maximum of 100 characters";
        }
        return null;
    }
}

