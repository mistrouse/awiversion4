package controllers;

import play.*;
import play.mvc.*;
import play.data.*;
import models.*;
import securesocial.core.Identity;
import securesocial.core.java.SecureSocial;

import views.html.*;

public class Application extends Controller {

    static Form<Task> taskForm = Form.form(Task.class);

    @SecureSocial.SecuredAction
    public static Result index() {
        return redirect(routes.Application.tasks());
    }

    @SecureSocial.SecuredAction
    public static Result tasks() {
        return ok(
                views.html.index.render(Task.all(), taskForm)
        );
    }

    @SecureSocial.SecuredAction
    public static Result newTask() {
        Form<Task> filledForm = taskForm.bindFromRequest();
        if(filledForm.hasErrors()) {
            return badRequest(
                    views.html.index.render(Task.all(), filledForm)
            );
        } else {
            Task.create(filledForm.get());
            return redirect(routes.Application.tasks());
        }
    }

    @SecureSocial.SecuredAction
    public static Result deleteTask(Long id) {
        Task.delete(id);
        return redirect(routes.Application.tasks());

    }


    @SecureSocial.SecuredAction
    public static Result update(Long id) {

    Form<Task> filledForm = taskForm.bindFromRequest();

        if(filledForm.hasErrors()) {
            return badRequest(
                    views.html.edittask.render(id, filledForm)
            );
        } else {
            filledForm.get().update(id);
            return redirect(routes.Application.tasks());
        }




    }

    @SecureSocial.SecuredAction
    public static Result edit(Long id) {
        Task t = Task.find.byId(id);
        Form<Task> filledForm = taskForm.bindFromRequest();
        return ok(
          views.html.edittask.render(id, filledForm.fill(t))
        );

    }


}
