package ua.stellar.seatingchart;

import android.app.Application;
import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;

import ua.stellar.seatingchart.domain.SysInfo;
import ua.stellar.seatingchart.event.NotifyEvent;
import ua.stellar.seatingchart.task.ReconnectTask;
import ua.stellar.seatingchart.tcp.TCPClient;
import ua.stellar.seatingchart.utils.ApplicationService;

public class ReserveApplication extends Application {

    private final String LOG_TAG = "RESERVE";

    private static ReserveApplication instance = null;

    private TCPClient tcpClient = null;
    private Thread tcpThread = null;

    public static Context getInstance() {
        if (null == instance) {
            instance = new ReserveApplication();
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(LOG_TAG, "Приложение <Reserve> запущено");

        //получаем SharedPreferences, которое работает с файлом настроек
        SysInfo.getInstance().init(PreferenceManager.getDefaultSharedPreferences(this));
        SysInfo.getInstance().setDeviceId(ApplicationService.getDeviceUniqueID(this));

        if (!SysInfo.getInstance().isEmpty()) {
            startTCPClient();
        }
    }

    //инициализация подключения к серверу обновления данных
    public void startTCPClient() {

        if (tcpClient == null) {
            Log.d(LOG_TAG, "Create TCP client");

            tcpClient = new TCPClient(SysInfo.getInstance().getHost(),
                    SysInfo.getInstance().getPortUpdate());

            tcpClient.setOnConnectionLostListener(new NotifyEvent() {
                @Override
                public void onAction(Object sender) {
                    //Log.d(LOG_TAG, "Reserve TCP client: Connection lost");

                    ReconnectTask task = new ReconnectTask();
                    task.setOnReconnect(new NotifyEvent() {
                        @Override
                        public void onAction(Object sender) {
                            //Log.d(LOG_TAG, "Reserve TCP client: Reconnect");

                            startTCPClient();
                        }
                    });

                    task.execute();
                }
            });
        }

        if (tcpThread != null) {
            tcpThread.interrupt();
        }

        tcpThread = new Thread(tcpClient);
        tcpThread.setDaemon(true);
        tcpThread.start();
    }

    public TCPClient getTcpClient() {
        return tcpClient;
    }

}
