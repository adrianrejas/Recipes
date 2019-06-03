package org.udacity.android.arejas.recipes.utils;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewAssertion;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.common.truth.Truth;

import java.util.ArrayList;
import org.hamcrest.Matcher;
import org.junit.Assert;

import static android.view.View.FIND_VIEWS_WITH_TEXT;


public final class RecyclerViewAssertions {

    public static ViewAssertion hasItemsCount(final int count) {
        return new ViewAssertion() {
            @Override public void check(View view, NoMatchingViewException e) {
                if (!(view instanceof RecyclerView)) {
                    throw e;
                }
                RecyclerView rv = (RecyclerView) view;
                Truth.assertThat(rv.getAdapter().getItemCount()).isEqualTo(count);
            }
        };
    }

    public static ViewAssertion hasHolderItemAtPosition(final int index,
                                                        final Matcher<RecyclerView.ViewHolder> viewHolderMatcher) {
        return new ViewAssertion() {
            @Override public void check(View view, NoMatchingViewException e) {
                if (!(view instanceof RecyclerView)) {
                    throw e;
                }
                RecyclerView rv = (RecyclerView) view;
                Assert.assertThat(rv.findViewHolderForAdapterPosition(index), viewHolderMatcher);
            }
        };
    }

    public static ViewAssertion hasViewWithTextAtPosition(final int index, final CharSequence text) {
        return new ViewAssertion() {
            @Override public void check(View view, NoMatchingViewException e) {
                if (!(view instanceof RecyclerView)) {
                    throw e;
                }
                RecyclerView rv = (RecyclerView) view;
                ArrayList<View> outviews = new ArrayList<>();
                rv.findViewHolderForAdapterPosition(index).itemView.findViewsWithText(outviews, text,
                        FIND_VIEWS_WITH_TEXT);
                Truth.assert_().withMessage("There's no view at index "+ index + " of recyclerview that has text : "+ text)
                        .that(outviews).isNotEmpty();
            }
        };
    }

    public static ViewAssertion doesntHaveViewWithText(final String text) {
        return new ViewAssertion() {
            @Override public void check(View view, NoMatchingViewException e) {
                if (!(view instanceof RecyclerView)) {
                    throw e;
                }
                RecyclerView rv = (RecyclerView) view;
                ArrayList<View> outviews = new ArrayList<>();
                for (int index = 0; index < rv.getAdapter().getItemCount(); index++) {
                    rv.findViewHolderForAdapterPosition(index).itemView.findViewsWithText(outviews, text,
                            FIND_VIEWS_WITH_TEXT);
                    if (outviews.size() > 0) break;
                }
                Truth.assertThat(outviews).isEmpty();
            }
        };
    }
}