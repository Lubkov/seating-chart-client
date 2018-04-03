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

import ua.stellar.seatingchart.domain.Operation;
import ua.stellar.seatingchart.domain.SysInfo;
import ua.stellar.seatingchart.domain.TheUser;
import ua.stellar.seatingchart.event.NotifyEvent;
import ua.stellar.seatingchart.event.OnLoginListener;
import ua.stellar.seatingchart.event.OnOperationLoad;
import ua.stellar.seatingchart.service.MapService;
import ua.stellar.seatingchart.utils.MapPageAdapter;
import ua.stellar.ua.test.seatingchart.R;

public class MainActivity extends FragmentActivity implements OnLoginListener {

    private final String LOG_TAG = "RESERVE";

    //UI links
    private RelativeLayout container;
    private ViewPager mapContainer;

    private TotalsFragment totals;

    private MapService mapService;

    public MainActivity() {
        super();

        mapService = new MapService(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(LOG_TAG, "Main activity: OnCreate");
        if (mapService.getLayouts() != null) {
            Log.d(LOG_TAG, "Есть информация о карте");
        }

        container = (RelativeLayout) findViewById(android.R.id.tabhost);
        mapContainer = (ViewPager) findViewById(R.id.mapContainer);
        mapService.setMapPageAdapter(new MapPageAdapter(getSupportFragmentManager()));

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
    }

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
        if ((mapService.getCount() == 0) || (container == null)) {
            return;
        }

        mapService.createMapFragments();
        mapContainer.setAdapter(mapService.getMapPageAdapter());

        totals = new TotalsFragment();
        totals.mapService = mapService;
        FragmentTransaction fragmentTrans = getSupportFragmentManager().beginTransaction();
        fragmentTrans.replace(R.id.paTotals, totals).commit();

        mapService.setOnUpdateTotals(new NotifyEvent() {
            @Override
            public void onAction(Object sender) {
                loadTotals();
            }
        });

        mapService.setAfterChangeEvent(new OnOperationLoad() {
            @Override
            public void onLoad(List<Operation> operations) {
                totals.addOperations(operations);
            }

            @Override
            public void onError(String error) {

            }
        });

        //загрузка итогов
        loadTotals();

        //создание слушателя на событие - "Автоматическое обноление данных"
        mapService.addTCPClientListener();

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
        mapService.loadLayoutList();
        initMap();
    }

    private void loadTotals() {
        totals.updateTotals(mapService.getLayouts());
    }

}
