package com.czat;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

public class RequestList {

    public static final List<Request> ITEMS = new ArrayList<>();
    //public static final SparseArray<Request> ITEM_MAP = new SparseArray<>();

    public static void addItem(Request item) {
        ITEMS.add(item);
        //ITEM_MAP.put(item.getId(), item);
    }

    public static void removeItem(Request item) {
        int id = item.getId();
        ITEMS.remove(item);
        //ITEM_MAP.remove(id);
    }
}
