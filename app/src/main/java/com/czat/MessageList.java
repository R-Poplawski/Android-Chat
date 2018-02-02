package com.czat;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

public class MessageList {
    public static final List<Message> ITEMS = new ArrayList<>();
    //public static final SparseArray<Message> ITEM_MAP = new SparseArray<>();

    public static void addItem(Message item) {
        ITEMS.add(item);
        //ITEM_MAP.put(item.getId(), item);
    }

    public static void removeItem(Message item) {
        int id = item.getId();
        ITEMS.remove(item);
        //ITEM_MAP.remove(id);
    }

    public static void clear() {
        ITEMS.clear();
        //ITEM_MAP.clear();
    }
}
