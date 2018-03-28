package ua.stellar.seatingchart.task;

import android.app.Activity;
import android.os.AsyncTask;

import ua.stellar.seatingchart.domain.SysInfo;
import ua.stellar.seatingchart.utils.JsonResponse;
import ua.stellar.seatingchart.utils.JsonUtils;

public class RemoveLockResourceTask extends AsyncTask<Void, Void, Boolean> {

    private final String LOG_TAG = "RESERVE";

    Activity activity;
    private String url;
    //private ProgressDialog pDialog;

    public RemoveLockResourceTask(final Activity activity){
        this.activity = activity;
        this.url = SysInfo.getInstance().getUrlAddress() +
                "/order/remove-lock-layout-item?" +
                "device_id=" + SysInfo.getInstance().getDeviceId();

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

//        pDialog = new ProgressDialog(activity);
//        pDialog.setCanceledOnTouchOutside(false);
//        pDialog.setMessage("Удаление блокировки ресурса...");
//        pDialog.show();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        JsonResponse response = null;

        try {
            response = JsonUtils.getJsonResponse(url);

            return response.isSuccess();
        } catch (Exception e) {
            hide();
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);

        hide();
    }

    private void hide() {
//        if (null != pDialog && pDialog.isShowing()) {
//            pDialog.dismiss();
//        }
    }
}