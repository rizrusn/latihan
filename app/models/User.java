// package models;

// import java.util.List;

// /**
//  * Created by rizrusn on 16/03/16.
//  */
// public class User {
//     public String fullname;
//     public Integer age;
//     public Boolean gender;
//     public Address address = new Address();
//     public List<User> friends;
    
// }

package models;
import java.util.*;
import javax.persistence.*;
import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;


@Entity
@Table(name = "User")
public class User extends Model {


	@Column
	public String name;

	@Id
	@Constraints.MinLength(3)
	@Constraints.Required
	public String username;

	@Constraints.MinLength(6)
	@Constraints.Required
	public String password;

	public static Finder<String, User> find = new Finder<String,User>(
		String.class, User.class
	);

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public List<ValidationError> validate() {
		List<ValidationError> errors = new ArrayList<ValidationError>();
		if (name.length() < 3 || name.length() > 50) {
			errors.add(new ValidationError("name", "Name must be at least 3 characters or a maximum of 50 characters"));
		}
		if (username == null) {
			errors.add(new ValidationError("username", "Username teu kengeng kosong"));
		}
		if (password == null) {
			errors.add(new ValidationError("password", "Password kedah di eusi"));
		}
		return errors.isEmpty() ? null : errors;
	}
}