package ua.stellar.seatingchart.event;

import ua.stellar.seatingchart.domain.TheUser;

public interface OnLoginListener {

    public void onLogin(TheUser user);
}
