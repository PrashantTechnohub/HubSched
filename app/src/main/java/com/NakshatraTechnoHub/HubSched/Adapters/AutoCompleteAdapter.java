package com.NakshatraTechnoHub.HubSched.Adapters;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class AutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
    private final List<String> dataList;
    private final Context context;
    private final JSONArray jsonArray;

    public AutoCompleteAdapter(Context context, JSONArray jsonArray) {
        super(context, android.R.layout.simple_dropdown_item_1line);
        this.context = context;
        this.jsonArray = jsonArray;
        dataList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public String getItem(int position) {
        return dataList.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    dataList.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            String name = jsonArray.getJSONObject(i).getString("name");
                            String email = jsonArray.getJSONObject(i).getString("email");
                            if (name.toLowerCase().startsWith(constraint.toString().toLowerCase())
                                    || email.toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                                dataList.add(name);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    filterResults.values = dataList;
                    filterResults.count = dataList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }
}
