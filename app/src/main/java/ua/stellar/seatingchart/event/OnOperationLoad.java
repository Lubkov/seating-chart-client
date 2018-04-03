package ua.stellar.seatingchart.event;


import java.util.List;

import ua.stellar.seatingchart.domain.Operation;

public interface OnOperationLoad {

    void onLoad(List<Operation> operations);
    void onError(String error);
}
