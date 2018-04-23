package ua.stellar.seatingchart.tcp;

import android.util.Log;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;

import ua.stellar.seatingchart.event.NotifyEvent;
import ua.stellar.seatingchart.event.OnDataUpdateListener;

public class TCPClient implements Runnable {

    private static final String LOG_TAG = "RESERVE";
    private int port;
    private String host;
    private TCPStatus status;
    private OnDataUpdateListener onDataUpdateListener;
    private NotifyEvent onConnectionLostListener;
    private OnTCPConnectEvent onConnectListener;

    public TCPClient(final String host,
                     final int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void run() {

        try {
            Socket socket = new Socket(InetAddress.getByName(host), port);
            socket.setKeepAlive(true);

            setStatus(TCPStatus.Connect);

            // из сокета клиента берём поток входящих данных
            InputStream is = socket.getInputStream();

            // буффер данных в 64 килобайта
            byte buf[] = new byte[64 * 1024];

            while (!Thread.currentThread().isInterrupted()) {
                // читаем 64кб от клиента, результат - кол-во реально принятых данных
                int r = socket.getInputStream().read(buf);
                String data = new String(buf, 0, r);

               //Log.d(LOG_TAG, "Read data: " + data);

                doDataUpdateListener(data);
            }
            setStatus(TCPStatus.Disconnect);
            Log.e(LOG_TAG, "TCPClient: Поток остановлен");
        } catch(Exception e) {
            //Log.d(LOG_TAG, "TCPClient error: " + e);
            setStatus(TCPStatus.Disconnect);
            doConnectionLostListener();
        }
    }

    public TCPStatus getStatus() {
        return status;
    }

    private void setStatus(TCPStatus status) {
        this.status = status;

        Log.d(LOG_TAG, "TCP Status: " + status);
        if (onConnectListener != null) {
            onConnectListener.onConnect(status);
        }
    }

    public void setOnDataUpdateListener(OnDataUpdateListener onDataUpdateListener) {
        this.onDataUpdateListener = onDataUpdateListener;
    }

    private void doDataUpdateListener(String tag) {
        if (onDataUpdateListener != null) {
            onDataUpdateListener.onDataUpdate(tag);
        }
    }

    public void setOnConnectListener(OnTCPConnectEvent listener) {
        this.onConnectListener = listener;
    }

    public void setOnConnectionLostListener(NotifyEvent onConnectionLostListener) {
        this.onConnectionLostListener = onConnectionLostListener;
    }

    private void doConnectionLostListener() {
        if (onConnectionLostListener != null) {
            onConnectionLostListener.onAction(this);
        }
    }
}


