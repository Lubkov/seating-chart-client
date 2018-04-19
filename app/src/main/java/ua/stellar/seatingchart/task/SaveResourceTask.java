package ua.stellar.seatingchart.task;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import ua.stellar.seatingchart.domain.Operation;
import ua.stellar.seatingchart.domain.OperationType;
import ua.stellar.seatingchart.domain.SysInfo;
import ua.stellar.seatingchart.event.NotifyEvent;
import ua.stellar.seatingchart.utils.JsonResponse;
import ua.stellar.seatingchart.utils.JsonUtils;

public class SaveResourceTask extends AsyncTask<Void, Void, Operation> {

    private final String LOG_TAG = "RESERVE";

    private Activity activity;
    private ProgressDialog pDialog;

    private Long itemId;
    private Long layoutId;
    private Long goodsId;
    private OperationType operationType;

    private NotifyEvent onSaveComplete;

    public SaveResourceTask(final Activity activity,
                            final Long itemId,
                            final Long layoutId,
                            final Long goodsId,
                            final OperationType operationType) {

        this.activity = activity;
        this.itemId = itemId;
        this.layoutId = layoutId;
        this.goodsId = goodsId;
        this.operationType = operationType;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        pDialog = new ProgressDialog(activity);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setMessage("Сохранение состояния...");
        pDialog.show();
    }

    @Override
    protected Operation doInBackground(Void... params) {
        try {
            String urlString = SysInfo.getInstance().getUrlAddress() +
                    "/order/add-operation?" +
                    "item_id=" + itemId + "&" +
                    "layout_id=" + layoutId + "&" +
                    "goods_id=" + goodsId + "&" +
                    "user_id=" + SysInfo.getInstance().getUser().getId() + "&" +
                    "operation_type=" + operationType.getId() + "&" +
                    "device_id=" + SysInfo.getInstance().getDeviceId();

            JsonResponse response = JsonUtils.getJsonResponse(urlString);
            if (response.isSuccess()) {
                Gson gson = new Gson();
                String innerJson = gson.toJson(response.getResult());
                return gson.fromJson(innerJson, Operation.class);
            }
        } catch (Exception e) {
            hide();
            Log.e(LOG_TAG, "Save resource task: " + e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Operation result) {
        super.onPostExecute(result);

        doSaveComplete(result);
        hide();
    }

    private void hide() {
        if (null != pDialog && pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }

    public void setOnSaveComplete(NotifyEvent onSaveComplete) {
        this.onSaveComplete = onSaveComplete;
    }

    private void doSaveComplete(Operation result) {
        if (onSaveComplete != null) {
            onSaveComplete.onAction(result);
        }
    }
}