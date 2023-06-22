package com.NakshatraTechnoHub.HubSched;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import com.NakshatraTechnoHub.HubSched.Ui.Dashboard.EmployeeListActivity;
import com.google.android.material.card.MaterialCardView;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.view.View;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EmployeeListTest {

    @Before
    public void launchActivity() {
        ActivityScenario.launch(EmployeeListActivity.class);
    }

    @Test
    public void testEditProfileButtonAndActivity() {
//        // Verify that the EmployeeListActivity is displayed
//        Espresso.onView(ViewMatchers.withId(R.id.empList_recyclerView))
//                .check(matches(isDisplayed()));
//
//        // Perform click on the edit button of the first employee item
//        Espresso.onView(withId(R.id.empList_recyclerView))
//                .perform(RecyclerViewActions.actionOnItemAtPosition(0, clickEditButton()));
//
//
//        // Go back to the EmployeeListActivity
//        Espresso.pressBack();
//
//        // Verify that the EmployeeListActivity is displayed again
//        Espresso.onView(withId(R.id.empList_recyclerView))
//                .check(matches(isDisplayed()));


        Espresso.onView(ViewMatchers.withId(R.id.empList_recyclerView))
                .check(matches(isDisplayed()));

        // Get the total item count in the RecyclerView
        int itemCount = getRecyclerViewItemCount(R.id.empList_recyclerView);

        // Iterate through each item and perform the click action
        for (int position = 0; position < itemCount; position++) {
            Espresso.onView(withId(R.id.empList_recyclerView))
                    .perform(RecyclerViewActions.actionOnItemAtPosition(position, clickEditButton()));


            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            // Go back to the EmployeeListActivity
            Espresso.pressBack();



            // Verify that the EmployeeListActivity is displayed again
            Espresso.onView(withId(R.id.empList_recyclerView))
                    .check(matches(isDisplayed()));
        }

    }

    private static ViewAction clickEditButton() {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(MaterialCardView.class);
            }

            @Override
            public String getDescription() {
                return "Click on edit button within MaterialCardView";
            }

            @Override
            public void perform(UiController uiController, View view) {
                MaterialCardView cardView = (MaterialCardView) view;
                View editButton = cardView.findViewById(R.id.edit_emp_btn);
                if (editButton != null) {
                    editButton.performClick();
                }
            }
        };
    }
    private int getRecyclerViewItemCount(int recyclerViewId) {
        final int[] itemCount = new int[1];
        Espresso.onView(ViewMatchers.withId(recyclerViewId)).check(new ViewAssertion() {
            @Override
            public void check(View view, NoMatchingViewException noViewFoundException) {
                if (view instanceof RecyclerView) {
                    RecyclerView recyclerView = (RecyclerView) view;
                    itemCount[0] = recyclerView.getAdapter().getItemCount();
                }
            }
        });
        return itemCount[0];
}
}
