package com.example.todolist;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements Filterable {
    private Context context;
    private List<Item> itemList;
    private List<Item> searchItemList;

    public RecyclerViewAdapter(Context context, List<Item> itemList){
        this.context = context;
        this.itemList = itemList;
        searchItemList = new ArrayList<>(itemList);
    }
    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row, parent, false);
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {

        Item item = itemList.get(position);
        holder.item1.setText(item.getListName());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView item1;

        public ViewHolder(@NonNull View itemView, Context c) {
            super(itemView);
            context = c;
            item1 = itemView.findViewById(R.id.item1);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Item item = itemList.get(position);
                    Intent intent = new Intent(context, TaskActivity.class);
                    intent.putExtra("ListName", item.getListName());
                    intent.putExtra("ItemId", item.getId());

                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public Filter getFilter(){
        return itemSearch;
    }

    private Filter itemSearch = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Item> filteredList = new ArrayList<>();
            if(constraint == null || constraint.length() == 0){
                filteredList.addAll(searchItemList);
            }else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Item item : searchItemList){
                    if (item.getListName().toLowerCase().contains(filterPattern)){
                        filteredList.add(item);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            itemList.clear();
            itemList.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };
}
















