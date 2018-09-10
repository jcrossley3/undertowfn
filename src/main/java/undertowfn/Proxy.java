package undertowfn;

import com.fnproject.fn.runtime.EntryPoint;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

import java.util.*;

public class Proxy {

    public static void main(final String[] args) {

        final Map<String,String> env = new HashMap<String,String>();
        env.putAll(System.getenv());
        env.put("FN_FORMAT", "http");

        Undertow server = Undertow.builder()
            .addHttpListener(8080, "localhost")
            .setHandler(new HttpHandler() {
                    @Override
                    public void handleRequest(final HttpServerExchange exchange) throws Exception {
                        if (exchange.isInIoThread()) {
                            exchange.dispatch(this);
                            return;
                        }
                        exchange.startBlocking();
                        new EntryPoint().run(env, exchange.getInputStream(), exchange.getOutputStream(), System.err, args);
                    }
                }).build();
        server.start();
    }
}
