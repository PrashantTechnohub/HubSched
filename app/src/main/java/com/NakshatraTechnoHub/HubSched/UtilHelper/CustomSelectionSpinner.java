package com.NakshatraTechnoHub.HubSched.UtilHelper;


import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
        import androidx.appcompat.app.AlertDialog;
        import androidx.appcompat.widget.SearchView;
        import androidx.recyclerview.widget.LinearLayoutManager;

import  com.devstune.searchablemultiselectspinner.R;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.devstune.searchablemultiselectspinner.SearchableAdapter;
import com.devstune.searchablemultiselectspinner.SearchableItem;
import com.devstune.searchablemultiselectspinner.SelectionCompleteListener;

import java.util.ArrayList;

public class CustomSelectionSpinner {
    public static void show(
            Context context,
            String doneButtonText,
            ArrayList<SearchableItem> searchableItems,
            final SelectionCompleteListener selectionCompleteListener
    ) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View convertView = inflater.inflate(R.layout.searchable_list_layout, null);

        alertDialog.setView(convertView);

        final SearchView searchView = convertView.findViewById(R.id.searchView);
        searchView.setQueryHint("Search Employee by Email");
        searchView.setIconifiedByDefault(false); // Expand the search view by default
        searchView.setIconified(false);
        final RecyclerView recyclerView = convertView.findViewById(R.id.recyclerView);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);

        final SearchableAdapter adapter = new SearchableAdapter(
                context,
                searchableItems,
                searchableItems,
                new SearchableAdapter.ItemClickListener() {
                    @Override
                    public void onItemClicked(SearchableItem item, int position, boolean b) {
                        for (int i = 0; i < searchableItems.size(); i++) {
                            if (searchableItems.get(i).getCode().equals(item.getCode())) {
                                searchableItems.get(i).setSelected(b);
                                break;
                            }
                        }
                    }
                },
                false
        );
        recyclerView.setItemAnimator(null);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // do something on text submit
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // do something when text changes
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        alertDialog.setPositiveButton(doneButtonText, (dialogInterface, i) -> {
            dialogInterface.dismiss();
            ArrayList<SearchableItem> resultList = new ArrayList<>();
            for (int index = 0; index < searchableItems.size(); index++) {
                if (searchableItems.get(index).isSelected()) {
                    resultList.add(searchableItems.get(index));
                }
            }
            selectionCompleteListener.onCompleteSelection(resultList);
        });


        alertDialog.setNeutralButton("Clear all", (dialogInterface, i) -> {
            for (SearchableItem item : searchableItems) {
                item.setSelected(false);
            }
        });

        alertDialog.show();
    }
}
