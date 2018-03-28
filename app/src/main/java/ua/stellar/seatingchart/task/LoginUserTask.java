package ua.stellar.seatingchart.task;


import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import ua.stellar.seatingchart.domain.SysInfo;
import ua.stellar.seatingchart.event.NotifyEvent;
import ua.stellar.seatingchart.utils.JsonResponse;
import ua.stellar.seatingchart.utils.JsonUtils;

public class LoginUserTask extends AsyncTask<Void, Void, JsonResponse> {

    private final String LOG_TAG = "RESERVE";

    private Activity activity;
    private ProgressDialog pDialog;
    private String login;
    private String password;
    private NotifyEvent onLoadingComplete;

    public LoginUserTask(final Activity activity,
                         final String login,
                         final String password) {

        this.activity = activity;
        this.login = login;
        this.password = password;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        pDialog = new ProgressDialog(activity);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setMessage("Авторизация на сервере...");
        pDialog.show();
    }

    @Override
    protected JsonResponse doInBackground(Void... params) {
        try {
            Log.v(LOG_TAG, "Авторизация на сервере");
            String urlString =
                    SysInfo.getInstance().getUrlAddress() +
                            "/order/login-user?" +
                            "user_name=" + login + "&" +
                            "user_password=" + password;

            return JsonUtils.getJsonResponse(urlString);

//            if (response.isSuccess()) {
//                Log.d(LOG_TAG, "response = " + response.getResult().toString());
//                return new Gson().fromJson(response.getResult().toString(), TheUser.class);
//            } else {
//                Log.d(LOG_TAG, "response = false");
//                return null;
//            }

        } catch (Exception e) {
            hide();
            return new JsonResponse(false, 0, e.getMessage());
        }
    }

    @Override
    protected void onPostExecute(JsonResponse result) {
        super.onPostExecute(result);

        doLoadingComplete(result);
        hide();
    }

    private void hide() {
        if (null != pDialog && pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }

    public void setOnLoadingComplete(NotifyEvent onLoadingComplete) {
        this.onLoadingComplete = onLoadingComplete;
    }

    private void doLoadingComplete(JsonResponse result) {
        if (onLoadingComplete != null) {
            onLoadingComplete.onAction(result);
        }
    }
}
