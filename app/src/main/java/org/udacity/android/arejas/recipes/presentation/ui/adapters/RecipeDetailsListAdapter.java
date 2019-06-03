package org.udacity.android.arejas.recipes.presentation.ui.adapters;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.udacity.android.arejas.recipes.R;
import org.udacity.android.arejas.recipes.databinding.ItemRecipeDetailsListBinding;
import org.udacity.android.arejas.recipes.domain.entities.Recipe;
import org.udacity.android.arejas.recipes.domain.entities.RecipeIngredient;
import org.udacity.android.arejas.recipes.domain.entities.RecipeStep;

import java.util.List;

public class RecipeDetailsListAdapter extends RecyclerView.Adapter<RecipeDetailsListAdapter.RecipeDetailsViewHolder> {


    private final OnRecipesDetailsItemClickListener mListener;

    private Recipe mData;

    public RecipeDetailsListAdapter(@NonNull OnRecipesDetailsItemClickListener mListener) {
        this.mListener = mListener;
    }

    public Recipe getData() {
        return mData;
    }

    public void setData(Recipe mData) {
        this.mData = mData;
    }

    @Override
    public RecipeDetailsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemRecipeDetailsListBinding binding = DataBindingUtil.inflate(inflater, R.layout.item_recipe_details_list,
                parent, false);
        return new RecipeDetailsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(final RecipeDetailsViewHolder holder, int position) {
        holder.itemView.setTag(position);
        if (ingredientsListClicked(position)) {
            holder.bindAsIngredients();
        } else {
            RecipeStep stepToShow = getRecipeStepByListPosition(position);
            holder.bindAsStep(stepToShow, position);
        }
    }

    private boolean dataHasSteps() {
        return ((mData != null) && (mData.getSteps() != null));
    }

    private RecipeStep getRecipeStepByListPosition(int position) {
        if (dataHasSteps()) {
            int index = position;
            if (dataHasIngredientsList()) index--;
            if (index >= 0) return mData.getSteps().get(index);
            else return null;
        } else {
            return null;
        }
    }

    private Integer getRecipeStepPositionByListPosition(int position) {
        if (dataHasSteps()) {
            int index = position;
            if (dataHasIngredientsList()) index--;
            if (index >= 0) return index;
            else return null;
        } else {
            return null;
        }
    }

    private List<RecipeIngredient> getRecipeIngredientsList() {
        if (dataHasIngredientsList()) {
            return mData.getIngredients();
        } else {
            return null;
        }
    }

    private boolean dataHasIngredientsList() {
        return ((mData != null) && (mData.getIngredients() != null) &&
                (!mData.getIngredients().isEmpty()));
    }

    private boolean ingredientsListClicked(int position) {
        return ((position == 0) && dataHasIngredientsList());
    }

    @Override
    public int getItemCount() {
        int itemCount = 0;
        if (dataHasIngredientsList()) {
            itemCount++;
        }
        if (dataHasSteps()) {
            itemCount += mData.getSteps().size() ;
        }
        return itemCount;
    }

    class RecipeDetailsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        boolean bIsIngredientsDetails;
        Integer iStepPositionToShow;
        final ItemRecipeDetailsListBinding binding;

        RecipeDetailsViewHolder(ItemRecipeDetailsListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            // Click listener set for calling user defined listener
            binding.getRoot().setOnClickListener(this);
        }

        void bindAsIngredients() {
            bIsIngredientsDetails = true;
            binding.setIsIngredientElement(true);
        }

        void bindAsStep(RecipeStep stepToShow, Integer iPosition) {
            bIsIngredientsDetails = false;
            iStepPositionToShow = getRecipeStepPositionByListPosition(iPosition);
            binding.setIsIngredientElement(false);
            binding.setListItemName((stepToShow != null) ? stepToShow.getShortDescription() : null);
        }

        @Override
        public void onClick(View v) {
            if (bIsIngredientsDetails) {
                mListener.onIngredientsClicked();
            } else {
                if (iStepPositionToShow != null) {
                    mListener.onStepClicked(iStepPositionToShow);
                }
            }
        }
    }

    /**
     * Callback to implement for defining user action when detail item clicked.
     */
    public interface OnRecipesDetailsItemClickListener {

        void onIngredientsClicked();

        void onStepClicked(Integer stepPositionId);
    }
}