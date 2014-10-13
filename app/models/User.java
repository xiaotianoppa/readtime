package models;
import play.*;
import play.db.jpa.*;
import javax.persistence.*;
import java.util.*;

@Entity
public class User extends Model {
    
public String username;
public String password;
public String email;

}