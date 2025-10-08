package org.firstinspires.ftc.teamcode.systemadditions;

import fi.iki.elonen.NanoHTTPD;

import org.firstinspires.ftc.onbotjava.RegisterWebHandler;
import org.firstinspires.ftc.robotcore.internal.webserver.WebHandler;

import java.io.IOException;

@RegisterWebHandler(uri = "/hello/")
public class WebHandlers implements WebHandler {

    @Override
    public NanoHTTPD.Response getResponse(NanoHTTPD.IHTTPSession session)
            throws IOException, NanoHTTPD.ResponseException {
        // Handle the request and return a response
        // For example, you can return a simple text response:
        // return NanoHTTPD.newFixedLengthResponse("Hello, World!");

        // Example of handling different URIs

        if (true) {
            return NanoHTTPD.newFixedLengthResponse(
                    NanoHTTPD.Response.Status.OK, NanoHTTPD.MIME_PLAINTEXT, "Hello, World!");
        }

        //        String uri = session.getUri();
        //
        //        switch (uri) {
        //            case "/hello":
        //                return NanoHTTPD.newFixedLengthResponse(
        //                        NanoHTTPD.Response.Status.OK,
        //                        NanoHTTPD.MIME_PLAINTEXT,
        //                        "Hello, World!"
        //                );
        //
        //            case "/goodbye":
        //                return NanoHTTPD.newFixedLengthResponse(
        //                        NanoHTTPD.Response.Status.OK,
        //                        NanoHTTPD.MIME_PLAINTEXT,
        //                        "Goodbye, World!"
        //                );
        //
        //            case "/status":
        //                return NanoHTTPD.newFixedLengthResponse(
        //                        NanoHTTPD.Response.Status.OK,
        //                        NanoHTTPD.MIME_PLAINTEXT,
        //                        "{\"status\": \"OK\"}"
        //                );
        //        }
        return null;
    }
}
