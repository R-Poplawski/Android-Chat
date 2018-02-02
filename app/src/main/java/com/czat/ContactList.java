package com.czat;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactList {

    public static final List<Contact> ITEMS = new ArrayList<>();
    //public static final SparseArray<Contact> ITEM_MAP = new SparseArray<>();

    public static void addItem(Contact item) {
        ITEMS.add(item);
        //ITEM_MAP.put(item.getId(), item);
    }

    public static void removeItem(Contact item) {
        int id = item.getId();
        ITEMS.remove(item);
        //ITEM_MAP.remove(id);
    }
}
