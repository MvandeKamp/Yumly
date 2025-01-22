package com.mvandekamp.yumly.ui.recipe;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mvandekamp.yumly.R;
import com.mvandekamp.yumly.models.CookingStep;

import java.util.List;

public class StepAdapter extends RecyclerView.Adapter<StepAdapter.ViewHolder> {

    private final List<CookingStep> steps;

    public StepAdapter(List<CookingStep> steps) {
        this.steps = steps;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_step, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CookingStep step = steps.get(position);
        holder.stepEditText.setText(step.description);

        // Handle step text changes
        holder.stepEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                step.description = holder.stepEditText.getText().toString();
            }
        });

        // Handle remove button click
        holder.removeButton.setOnClickListener(v -> {
            steps.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, steps.size());
        });
    }

    @Override
    public int getItemCount() {
        return steps.size();
    }

    public void addStep(CookingStep step) {
        steps.add(step);
        notifyItemInserted(steps.size() - 1);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        EditText stepEditText;
        ImageButton removeButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            stepEditText = itemView.findViewById(R.id.stepEditText);
            removeButton = itemView.findViewById(R.id.removeStepButton);
        }
    }
}