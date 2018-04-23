package ua.stellar.seatingchart.domain;

import android.content.SharedPreferences;

import java.util.List;

public class SysInfo {

    private static final SysInfo instance = new SysInfo();

    //параметры подключения к серверу
    private String host;
    private Integer port;
    private String domain;
    private Integer portUpdate;
    private TheUser user;
    private String deviceId;
    private String version = "1.0.3";

    //список вилов товара
    private List<GoodsType> goodsTypes = null;

    //список типов операций(состояния)
    private List<OperationType> operationTypes = null;

    private String layoutIdList = "";

    private SysInfo() {
        host = "";
        port = 0;
        domain = "";
        portUpdate = 0;
    }

    public static SysInfo getInstance() {
        return instance;
    }

    public boolean isEmpty() {
        return ((host.equals("")) ||
                (port == 0) ||
                (domain.equals("") ||
                (portUpdate == 0)));
    }

    public void init(SharedPreferences pref) {
        if (pref != null) {
            //pref.edit().clear().commit();

            host = pref.getString("host", "");
            port = Integer.parseInt(pref.getString("port", "0"));
            domain = pref.getString("domain", "");
            portUpdate = Integer.parseInt(pref.getString("portUpdate", "0"));
        }
    }

    public String getUrlAddress() {
        String url = "http://" + host;

        if (getPort() > 0) {
            url = url + ":" + Integer.toString(port);
        }

        url = url + "/" + domain;

        return url;
    }

    public GoodsType getGoodsType(Long id) {

        if (goodsTypes == null) {
            return null;
        }

        for (GoodsType item : goodsTypes) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }

    public OperationType getOperationType(Long id) {

        if (operationTypes == null) {
            return null;
        }

        for (OperationType item : operationTypes) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }

    public String getLayoutIdList() {
        return layoutIdList;
    }

    public void setLayoutIdList(final List<Layout> layouts) {
        String id = "";
        if (layouts == null) {
            layoutIdList = id;
            return;
        }
        for (Layout layout : layouts) {
            if (id != "") {
                id += ",";
            }
            id += layout.getId().toString();
        }

        layoutIdList = id;
    }


    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public List<GoodsType> getGoodsTypes() {
        return goodsTypes;
    }

    public void setGoodsTypes(List<GoodsType> goodsTypes) {
        this.goodsTypes = goodsTypes;
    }

    public TheUser getUser() {
        return user;
    }

    public void setUser(TheUser user) {
        this.user = user;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Integer getPortUpdate() {
        return portUpdate;
    }

    public void setPortUpdate(Integer portUpdate) {
        this.portUpdate = portUpdate;
    }

    public List<OperationType> getOperationTypes() {
        return operationTypes;
    }

    public void setOperationTypes(List<OperationType> operationTypes) {
        this.operationTypes = operationTypes;
    }

    public String getVersion() {
        return version;
    }
}

