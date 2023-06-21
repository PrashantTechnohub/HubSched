package com.NakshatraTechnoHub.HubSched;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.NakshatraTechnoHub.HubSched.Ui.StartActivity.LoginActivity;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.JVM)

public class LoginTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule = new ActivityScenarioRule<>(LoginActivity.class);


    @Test
    public void test01_invalidEmail() {
        // Perform login with invalid credentials
        Espresso.onView(withId(R.id.email_id)).perform(ViewActions.typeText("invalid@gmail.com"));
        Espresso.onView(withId(R.id.password_id)).perform(ViewActions.typeText("1232332"));
        Espresso.onView(withId(R.id.login_btn));
    }

    @Test
    public void test02_passwordAbove6Digit() {
        // Perform login with invalid credentials
        Espresso.onView(withId(R.id.email_id)).perform(ViewActions.typeText("invalid@gmail.com"));
        Espresso.onView(withId(R.id.password_id)).perform(ViewActions.typeText("1232332"));
        Espresso.onView(withId(R.id.login_btn));
    }

    @Test
    public void test03_passwordBelow6Digit() {
        // Perform login with invalid credentials
        Espresso.onView(withId(R.id.email_id)).perform(ViewActions.typeText("invalid@gmail.com"));
        Espresso.onView(withId(R.id.password_id)).perform(ViewActions.typeText("1332"));
        Espresso.onView(withId(R.id.login_btn));
    }


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

        }



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
