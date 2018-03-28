package ua.stellar.seatingchart.event;

import ua.stellar.seatingchart.ResourceItem;

public interface OnResourceStatusChangedListener {

    void onStatusChanged(ResourceItem item);
}
