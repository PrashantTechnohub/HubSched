package com.NakshatraTechnoHub.HubSched;

import static androidx.test.espresso.action.ViewActions.click;

import static org.junit.Assert.assertTrue;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.NakshatraTechnoHub.HubSched.Ui.Dashboard.CreateEmployeeActivity;
import com.NakshatraTechnoHub.HubSched.Ui.StartActivity.LoginActivity;
import com.NakshatraTechnoHub.HubSched.UtilHelper.InputValidator;

import org.junit.Rule;
import org.junit.Test;

import java.util.Random;

public class CreateEmp {

    @Rule
    public ActivityScenarioRule<CreateEmployeeActivity> activityRule = new ActivityScenarioRule<>(CreateEmployeeActivity.class);



    @Test
    public void testValidInput() {
        Espresso.onView(ViewMatchers.withId(R.id.topAppBar)).perform(click());
        Espresso.onView(ViewMatchers.withId(R.id.drawer_layout))
                .perform(DrawerActions.open());

        Espresso.onView(ViewMatchers.withId(R.id.employeeList)).perform(click());

        Espresso.onView(ViewMatchers.withId(R.id.add_emp)).perform(click());

        InputValidator validator = new InputValidator();

        int employeeCount = 10;
        for (int i = 0; i < employeeCount; i++) {
            String empId = String.valueOf(i + 1);
            String name = generateRandomName();
            String email = generateRandomEmail();
            String mobile = "1234567890";
            String gender = "Male";
            String position = "Manager";
            String password = "password123";
            String confirmPassword = "password123";
            String userType = "Employee";

            Espresso.onView(ViewMatchers.withId(R.id.add_emp_btn)).perform(click());

            boolean isValid = validator.validateInput(empId, name, email, mobile, gender, position, userType, password, confirmPassword);
            assertTrue(isValid);
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

}
