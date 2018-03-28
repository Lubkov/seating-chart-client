package ua.stellar.seatingchart.task;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

import ua.stellar.seatingchart.domain.LayoutComposition;
import ua.stellar.seatingchart.event.NotifyEvent;
import ua.stellar.seatingchart.utils.JsonUtils;

public class LoadLayoutCompositionTask extends AsyncTask<Void, Void, List<LayoutComposition>> {

    private final String LOG_TAG = "RESERVE";

    ProgressDialog pDialog;
    private String url;
    private Activity activity;
    private NotifyEvent onLoadingComplete;

    public LoadLayoutCompositionTask(final Activity activity,
                                     final String url ) {
        this.activity = activity;
        this.url = url;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        pDialog = new ProgressDialog(activity);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setMessage("Загрузка состава карты...");
        pDialog.show();
    }

    @Override
    protected List<LayoutComposition> doInBackground(Void... params) {
        try {
            return JsonUtils.sendRequest(url, LayoutComposition.class);

            //                JsonResponse response = JsonUtils.getJsonResponse(urlString);
            //                if (response.isSuccess()) {
            //                    Gson gson = new Gson();
            //
            //                    Log.d(LOG_TAG, response.getResult().toString());
            //
            //                    return Arrays.asList(gson.fromJson(response.getResult().toString(), LayoutComposition[].class));
            //                } else {
            //                    return null;
            //                }
        } catch (Exception e) {
            hide();
            Log.e(LOG_TAG, "Error loader: " + e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<LayoutComposition> result) {
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

    private void doLoadingComplete(List<LayoutComposition> items) {
        if (onLoadingComplete != null) {
            onLoadingComplete.onAction(items);
        }
    }
}
