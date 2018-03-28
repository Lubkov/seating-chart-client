package ua.stellar.seatingchart;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.List;

import ua.stellar.seatingchart.domain.Layout;
import ua.stellar.seatingchart.domain.SysInfo;
import ua.stellar.seatingchart.domain.TheUser;
import ua.stellar.seatingchart.event.OnLoginListener;
import ua.stellar.seatingchart.task.LoadLayoutsTask;
import ua.stellar.seatingchart.utils.MapPageAdapter;
import ua.stellar.ua.test.seatingchart.R;

public class MainActivity extends FragmentActivity implements OnLoginListener {

    private final String LOG_TAG = "RESERVE";

    //UI links
    private RelativeLayout container;
    //private MapFragment mapFragment = null;
    private ViewPager mapContainer;

    //data
    private List<Layout> layouts = null;

    private MapPageAdapter mapPageAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(LOG_TAG, "Main activity: OnCreate");
        if (layouts != null) {
            Log.d(LOG_TAG, "Есть информация о карте");
        }

        container = (RelativeLayout) findViewById(android.R.id.tabhost);
        mapContainer = (ViewPager) findViewById(R.id.mapContainer);
        mapPageAdapter = new MapPageAdapter(getSupportFragmentManager());

        if (SysInfo.getInstance().isEmpty()) {
            showSettings();
        }

        if (SysInfo.getInstance().isEmpty()) {
            Toast.makeText(this, "Необходимо настроить подключение к серверу", Toast.LENGTH_SHORT).show();
        } else {
            if ((SysInfo.getInstance().getUser() == null) ||
                    (SysInfo.getInstance().getUser().getId() == null)) {

                LoginUserDialog loginDialog = new LoginUserDialog();
                loginDialog.setOnLoginListener(this);
                loginDialog.show(this.getFragmentManager(), "login");
            } else {
                onLogin(SysInfo.getInstance().getUser());
            }
        }

//        //инициализация подключения к серверу обновления данных
//        initTCPClient();
    }

//    private void initTCPClient() {
//        TCPClient client = new TCPClient(SysInfo.getInstance().getHost(),
//                                         SysInfo.getInstance().getPortUpdate());
//        OnDataUpdateListener onDataUpdate =  new OnDataUpdateListener() {
//            @Override
//            public void onDataUpdate(String tag) {
//
//                //загрузить обновление данных
//                mapFragment.loadUpdateData();
//            }
//        };
//        client.setOnDataUpdateListener(onDataUpdate);
//
//        Thread thread = new Thread(client);
//        thread.setDaemon(true);
//        thread.start();
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add("Настройки подключения");
//        MenuItem item = menu.add(0, 1, 0, "Настройки");
//        item.setIntent(new Intent(this, SettingsActivity.class));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //показать настройки
        showSettings();

//        Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }

    private void initMap() {
        if ((layouts == null) || (layouts.size() == 0)) {
            return;
        }

        if (container == null) {
            return;
        }

        for (Layout layout: layouts) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("layout", layout);

            MapFragment mapFragment = new MapFragment();
            mapFragment.setArguments(bundle);
            mapPageAdapter.addFragment(mapFragment, layout.getName());
        }
        mapContainer.setAdapter(mapPageAdapter);

//        Layout layout = layouts.get(0);
//        Bundle bundle = new Bundle();
//        bundle.putParcelable("layout", layout);
//
//        mapFragment = new MapFragment();
//        mapFragment.setArguments(bundle);
//        FragmentTransaction fragmentTrans = getSupportFragmentManager().beginTransaction();
//        fragmentTrans.replace(container.getId(), mapFragment).commit();

//        mapPageAdapter.addFragment(mapFragment, layout.getName());
//        mapContainer.setAdapter(mapPageAdapter);

        mapContainer.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                Log.d("LOG_TAG", "onPageSelected, position = " + position);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void showSettings() {
        Intent settingsActivity = new Intent(getBaseContext(), SettingsActivity.class);
        startActivity(settingsActivity);
    }

    @Override
    public void onLogin(TheUser user) {
        SysInfo.getInstance().setUser(user);

        LoadLayoutsTask task = new LoadLayoutsTask(this);

        try {
            layouts = task.execute().get();
            initMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
