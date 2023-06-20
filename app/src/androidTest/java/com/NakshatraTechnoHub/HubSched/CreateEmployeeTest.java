package com.NakshatraTechnoHub.HubSched;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.containsString;

import androidx.core.view.GravityCompat;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.DrawerMatchers;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.espresso.matcher.ViewMatchers;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.NakshatraTechnoHub.HubSched.Ui.StartActivity.LoginActivity;
import com.NakshatraTechnoHub.HubSched.Ui.Dashboard.DashboardActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import androidx.appcompat.widget.Toolbar;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;

import java.io.IOException;

public class CreateEmployeeTest {
    private SharedPreferences sharedPreferences;

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule = new ActivityScenarioRule<>(LoginActivity.class);


    @Before
    public void setUp() {
        // Disable animations
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            try {
                UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).executeShellCommand(
                        "settings put global transition_animation_scale 0");
                UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).executeShellCommand(
                        "settings put global window_animation_scale 0");
                UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).executeShellCommand(
                        "settings put global animator_duration_scale 0");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } else {
            // For older Android versions
            System.setProperty("com.android.uiautomator.disableAnimation", "true");
        }

        // Perform login before starting DashboardActivity
        onView(ViewMatchers.withId(R.id.email_id)).perform(typeText("admin@gmail.com"));
        onView(ViewMatchers.withId(R.id.password_id)).perform(typeText("123456"));
        onView(ViewMatchers.withId(R.id.login_btn)).perform(click());

        try {
            Thread.sleep(2000);
            // Open the navigation drawer
            Espresso.onView(ViewMatchers.withId(R.id.topAppBar)).perform(click());
            Espresso.onView(ViewMatchers.withId(R.id.drawer_layout))
                    .perform(DrawerActions.open());

            Espresso.onView(ViewMatchers.withId(R.id.employeeList)).perform(click());

            Espresso.onView(ViewMatchers.withId(R.id.add_emp)).perform(click());

            onView(ViewMatchers.withId(R.id.add_emp_id)).perform(typeText("121"));
            onView(ViewMatchers.withId(R.id.add_emp_name)).perform(typeText("Sumit Deshmukh"));
            onView(ViewMatchers.withId(R.id.add_emp_email)).perform(typeText("Sumit@gmai.com"));
//            onView(ViewMatchers.withId(R.id.add_emp_mobile)).perform(typeText(""));
            onView(withId(R.id.add_emp_mobile)).perform(scrollTo(), typeText("888988921"));
            onView(withId(R.id.add_emp_password)).perform(scrollTo(), typeText("12345655"));
            onView(withId(R.id.add_emp_c_password)).perform(scrollTo(), typeText("12345655"));

//            Espresso.onView(ViewMatchers.withId(R.id.add_emp_gender)).perform(click());
//
//            int selectedIndex = 0; // Index of the option you want to select (starting from 0)
//            onData(anything())
//                    .inAdapterView(withClassName(containsString("Male")))
//                    .atPosition(selectedIndex)
//                    .perform(click());
//
//            Espresso.onView(ViewMatchers.withId(R.id.user_type)).perform(click());
//            Espresso.onView(ViewMatchers.withId(R.id.user_position)).perform(click());
            onView(ViewMatchers.withId(R.id.add_emp_password)).perform(typeText("1232311"));
            onView(ViewMatchers.withId(R.id.add_emp_c_password)).perform(typeText("1232311"));

            Espresso.onView(ViewMatchers.withId(R.id.add_emp_btn)).perform(click());


        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @After
    public void tearDown() {
        Context context = ApplicationProvider.getApplicationContext();
        sharedPreferences = context.getSharedPreferences("userDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Enable animations back to their original values
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            try {
                UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).executeShellCommand(
                        "settings put global transition_animation_scale 1");

                UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).executeShellCommand(
                        "settings put global window_animation_scale 1");
                UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).executeShellCommand(
                        "settings put global animator_duration_scale 1");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } else {
            // For older Android versions
            System.clearProperty("com.android.uiautomator.disableAnimation");
        }
    }

    @Test
    public void testDashboardActivity() {
        // Open the DashboardActivity after login
        // Add your test code for DashboardActivity here
    }
}
