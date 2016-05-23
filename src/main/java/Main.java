import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;

import java.util.Optional;

import static io.vertx.core.http.HttpMethod.GET;

public class Main {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        HttpServer httpServer = vertx.createHttpServer();

        httpServer.requestHandler(createRouter(vertx)::accept).listen(3001);
    }

    private static Router createRouter(Vertx vertx) {
        Router router = Router.router(vertx);

        router.route(GET, "/recipes").handler(context -> {
            String query = context.request().getParam("q");
            emptyStringCheck(query)
                    .map(q -> context.response().setChunked(true).write(q))
                    .orElse(context.response().setStatusCode(400))
                    .end();
        });

        router.route().last().handler(context ->
            context.response().setStatusCode(404).end());

        return router;
    }

    private static Optional<String> emptyStringCheck(String nullOrEmptyString) {
        // taken from http://stackoverflow.com/a/28322647
        return Optional.ofNullable(nullOrEmptyString).filter(s -> !s.isEmpty());
    }

}

/*
TODO
- move to proper package?
- on load, create lucene index
  - use name, ingredients
- on request, search lucene index
- handle pagination: from/limit & defaults
- investigate Verticles (one for Lucene, one for Server?)
- make server port configurable
*/