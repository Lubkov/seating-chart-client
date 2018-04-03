package ua.stellar.seatingchart.task;

import android.app.Activity;
import android.os.AsyncTask;

import ua.stellar.seatingchart.domain.SysInfo;
import ua.stellar.seatingchart.event.NotifyEvent;
import ua.stellar.seatingchart.utils.JsonResponse;
import ua.stellar.seatingchart.utils.JsonUtils;

public class OperationLoadTask extends AsyncTask<Void, Void, JsonResponse> {

    private final String LOG_TAG = "RESERVE";

    private Activity activity;
    private String layoutID;
    private NotifyEvent onLoadingComplete;

    public OperationLoadTask(final Activity activity,
                             final String layoutID) {

        this.activity = activity;
        this.layoutID = layoutID;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected JsonResponse doInBackground(Void... params) {
        try {
            String urlString =
                    SysInfo.getInstance().getUrlAddress() +
                            "/order/get-operation-list?" +
                            "layout_id=" + layoutID;

            return JsonUtils.getJsonResponse(urlString);
        } catch (Exception e) {
            return new JsonResponse(false, 0, e.getMessage());
        }
    }

    @Override
    protected void onPostExecute(JsonResponse result) {
        super.onPostExecute(result);

        doLoadingComplete(result);
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
