package org.firstinspires.ftc.teamcode.systemadditions;

import static org.firstinspires.ftc.teamcode.robot.Constants.TAG;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManagerNotifier;
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegistrar;
import com.qualcomm.robotcore.util.WebHandlerManager;

import org.firstinspires.ftc.ftccommon.external.OnCreate;
import org.firstinspires.ftc.ftccommon.external.OnCreateEventLoop;
import org.firstinspires.ftc.ftccommon.external.WebHandlerRegistrar;
import org.firstinspires.ftc.robotcore.internal.webserver.WebHandler;
import org.firstinspires.ftc.robotserver.internal.webserver.MimeTypesUtil;

import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;

public class FRITESystem implements OpModeManagerNotifier.Notifications {

    private static FRITESystem instance;
    private final boolean webServerAttached = false;

    @OnCreate
    public static void start(Context context) {
        if (instance == null) {
            instance = new FRITESystem();
        }
        Log.d(TAG, "FRITESystem created");
    }

    @OnCreateEventLoop
    public static void attachEventLoop(Context context) {
        //TODO
    }

    @WebHandlerRegistrar
    public static void attachWebServer(Context context, WebHandlerManager manager) {
//        if (instance != null) {
//            instance.internalAttachWebServer(manager.getWebServer());
//        }
    }

    @OpModeRegistrar
    public static void registerOpModes() {
    }

    @Override
    public void onOpModePreInit(OpMode opMode) {

    }

    @Override
    public void onOpModePreStart(OpMode opMode) {

    }

    @Override
    public void onOpModePostStop(OpMode opMode) {

    }

//    public void internalAttachWebServer(WebServer webServer) {
//        if (webServer == null) {
//            return;
//        }
//
//        Activity activity = AppUtil.getInstance().getActivity();
//
//        if (activity == null) {
//            return;
//        }
//
//        WebHandlerManager webHandlerManager = webServer.getWebHandlerManager();
//        AssetManager assetManager = activity.getAssets();
//
//        webHandlerManager.register("/frites",
//                newStaticAssetHandler(assetManager, "frites/index.html"));
//        webHandlerManager.register("/frites/",
//                newStaticAssetHandler(assetManager, "frites/index.html"));
//        addAssetWebHandlers(webHandlerManager, assetManager, "dash");
//
//        addAssetWebHandlers(webHandlerManager, assetManager, "images");
//
//        webServerAttached = true;
//

    /// /        updateStatusView();
//    }
    private void addAssetWebHandlers(WebHandlerManager webHandlerManager,
                                     AssetManager assetManager, String path) {
        try {
            String[] list = assetManager.list(path);

            if (list == null) {
                return;
            }

            if (list.length > 0) {
                for (String file : list) {
                    addAssetWebHandlers(webHandlerManager, assetManager, path + "/" + file);
                }
            } else {
                webHandlerManager.register("/" + path,
                        newStaticAssetHandler(assetManager, path));
            }
        } catch (IOException e) {
            Log.w(TAG, e);
        }
    }

    private WebHandler newStaticAssetHandler(final AssetManager assetManager, final String file) {
        return new WebHandler() {
            @Override
            public NanoHTTPD.Response getResponse(NanoHTTPD.IHTTPSession session)
                    throws IOException {
                if (session.getMethod() == NanoHTTPD.Method.GET) {
                    String mimeType = MimeTypesUtil.determineMimeType(file);
                    return NanoHTTPD.newChunkedResponse(NanoHTTPD.Response.Status.OK,
                            mimeType, assetManager.open(file));
                } else {
                    return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND,
                            NanoHTTPD.MIME_PLAINTEXT, "");
                }
            }
        };
    }

//    private void updateStatusView() {
//        if (connectionStatusTextView != null) {
//            AppUtil.getInstance().runOnUiThread(new Runnable() {
//                @SuppressLint("SetTextI18n")
//                @Override
//                public void run() {
//                    if (!core.enabled) {
//                        connectionStatusTextView.setText("Dashboard: disabled");
//                        return;
//                    }
//
//                    String serverStatus = webServerAttached ? "server attached" : "server detached";
//
//                    String connStatus;
//                    int connections = core.clientCount();
//                    if (connections == 0) {
//                        connStatus = "no connections";
//                    } else if (connections == 1) {
//                        connStatus = "1 connection";
//                    } else {
//                        connStatus = connections + " connections";
//                    }
//
//                    connectionStatusTextView.setText(
//                            "Dashboard: " + serverStatus + ", " + connStatus);
//                }
//            });
//        }
//    }
}
