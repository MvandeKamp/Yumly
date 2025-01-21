package com.mvandekamp.yumly.ui.inventory;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mvandekamp.yumly.R;
import com.mvandekamp.yumly.models.InventoryItem;
import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> {

    private final List<InventoryItem> inventoryItems;

    public InventoryAdapter(List<InventoryItem> inventoryItems) {
        this.inventoryItems = inventoryItems;
    }

    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inventory, parent, false);
        return new InventoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryViewHolder holder, int position) {
        InventoryItem item = inventoryItems.get(position);
        holder.itemName.setText(item.name);
        holder.itemExpirationDate.setText("Expiration Date: " + item.estimatedExpirationDate);
        holder.itemQuantity.setText("Quantity: " + item.estimatedQuantity);

        // Placeholder for image 
        holder.itemImage.setImageResource(android.R.drawable.ic_menu_report_image);
    }

    @Override
    public int getItemCount() {
        return inventoryItems.size();
    }

    static class InventoryViewHolder extends RecyclerView.ViewHolder {

        ImageView itemImage;
        TextView itemName;
        TextView itemExpirationDate;
        TextView itemQuantity;

        public InventoryViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage);
            itemName = itemView.findViewById(R.id.itemName);
            itemExpirationDate = itemView.findViewById(R.id.itemExpirationDate);
            itemQuantity = itemView.findViewById(R.id.itemQuantity);
        }
    }
}