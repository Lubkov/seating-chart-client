package ua.stellar.seatingchart.event;

public interface NotifyEvent<T> {

    public void onAction(T sender);
}
