package org.udacity.android.arejas.recipes.presentation.ui.adapters;

import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.udacity.android.arejas.recipes.R;
import org.udacity.android.arejas.recipes.databinding.CardViewRecipeBinding;
import org.udacity.android.arejas.recipes.domain.entities.RecipeListItem;

/*
 * Class used for the management of lists of recipe.
 */
public class RecipesListAdapter extends android.arch.paging.PagedListAdapter<RecipeListItem,
        RecipesListAdapter.RecipeListViewHolder> {

    private final OnRecipesListItemClickListener mListener;

    public RecipesListAdapter(
            OnRecipesListItemClickListener clickListener) {
        super(diffCallback);
        this.mListener = clickListener;
    }

    @NonNull
    @Override
    public RecipeListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        CardViewRecipeBinding binding = DataBindingUtil.inflate(inflater, R.layout.card_view_recipe,
                parent, false);
        return new RecipeListViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeListViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    private static final DiffUtil.ItemCallback<RecipeListItem> diffCallback =
            new DiffUtil.ItemCallback<RecipeListItem>() {

                @Override
                public boolean areItemsTheSame(@NonNull RecipeListItem oldItem, @NonNull RecipeListItem newItem) {
                    return oldItem.getId().equals(newItem.getId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull RecipeListItem oldItem, @NonNull RecipeListItem newItem) {
                    return (oldItem.getId().equals(newItem.getId())) &&
                            oldItem.getName().equals(newItem.getName()) &&
                            oldItem.getServings().equals(newItem.getServings()) &&
                            oldItem.getImage().equals(newItem.getImage());
                }

            };

    /**
     * View holder for the adapter.
     */
    class RecipeListViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {

        RecipeListItem item;
        final CardViewRecipeBinding uiBinding;

        /**
         * Constructor
         *
         * @param binding
         */
        RecipeListViewHolder(CardViewRecipeBinding binding) {
            super(binding.getRoot());
            binding.executePendingBindings();
            uiBinding = binding;
            // Click listener set for calling user defined listener
            binding.getRoot().setOnClickListener(this);
        }

        /**
         * Function called when a item list has to be visible, to bind it to the current holder.
         *
         * @param item Movie list item to bind. If null, show the load more layout.
         */
        @SuppressLint("DefaultLocale")
        void bind(RecipeListItem item) {
            this.item = item;
            // If not null
            if (item != null) {
                // If not null, set movie item
                uiBinding.setRecipeListItem(item);
            }
        }

        /**
         * Function called when item clicked. User defined callback will be invoked
         * @param view
         */
        @Override
        public void onClick(View view) {
            mListener.onClick((this.item != null) ? this.item.getId() : null);
        }
    }

    /**
     * Callback to implement for defining user action when item clicked.
     */
    public interface OnRecipesListItemClickListener {
        void onClick(Integer recipeId);
    }

}
