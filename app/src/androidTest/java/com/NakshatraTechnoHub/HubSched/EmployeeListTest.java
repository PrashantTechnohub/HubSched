package com.NakshatraTechnoHub.HubSched;

import static androidx.test.espresso.action.ViewActions.click;
import static org.junit.Assert.assertTrue;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.NakshatraTechnoHub.HubSched.Ui.Dashboard.EmployeeListActivity;
import com.NakshatraTechnoHub.HubSched.Ui.StartActivity.LoginActivity;
import com.NakshatraTechnoHub.HubSched.UtilHelper.InputValidator;

import org.junit.Rule;
import org.junit.Test;

public class EmployeeListTest {

    @Rule
    public ActivityScenarioRule<EmployeeListActivity> activityRule = new ActivityScenarioRule<>(EmployeeListActivity.class);

    @Test
    public void testValidInput() {
        Espresso.onView(ViewMatchers.withId(R.id.topAppBar)).perform(click());
        Espresso.onView(ViewMatchers.withId(R.id.drawer_layout))
                .perform(DrawerActions.open());

        Espresso.onView(ViewMatchers.withId(R.id.employeeList)).perform(click());

        Espresso.onView(ViewMatchers.withId(R.id.edit_emp_btn)).perform(click());



    }



}
