package controllers;

import actors.FileReaderProtocol;
import models.Task;
import models.User;
import play.Logger;
import play.data.Form;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.libs.Json;
import play.libs.ws.*;
import play.mvc.*;

import views.html.*;

import java.util.List;

import static play.libs.Json.toJson;
import com.fasterxml.jackson.databind.JsonNode;

import javax.inject.Inject;

import java.nio.file.*;
import java.io.*;

import java.util.*;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import play.libs.Akka;
import play.libs.F.Promise;
import akka.actor.*;

import scala.concurrent.ExecutionContextExecutor;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */

public class HomeController extends Controller {

    public static class Login {

        @Constraints.MinLength(3)
        @Constraints.Required
        public String username;

        @Constraints.MinLength(6)
        @Constraints.Required
        public String password;

    }

//    @Inject
//    WSClient ws;
    private final WSClient ws;
    private final ExecutionContextExecutor exec;

    public Result index() {
        List<Task> tasks = Task.find.all();
        return ok(index.render("The First Play!!!",tasks));
        //return ok(index.render("Your new application is ready."));
    }



    public Result toForm(){
        String finalTask = session("task");                             // contoh pemanggilan session
        if(finalTask != null) {
            return ok(formTask.render(finalTask));
        } else {
            return ok(formTask.render("this is first task"));
        }
        
        //return ok(formTask.render());
    }

    public Result addTask(){

        Task myTask = Form.form(Task.class).bindFromRequest().get();
        myTask.save();
        session("task", myTask.name);                                   // contoh pengisian session
        flash("success", "Task saved!");
        return redirect(routes.HomeController.index());
    }

    public  Result updateDone(long id){

        Task task = Task.find.byId(id);
        task.done = !task.done;
        task.save();
        return redirect(routes.HomeController.index());
    }

    public  Result deleteTask(long id){
        Task.find.ref(id).delete();
        return redirect(routes.HomeController.index());
    }

    public Result getTask() {                                                   // contoh send json
        List<Task> tasks = new Model.Finder(String.class, Task.class).all();    
        return ok(toJson(tasks));
        //return redirect(routes.HomeController.index());
    }

    @Security.Authenticated(BasicAuthenticator.class)                           // securing API endpoints
    @BodyParser.Of(BodyParser.Json.class)                                       // parser json
    public Result postTask() {                                                  // contoh POST json via API
        JsonNode json = request().body().asJson();
        long id = json.findPath("id").longValue();
        String name = json.findPath("name").textValue();
        boolean done =  json.findPath("done").booleanValue();
        Task newTask = new Task();
        //newTask.id = id;
        newTask.name = name;
        newTask.done = done;
        newTask.save();
        return status(200,"Unlock Success");
        //return redirect(routes.HomeController.index());
    }

    public Result toSignup(){
        Form<User> userForm = Form.form(User.class);
        return ok(signup.render(userForm));
        //return redirect(routes.HomeController.index());
    }

    public Result submit(){
        Form<User> userForm = Form.form(User.class).bindFromRequest();
        if (userForm.hasErrors()) {
            return badRequest(views.html.signup.render(userForm));
        } else {
            User user = userForm.get();
            user.save();
            flash("success", "User saved!");
            return redirect(routes.HomeController.toLogin());
        }
    }

    public Result toLogin(){
        Form<Login> loginForm = Form.form(Login.class);
        return ok(login.render(loginForm));
    }

    public  Result login(){
        Form<Login> loginForm = Form.form(Login.class).bindFromRequest();
        if (loginForm.hasErrors()) {
            return badRequest(views.html.login.render(loginForm));
        } else {
            Login form = loginForm.get();
            User user2 = User.find.byId(form.username);
            if (user2 != null && form.password.equals(user2.password)) {
                session().clear();
                session("user", user2.name);
                flash("success", "Login success");
                return redirect(routes.HomeController.index());
            }else{
                flash("success", "Username or Password not match");
                return redirect(routes.HomeController.toLogin());
                //return ok(user2.password);
            }
        }
    }

    public Result getUser() {                                                   // contoh send json
        List<User> users = new Model.Finder(String.class, User.class).all();
        return ok(toJson(users));
        //return redirect(routes.HomeController.index());
    }

    // Method to access API postTask which use json, and use Basic Authentication
//    public Result testWS(){
//        JsonNode task = Json.newObject()
//                .put("id", 123236)
//                .put("name", "Task ws")
//                .put("done", true);
//
//        ws.url("http://localhost:9000/json/task").setAuth("rizrusn", "password", WSAuthScheme.BASIC).post(task);
//        return redirect(routes.HomeController.index());
//    }

    @Inject
    public HomeController(final ExecutionContextExecutor exec, final WSClient ws) {
        this.exec = exec;
        this.ws = ws;
    }

    public CompletionStage<Result> testWS() {
        final JsonNode task = Json.newObject()
                .put("id", 123236)
                .put("name", "Task ws")
                .put("done", true);

        final CompletionStage<WSResponse> eventualResponse = ws.url("http://localhost:9000/json/task").setAuth("user","password", WSAuthScheme.BASIC)
                .post(task);

        return eventualResponse.thenApplyAsync(response -> ok(response.asJson()), exec);
    }

