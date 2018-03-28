package ua.stellar.seatingchart.task;


import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import ua.stellar.seatingchart.event.OnTaskCompleteListener;
import ua.stellar.seatingchart.utils.JsonResponse;
import ua.stellar.seatingchart.utils.JsonUtils;

public class LoadDataTask extends AsyncTask<Void, Void, JsonResponse> {

    private final String LOG_TAG = "RESERVE";

    private Activity activity;
    private ProgressDialog pDialog;
    private String url;
    private boolean showLoadDialog;
    private OnTaskCompleteListener onTaskComplete;

    public LoadDataTask(final Activity activity,
                        final boolean showLoadDialog,
                        final String url) {

        this.activity = activity;
        this.url = url;
        this.showLoadDialog = showLoadDialog;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (showLoadDialog) {
            pDialog = new ProgressDialog(activity);
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.setMessage("Подождите...");
            pDialog.show();
        }
    }

    @Override
    protected JsonResponse doInBackground(Void... params) {
        try {
            return JsonUtils.getJsonResponse(url);
        } catch (Exception e) {
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
        if ((null != pDialog) && (pDialog.isShowing())) {
            pDialog.dismiss();
        }
    }

    public void setOnTaskComplete(OnTaskCompleteListener onTaskComplete) {
        this.onTaskComplete = onTaskComplete;
    }

    private void doLoadingComplete(JsonResponse result) {
        if (onTaskComplete != null) {
            onTaskComplete.onTaskComplete(result);
        }
    }
}
