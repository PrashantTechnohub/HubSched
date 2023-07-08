package com.NakshatraTechnoHub.HubSched;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.NakshatraTechnoHub.HubSched.Ui.Dashboard.CreateEmployeeActivity;
import com.NakshatraTechnoHub.HubSched.Ui.StartActivity.LoginActivity;
import com.NakshatraTechnoHub.HubSched.UtilHelper.InputValidator;

import org.junit.Rule;
import org.junit.Test;

import java.util.Random;

public class FullAutomation {
    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule = new ActivityScenarioRule<>(LoginActivity.class);
    public ActivityScenarioRule<CreateEmployeeActivity> activityRuleCreateEmployee = new ActivityScenarioRule<>(CreateEmployeeActivity.class);



    @Test
    public void test04_LoginWithValidCred() {


        ViewInteraction emailField = Espresso.onView(withId(R.id.email_id));
        ViewInteraction passwordField = Espresso.onView(withId(R.id.password_id));
        ViewInteraction loginButton = Espresso.onView(withId(R.id.login_btn));

        Context context = ApplicationProvider.getApplicationContext();

        SharedPreferences sharedPreferences = context.getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("type", "");

        if (!username.isEmpty()) {
            logoutCheck();
        } else {
            emailField.perform(typeText("admin@gmail.com"));
            passwordField.perform(typeText("123456"));
            loginButton.perform(click());
            testValidInput();

        }



    }


    public void testValidInput() {
        try {
            Thread.sleep(4000);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Espresso.onView(ViewMatchers.withId(R.id.topAppBar)).perform(click());
        Espresso.onView(ViewMatchers.withId(R.id.drawer_layout))
                .perform(DrawerActions.open());

        Espresso.onView(ViewMatchers.withId(R.id.employeeList)).perform(click());

        Espresso.onView(ViewMatchers.withId(R.id.add_emp)).perform(click());


        int employeeCount = 10;

        for (int i = 0; i < employeeCount; i++) {
            String empId = String.valueOf(i + 1);
            String name = generateRandomName();
            String email = generateRandomEmail();
            Espresso.onView(withId(R.id.add_emp_id)).perform(typeText(empId));
            Espresso.onView(withId(R.id.add_emp_name)).perform(typeText(name));
            Espresso.onView(withId(R.id.add_emp_email)).perform(typeText(email));
            Espresso.onView(withId(R.id.add_emp_mobile)).perform(typeText("8889818200"));
            Espresso.onView(ViewMatchers.withId(R.id.add_emp_gender)).perform(click());
            Espresso.onView(ViewMatchers.withId(R.id.user_type)).perform(click());
            Espresso.onView(ViewMatchers.withId(R.id.add_emp_position)).perform(click());
            Espresso.onView(withId(R.id.add_emp_mobile)).perform(typeText("8889818200"));
            Espresso.onView(withId(R.id.add_emp_password)).perform(typeText("123456"));
            Espresso.onView(withId(R.id.add_emp_c_password)).perform(typeText("123456"));


            if (isViewVisible(R.id.add_emp_btn)) {
                Espresso.onView(withId(R.id.add_emp_btn)).perform(click());
            } else {
                // Close the keyboard
                Espresso.closeSoftKeyboard();

                // Scroll down
                Espresso.onView(withId(R.id.parent_scroll_view)).perform(swipeUp());

                // Try to find the submit button again
                if (isViewVisible(R.id.add_emp_btn)) {
                    Espresso.onView(withId(R.id.add_emp_btn)).perform(click());
                } else {
                    // Handle the case when the submit button is still not found
                    // Add the necessary code here
                }
            }

        }
    }
    private String generateRandomName() {
        // Generate a random name using a combination of letters
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder randomName = new StringBuilder();
        Random random = new Random();
        int nameLength = random.nextInt(5) + 5; // Random name length between 5 and 10 characters
        for (int i = 0; i < nameLength; i++) {
            int randomIndex = random.nextInt(alphabet.length());
            char randomChar = alphabet.charAt(randomIndex);
            randomName.append(randomChar);
        }
        return randomName.toString();
    }
    private boolean isViewVisible(int viewId) {
        ViewInteraction interaction = Espresso.onView(withId(viewId));
        try {
            interaction.check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
            return true;
        } catch (NoMatchingViewException e) {
            return false;
        }
    }
    private String generateRandomEmail() {
        // Generate a random email using a combination of letters and numbers
        String alphabet = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder randomEmail = new StringBuilder();
        Random random = new Random();
        int emailLength = random.nextInt(5) + 5; // Random email length between 5 and 10 characters
        for (int i = 0; i < emailLength; i++) {
            int randomIndex = random.nextInt(alphabet.length());
            char randomChar = alphabet.charAt(randomIndex);
            randomEmail.append(randomChar);
        }
        randomEmail.append("@example.com");
        return randomEmail.toString();
    }
    public void logoutCheck() {
        try {
            Thread.sleep(2000);
            Espresso.onView(withId(R.id.account)).perform(ViewActions.click());

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        try {
            Thread.sleep(2000);

            Espresso.onView(withId(R.id.log_out_btn))
                    .perform(ViewActions.click());


            Thread.sleep(1000);

            Espresso.onView(withId(R.id.yes_btn))
                    .perform(ViewActions.click());


        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


}