    public Result cekPassword(String name){
        try {
            JsonNode json = request().body().asJson();
            String key = json.findPath("password").textValue();

            User user = User.find.byId(name);

            if (user == null) {
                JsonNode message = Json.newObject()
                        .put("code", 422)
                        .put("message", "user not found");
                return ok(message);
            }

            if(user.password.equals(key)){
                JsonNode message = Json.newObject()
                        .put("code", 1)
                        .put("message", "The password match");
                return ok(message);
            }else{
                JsonNode message = Json.newObject()
                        .put("code", 422)
                        .put("message", "The Password dosn't match");
                return ok(message);
            }
        } catch (Exception e) {
            Logger.error("Error in HomeController#verify " + e.getMessage());
            JsonNode message = Json.newObject()
                    .put("code", 422)
                    .put("message", e.getMessage());

            return ok(message);
        }
    }

    public Result uploadForm() {
        return ok(formImage.render());
    }

    public play.mvc.Result handleUpload() {
        play.mvc.Http.MultipartFormData<File> body = request().body().asMultipartFormData();
        play.mvc.Http.MultipartFormData.FilePart<File> picture = body.getFile("picture");
        if (picture != null && (picture.getContentType().equals("image/png") || picture.getContentType().equals("image/jpeg"))) {
            String fileName = picture.getFilename();
            String contentType = picture.getContentType();
            java.io.File file = picture.getFile();
            //file.renameTo(new File("home/", fileName));
            return ok("File uploaded "+picture.getContentType());
        } else if(!picture.getContentType().equals("image/png") || !picture.getContentType().equals("image/jpeg")){
            return ok("Error: Allowed file type only .png/.jpg/.jpeg");
        }else {
            flash("error", "Missing file");
            return ok("Error: Missing file");
        }

//        Http.MultipartFormData body = request().body().asMultipartFormData();
//        Http.MultipartFormData.FilePart profileImage = body.getFile("profile");
//        if (profileImage != null) {
//            try {
//                String fileName = profileImage.getFilename();
//                String contentType = profileImage.
//                        getContentType();
//                File file = profileImage.getFile();
//                Path path = FileSystems.getDefault().getPath("/tmp/" + fileName);
//                        Files.write(path, Files.readAllBytes(file.toPath()));
//                return ok("Image uploaded");
//            } catch(Exception e) {
//                return internalServerError(e.getMessage());
//            }
//        } else {
//            flash("error", "Please upload a valid file");
//            return redirect(routes.HomeController.uploadForm());
//        }
    }


    public Result modifyHeaders() {
        response().setHeader("ETag", "foo_java");
        JsonNode message = Json.newObject()
                .put("code", 22)
                .put("message", "header example");

        return ok(message);
    }

    public Result modifyCookies() {
        response().setCookie("source", "tw", (60*60));
        return ok("Cookie Modification Example");
    }

//    public Promise<Result> asyncExample() {
//        ActorRef fileReaderActor = Akka.system().actorOf(Props.create(FileReaderActor.class));
//        FileReaderProtocol words = new FileReaderProtocol("/usr/share/dict/words");
//        return Promise.wrap(ask(fileReaderActor, words, 3000)).map(
//                new Function<Object, Result>() {
//                    public Result apply(Object response) {
//                        return ok(response.toString());
//                    }
//                }
//        );
//    }

