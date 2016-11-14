package chanathip.gotogether;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by neetc on 11/7/2016.
 */

public class GotogetherNotificationManager {
    private Context context;
    private String connectionString;

    private interface AsyncTaskCompleteListener<T> {
        void OnTaskComplete(T result);
    }

    private class Uidandtoken {
        String uid;
        String token;

        Uidandtoken(String uid, String token) {
            this.uid = uid;
            this.token = token;
        }
    }
    private class RequestFriendData{
        String ReceiveUid;
        String Message;

        RequestFriendData(String ReceiveUid,String Message){
            this.ReceiveUid = ReceiveUid;
            this.Message = Message;
        }
    }

    GotogetherNotificationManager(Context context) {
        connectionString = context.getResources().getString(R.string.connection_string);
        this.context = context;
    }

    public void updateToken(String Uid, String token) {
        Uidandtoken uidandtoken = new Uidandtoken(Uid, token);

        UpdateTokenToServer updateTokenToServer = new UpdateTokenToServer(uidandtoken);
        updateTokenToServer.launchTask();
    }
    public void deleteToken(String Uid){
        Uidandtoken uidandtoken = new Uidandtoken(Uid, "");

        UpdateTokenToServer updateTokenToServer = new UpdateTokenToServer(uidandtoken);
        updateTokenToServer.launchTask();
    }
    public void sendFriendRequest(String ReceiveUid,String SenderName){
        String Message = SenderName + " send friend request to you";
        RequestFriendData requestFriendData = new RequestFriendData(ReceiveUid,Message);

        SendNotificationToServer sendNotificationToServer = new SendNotificationToServer(requestFriendData);
        sendNotificationToServer.launchTask();
    }
    public void acceptFriendRequest(String ReceiveUid,String SenderName){
        String Message = SenderName + " accept your request already";
        RequestFriendData requestFriendData = new RequestFriendData(ReceiveUid,Message);

        SendNotificationToServer sendNotificationToServer = new SendNotificationToServer(requestFriendData);
        sendNotificationToServer.launchTask();
    }
    public void sendPersonChat(String ReceiveUid,String SenderName){
        String Message = "new Message from "+SenderName;
        RequestFriendData requestFriendData = new RequestFriendData(ReceiveUid,Message);

        SendNotificationToServer sendNotificationToServer = new SendNotificationToServer(requestFriendData);
        sendNotificationToServer.launchTask();
    }

    private class UpdateTokenToServer implements AsyncTaskCompleteListener<String> {
        private Uidandtoken uidandtoken;

        UpdateTokenToServer(Uidandtoken uidandtoken) {
            this.uidandtoken = uidandtoken;
        }

        @Override
        public void OnTaskComplete(String result) {
            Log.d("test response", result);

            try {
                //solution
                JSONObject jObject = new JSONObject(result);
                String StatusReturnCode = jObject.getString("statuscode");
                if (StatusReturnCode.equals("4") || StatusReturnCode.equals("1")) {

                } else {
                    launchTask();
                }
            } catch (JSONException e) {
                Log.e("Register", "JSONException", e);
            }
        }

        public void launchTask() {
            UpdateTokenToServerTask updateTokenToServerTask = new UpdateTokenToServerTask(this);
            updateTokenToServerTask.execute(uidandtoken);
        }
    }
    private class SendNotificationToServer implements AsyncTaskCompleteListener<String> {
        private RequestFriendData requestFriendData;

        SendNotificationToServer(RequestFriendData requestFriendData) {
            this.requestFriendData = requestFriendData;
        }

        @Override
        public void OnTaskComplete(String result) {
            Log.d("test response", result);

            try {
                //solution
                JSONObject jObject = new JSONObject(result);
                String StatusReturnCode = jObject.getString("statuscode");
                if (StatusReturnCode.equals("1")) {

                } else {
                    launchTask();
                }
            } catch (JSONException e) {
                Log.e("sendfriend notification", "JSONException", e);
            }
        }

        public void launchTask() {
            SendNotificationToServerTask sendNotificationToServerTask = new SendNotificationToServerTask(this);
            sendNotificationToServerTask.execute(requestFriendData);
        }
    }

    private class UpdateTokenToServerTask extends AsyncTask<Uidandtoken, Void, String> {
        private HttpURLConnection urlConnection;
        private AsyncTaskCompleteListener<String> callback;

        UpdateTokenToServerTask(AsyncTaskCompleteListener<String> cb) {
            this.callback = cb;
        }

        @Override
        protected String doInBackground(Uidandtoken... uidandtoken) {
            OutputStream os;
            InputStream in;


            //convert to json
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("Uid", uidandtoken[0].uid);
                jsonObject.put("Token", uidandtoken[0].token);
            } catch (JSONException e) {
                Log.e("JsonEncode", "exception", e);
            }
            String outputJson = jsonObject.toString();

            //start connection
            StringBuilder result = new StringBuilder();
            try {
                String connectURL = connectionString + "updatetoken.php";
                URL url = new URL(connectURL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000 /*milliseconds*/);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setFixedLengthStreamingMode(outputJson.getBytes().length);
                urlConnection.setRequestProperty("Content-Type", "application/json;charset=utf-8");

                //open
                urlConnection.connect();

                //prepare send
                os = new BufferedOutputStream(urlConnection.getOutputStream());
                os.write(outputJson.getBytes());
                //clean up
                os.flush();

                //do something with response
                in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                //clean up
                os.close();
                in.close();
            } catch (Exception e) {
                Log.e("ConnectDatabase", "exception", e);
            } finally {
                urlConnection.disconnect();
            }
            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            callback.OnTaskComplete(result);
        }
    }
    private class SendNotificationToServerTask extends AsyncTask<RequestFriendData, Void, String> {
        private HttpURLConnection urlConnection;
        private AsyncTaskCompleteListener<String> callback;

        SendNotificationToServerTask(AsyncTaskCompleteListener<String> cb) {
            this.callback = cb;
        }

        @Override
        protected String doInBackground(RequestFriendData... requestFriendData) {
            OutputStream os;
            InputStream in;

            //convert to json
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("ReceiveUid", requestFriendData[0].ReceiveUid);
                jsonObject.put("Message", requestFriendData[0].Message);
            } catch (JSONException e) {
                Log.e("JsonEncode", "exception", e);
            }
            String outputJson = jsonObject.toString();

            //start connection
            StringBuilder result = new StringBuilder();
            try {
                String connectURL = connectionString + "sendNotification.php";
                URL url = new URL(connectURL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000 /*milliseconds*/);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setFixedLengthStreamingMode(outputJson.getBytes().length);
                urlConnection.setRequestProperty("Content-Type", "application/json;charset=utf-8");

                //open
                urlConnection.connect();

                //prepare send
                os = new BufferedOutputStream(urlConnection.getOutputStream());
                os.write(outputJson.getBytes());
                //clean up
                os.flush();

                //do something with response
                in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                //clean up
                os.close();
                in.close();
            } catch (Exception e) {
                Log.e("ConnectDatabase", "exception", e);
            } finally {
                urlConnection.disconnect();
            }
            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            callback.OnTaskComplete(result);
        }
    }
}
