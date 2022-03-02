package com.utility;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class JSONUtil<T> {
    public List<T> getListFromJsonArray(JSONArray jsonArray) {
        List<T> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++ ) {
            list.add((T) jsonArray.get(i));
        }
        return list;
    }
}