    /*
 * OVO-254 : "As a customer, I want to unlock my OVO app, so that I can use OVO to transfer money"
 * by Rizki Rusmin N.
 * 05 April 2016
 * */
//    public Result unlockOVO(String id){
//        try {
//            JsonNode json = request().body().asJson();
//            String code = json.findPath("code").textValue();
//
//            Customer customer = Customer.findById(new ObjectId(id));
//
//            if (customer == null) {
////        JsonNode message = Json.newObject()
////                .put("status", 422)
////                .put("message", "user not found");
////        return ok(message);
//                return status(422, "user not found");
//            }else if(customer.getSecurityCode().equals(code)){
////        JsonNode message = Json.newObject()
////                .put("status", 200)
////                .put("message", "Unlock Success");
////        return ok(message);
//                return status(200,"Unlock Success");
//            }else{
////        JsonNode message = Json.newObject()
////                .put("status", 400)
////                .put("message", "The Security Code doesn't match");
////        return ok(message);
//                return status(400, "The Security Code doesn't match");
//            }
//        } catch (Exception e) {
//            Logger.error("Error in HomeController#verify " + e.getMessage());
////      JsonNode message = Json.newObject()
////              .put("status", 500)
////              .put("message", e.getMessage());
////      return ok(message);
//            return status(500,"Null Content");
//        }
//    }
//
//
//    @Test
//    public void testUnlockOVOWithValidFormat() {
//        CustomerKtpCard ktpCard = new CustomerKtpCard();
//        ktpCard.setNIK(NIK);
//        ktpCard.setKecamatan(kecamatan);
//        ktpCard.setKelurahan(kelurahan);
//        ktpCard.setPlaceOfBirth(placeOfBirth);
//        ktpCard.setDateOfBirth(dateOfBirth);
//        ktpCard.setAddressOfKTP(addressOfKTP);
//        ktpCard.setRt(rt);
//        ktpCard.setRw(rw);
//        ktpCard.setDomicileAddress(domicileAddress);
//        ktpCard.setTaxId(taxId);
//
//        Customer customer = new Customer();
//        customer.setMobile(mobile);
//        customer.setFirstName(firstName);
//        customer.setLastName(lastName);
//        customer.setDob(dob);
//        customer.setGender(gender);
//
//        customer.setCbAccountNumber(cbAccountNumber);
//        customer.setCbRefNo(cbRefNo);
//        customer.setCbTraceNo(cbTraceNo);
//        customer.setCbCIF(cbCIF);
//        customer.setCifAddress(cifAddress);
//        customer.setCity(city);
//        customer.setState(state);
//        customer.setMaidenName(maidenName);
//        customer.setMaritalStatus(maritalStatus);
//        customer.setCbAccName(cbAccName);
//        customer.setKtpCard(ktpCard);
//        customer.setMobileVerificationStatus("verified");
//        customer.save();
//
//        Logger.info(customer.getId()+"");
//
//        WSResponse response = WS.url(url()+"/"+ customer.getId().toString() +"/securityCode").post(Json.newObject().put("code", "123456")).get(3000);
//        assertEquals(200, response.getStatus());
//
//        Customer found = Customer.findByMobilePhoneNumber(mobile);
//        Logger.info(found.getId()+"");
//
//        response = WS.url(url()+"/"+ found.getId() +"/securityCode/"+found.getSecurityCode()).execute().get(3000);
//        assertEquals(200, response.getStatus());
//
//        response = WS.url(url()+"/"+ customer.getId().toString() +"/unlockOVO").post(Json.newObject().put("code", "123456")).get(3000);
//        assertEquals(200, response.getStatus());
//        assertEquals("Unlock Success", response.getBody());
//
////    JsonNode json = Json.parse(response.getBody());
////
////    assertEquals(200, response.getStatus());
////    assertEquals("Unlock Success",json.findPath("message").asText());
//
//        Customer.collections().remove(found.getId());
//    }
//
//    @Test
//    public void testUnlockOVOWithInvalidCustomer(){
//        Customer c = new Customer();
//        c.save(true);
//
//        WSResponse response = WS.url(url()+"/"+ c.getId().toString() +"/unlockOVO").post(Json.newObject().put("code", "123456")).get(3000);
//        assertEquals(500, response.getStatus());
//    }
//
//    @Test
//    public void testUnlockOVOWithInvalidSecurityCode() {
//        CustomerKtpCard ktpCard = new CustomerKtpCard();
//        ktpCard.setNIK(NIK);
//        ktpCard.setKecamatan(kecamatan);
//        ktpCard.setKelurahan(kelurahan);
//        ktpCard.setPlaceOfBirth(placeOfBirth);
//        ktpCard.setDateOfBirth(dateOfBirth);
//        ktpCard.setAddressOfKTP(addressOfKTP);
//        ktpCard.setRt(rt);
//        ktpCard.setRw(rw);
//        ktpCard.setDomicileAddress(domicileAddress);
//        ktpCard.setTaxId(taxId);
//
//        Customer customer = new Customer();
//        customer.setMobile(mobile);
//        customer.setFirstName(firstName);
//        customer.setLastName(lastName);
//        customer.setDob(dob);
//        customer.setGender(gender);
//
//        customer.setCbAccountNumber(cbAccountNumber);
//        customer.setCbRefNo(cbRefNo);
//        customer.setCbTraceNo(cbTraceNo);
//        customer.setCbCIF(cbCIF);
//        customer.setCifAddress(cifAddress);
//        customer.setCity(city);
//        customer.setState(state);
//        customer.setMaidenName(maidenName);
//        customer.setMaritalStatus(maritalStatus);
//        customer.setCbAccName(cbAccName);
//        customer.setKtpCard(ktpCard);
//        customer.setMobileVerificationStatus("verified");
//        customer.save();
//
//        Logger.info(customer.getId()+"");
//
//        WSResponse response = WS.url(url()+"/"+ customer.getId().toString() +"/securityCode").post(Json.newObject().put("code", "123456")).get(3000);
//        assertEquals(200, response.getStatus());
//
//        Customer found = Customer.findByMobilePhoneNumber(mobile);
//        Logger.info(found.getId()+"");
//
//        response = WS.url(url()+"/"+ found.getId() +"/securityCode/"+found.getSecurityCode()).execute().get(3000);
//        assertEquals(200, response.getStatus());
//
//        response = WS.url(url()+"/"+ customer.getId().toString() +"/unlockOVO").post(Json.newObject().put("code", "12345")).get(3000);
//        assertEquals(400, response.getStatus());
//        assertEquals("The Security Code doesn't match", response.getBody());
//
//        Customer.collections().remove(found.getId());
//    }


}


