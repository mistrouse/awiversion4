package controllers;

import play.*;
import play.mvc.*;
import play.data.*;
import models.*;
import securesocial.core.Identity;
import securesocial.core.java.SecureSocial;
import com.avaje.ebean.Expr;
import java.util.List;

import views.html.*;

public class Application extends Controller {

    static Form<Task> taskForm = Form.form(Task.class);

    @SecureSocial.SecuredAction
    public static Result index() {
        return redirect(routes.Application.tasks());
    }

    public static List<Task> userTasks(Identity user) {
        return Task.find.where().eq("email", user.email().get()).findList();
    }

    @SecureSocial.SecuredAction
    public static Result tasks() {
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        String q = Form.form().bindFromRequest().get("q");
        if(q != null) {
            List<Task> t = Task.find
              .where()
              .and(
                Expr.or(
                  Expr.like("label", q + "%"),Expr.like("name", q + "%")
                ),
                Expr.eq("email", user.email().get())
              )
              .findList();
            return ok(
                    views.html.index.render(t, taskForm, q)
            );
        }
        else {
            return ok(
                    views.html.index.render(userTasks(user), taskForm, q)
            );
        }
    }

    @SecureSocial.SecuredAction
    public static Result newTask() {
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        Form<Task> filledForm = taskForm.bindFromRequest();
        if(filledForm.hasErrors()) {
            return badRequest(
                    views.html.index.render(userTasks(user), filledForm, "")
            );
        } else {
            Task t = filledForm.get();
            t.email = user.email().get();
            Task.create(t);

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
