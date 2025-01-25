package com.mvandekamp.yumly.ui.inventory;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mvandekamp.yumly.R;
import com.mvandekamp.yumly.models.Ingridient;
import com.mvandekamp.yumly.models.Recipe;

import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> {

    private List<Ingridient> ingridients;
    private OnIngredientClickListener onIngredientClickListener;

    public InventoryAdapter(List<Ingridient> ingridients) {
        this.ingridients = ingridients;
    }

    public void updateIngredients(List<Ingridient> ingridient) {
        ingridients.clear();
        ingridients.addAll(ingridient);
        notifyDataSetChanged();
    }

    public interface OnIngredientClickListener {
        void onIngredientClick(Ingridient ingredient, int position);
    }

    public void setOnIngredientClickListener(OnIngredientClickListener listener) {
        this.onIngredientClickListener = listener;
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
        Ingridient ingridient = ingridients.get(position);
        holder.itemName.setText(ingridient.toString());
        holder.itemExpirationDate.setText("Expiration Date: " + (ingridient.estimatedExpirationDate != null ? ingridient.estimatedExpirationDate : "N/A"));
        holder.itemQuantity.setText(String.format("Quantity: %.2f %s", ingridient.getAmount(), ingridient.getUnit().getUnit()));

        // Placeholder for image
        holder.itemImage.setImageResource(android.R.drawable.ic_menu_report_image);

        // Handle item click
        holder.itemView.setOnClickListener(v -> {
            if (onIngredientClickListener != null) {
                onIngredientClickListener.onIngredientClick(ingridient, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ingridients.size();
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