package ua.stellar.seatingchart.event;

import ua.stellar.seatingchart.utils.JsonResponse;


public interface OnTaskCompleteListener {

    void onTaskComplete(JsonResponse response);
}
