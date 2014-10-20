package controllers;

import controllers.CRUD;
import controllers.CRUD.For;
import models.User;
import play.mvc.With;

@CRUD.For(User.class)
public class UserAdmin extends AdminBase {
    
}
