package ua.stellar.seatingchart.service;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ua.stellar.seatingchart.MainActivity;
import ua.stellar.seatingchart.MapFragment;
import ua.stellar.seatingchart.ReserveApplication;
import ua.stellar.seatingchart.ResourceItem;
import ua.stellar.seatingchart.domain.Layout;
import ua.stellar.seatingchart.domain.LayoutComposition;
import ua.stellar.seatingchart.domain.Operation;
import ua.stellar.seatingchart.domain.SysInfo;
import ua.stellar.seatingchart.event.NotifyEvent;
import ua.stellar.seatingchart.event.OnDataUpdateListener;
import ua.stellar.seatingchart.event.OnOperationLoad;
import ua.stellar.seatingchart.task.BackgroundUpdateTask;
import ua.stellar.seatingchart.task.LoadLayoutsTask;
import ua.stellar.seatingchart.task.OperationLoadTask;
import ua.stellar.seatingchart.tcp.TCPClient;
import ua.stellar.seatingchart.utils.JsonResponse;
import ua.stellar.seatingchart.utils.MapPageAdapter;

public class MapService {

//    private static final MapService instance = new MapService();

    private final String LOG_TAG = "RESERVE";

    //map list
    private List<Layout> layouts;

    private MainActivity activity;

    private MapPageAdapter mapPageAdapter;

    private Long lastUpdateID = 0L;

    public MapService(final MainActivity activity) {
        this.activity = activity;
    }

    //мнмцмализация сервиса
    public void init() {
        mapPageAdapter = new MapPageAdapter(activity.getSupportFragmentManager());
        createMapFragments();
    }

    public void loadLayoutList() {
        LoadLayoutsTask task = new LoadLayoutsTask(activity);

        try {
            layouts = task.execute().get();
        } catch (Exception e) {
            //TODO: добавить показ ошибки с возможность закрыть приложение, либо повторить загрузку данных
            e.printStackTrace();
        }
    }

    private void createMapFragments() {
        for (Layout layout: layouts) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("layout", layout);

            MapFragment mapFragment = new MapFragment();
            mapFragment.setArguments(bundle);
            mapPageAdapter.addFragment(mapFragment, layout.getName());
        }
    }

    public void loadAllOperation(OnOperationLoad loadEvent) {

        OperationLoadTask task = new OperationLoadTask(activity, getLayoutIdList());
        task.setOnLoadingComplete(new NotifyEvent<JsonResponse>() {
            @Override
            public void onAction(JsonResponse response) {
                List<Operation> list = null;
                if (response.isSuccess()) {
                    try {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<ArrayList<Operation>>(){}.getType();
                        String innerJson = gson.toJson(response.getResult());
                        list = gson.fromJson(innerJson, listType);
                        Log.d(LOG_TAG, "Загружено " + list.size() + " операций");
                        loadEvent.onLoad(list);
                    } catch(Exception e) {
                        loadEvent.onError("Загрузка операций: " + e.getMessage());
                    }
                } else {
                    loadEvent.onError("Загрузка операций, success = false");
                }
            }
        });
        task.execute();
    }

    public void loadChanges() {
        Log.d(LOG_TAG, "Обновление данных");
        String url = getLoadLayoutCompositionsUrl(getLayoutIdList(), lastUpdateID); //layout.getId()

        //загрузить обновление данных
        BackgroundUpdateTask task = new BackgroundUpdateTask(url);

        task.setOnLoadingComplete(new NotifyEvent<List<LayoutComposition>>() {
            public void onAction(List<LayoutComposition> items) {
                if (items != null) {
                    Log.d(LOG_TAG, "Необходимо обновить " + items.size() + " ресурсов");

                    List<Operation> operations = new ArrayList<>();
                    for (LayoutComposition item : items) {
                        //поиск ресурса на карте
                        ResourceItem resourceItem = getResourceItem(item);

                        //нашли ресурс на карте
                        if (resourceItem != null) {
                            Log.d(LOG_TAG, "Old resource state: " + resourceItem.getLayoutComposition().getLastOper().getOperationType());
                            Log.d(LOG_TAG, "New resource state: " + item.getLastOper().getOperationType());

                            //заменяем последнюю операцию
                            resourceItem.getLayoutComposition().setLastOper(item.getLastOper());

                            //обновляем визуальное состояние
                            resourceItem.update();

                            if ((item.getLastOper().getId() != null) && (item.getLastOper().getId() > 0)) {
                                operations.add(new Operation(item.getLastOper()));
                            }
                        } else {
                            Log.e(LOG_TAG, "Ресурс не найден на карте");
                        }

                        //обновить ID последней загруженной операции с сервера
                        setLastOperation(item.getLastOper());
                    }

                    activity.addOperations(operations);
                } else {
                    Log.d(LOG_TAG, "Данные актуальны");
                }
            }
        });
        task.execute();

        //обновление итогов
        activity.loadTotals();
    }

    public List<Layout> getLayouts() {
        return layouts;
    }

    public long getCount() {
        if (layouts == null) {
            return 0;
        }
        return layouts.size();
    }

    public MapPageAdapter getMapPageAdapter() {
        return mapPageAdapter;
    }

    //создание слушателя на событие - "Автоматическое обноление данных"
    public void addTCPClientListener() {
        ReserveApplication application = (ReserveApplication) activity.getApplicationContext();
        TCPClient client = application.getTcpClient();

        if (client != null) {
            OnDataUpdateListener onDataUpdate = new OnDataUpdateListener() {
                @Override
                public void onDataUpdate(String tag) {

                    //загрузить обновление данных
                    loadChanges();
                }
            };
            client.setOnDataUpdateListener(onDataUpdate);
        }
    }

    public String getLayoutIdList() {
        String id = "";
        for (Layout layout : layouts) {
            if (id != "") {
                id += ",";
            }
            id += layout.getId().toString();
        }

        return id;
    }

    public Long getLastUpdateID() {
        return lastUpdateID;
    }

    public void setLastOperation(final Operation operation) {
        if (operation.getId() > lastUpdateID) {
            lastUpdateID = operation.getId();
        }
    }

    private String getLoadLayoutCompositionsUrl(final String layoutID, final Long lastOperID) {
        return SysInfo.getInstance().getUrlAddress() +
                "/order/get-layout-compositions?" +
                "layout_id=" + layoutID + "&" +
                "last_oper_id=" + lastOperID;
    }

    private ResourceItem getResourceItem(final LayoutComposition search) {

        for (int i = 0; i < mapPageAdapter.getCount(); i++) {
            MapFragment layout = (MapFragment) mapPageAdapter.getItem(i);
            ResourceItem item = layout.getResourceItem(search);
            if (item != null) {
                return item;
            }
        }

        return null;
    }

}
