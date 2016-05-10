package controllers;

/**
 * Created by rizrusn on 13/04/16.
 */
import com.fasterxml.jackson.databind.JsonNode;
import play.libs.F;
import play.libs.F.Promise;
import play.libs.Json;
import play.libs.oauth.OAuth;
import play.libs.ws.WS;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;
import play.mvc.Controller;
import play.mvc.Result;
import static play.libs.Json.toJson;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

public class WebClient extends Controller {

    @Inject
    WSClient ws;

    public Result getTodos() {
        //WSResponse todos = (WSResponse) ws.url("http://jsonplaceholder.typicode.com/todos").get();
        CompletionStage<WSResponse> todos = ws.url("http://jsonplaceholder.typicode.com/todos").get();

        return ok(todos.toString());
//        return ws.url("http://jsonplaceholder.typicode.com/todos")
//                .get()
//                .thenApply(result -> ok(result.asJson()));
    }
}
