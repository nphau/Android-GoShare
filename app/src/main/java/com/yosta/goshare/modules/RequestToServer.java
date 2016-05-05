package com.yosta.goshare.modules;

/**
 * Created by Dell on 3/26/2016.
 */
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

public class RequestToServer {
    static enum MessageType {
        Login, Logout, AskQuestion, AnswerQuestion
    }
    public static enum Method {
        POST, GET
    }

    public static String ServerURL = "http://104.199.148.50/";
    public static String ServerHandle = ServerURL + "handler.php";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void sendRequest(Method method, RequestParams params, ResponseHandlerInterface responseHandler) {
        if (method == Method.GET)
            client.get(ServerHandle, params, responseHandler);
        else if (method == Method.POST)
            client.post(ServerHandle, params, responseHandler);
    }
}
