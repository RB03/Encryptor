package com.burman.rohit.encryptor;

import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Rohit on 3/21/2016.
 */
public class CheckList {
    private static ConcurrentHashMap<String, ElementModal> elementModals = new ConcurrentHashMap<>();
    private static ArrayList<String> paths;

    public static ElementModal getElementModal(String path) {
        return elementModals.get(path);
    }

    public static int size() {
        return elementModals.size();
    }


    public static boolean isEmpty() {
        return elementModals.isEmpty();
    }

    public static void addToCheckedList(ElementModal element) {
        elementModals.put(element.getPath(), element);
        ContextActionMode.itemAdded(element.getMode());
    }

    public static void removeFromCheckedList(ElementModal element) {
        elementModals.remove(element.getPath());
        ContextActionMode.itemRemoved(element.getMode());
    }

    public static void clear() {
        Iterator iterator = elementModals.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            ElementModal ele = (ElementModal) pair.getValue();
            ele.getCheckBox().setChecked(false);
            iterator.remove();
        }
    }

    public static void checkItems() {
        Iterator iterator = elementModals.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            ElementModal ele = (ElementModal) pair.getValue();
            ele.getCheckBox().setChecked(true);

        }
    }

    public static ArrayList<String> getPaths() {
        paths = new ArrayList<>();

        Iterator iterator = elementModals.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            paths.add((String) pair.getKey());
            Log.d(" adding to paths ", (String) pair.getKey());
        }
        return paths;
    }
}
