package com.NakshatraTechnoHub.HubSched;

import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.NakshatraTechnoHub.HubSched.Ui.StartActivity.LoginActivity;
import com.NakshatraTechnoHub.HubSched.databinding.ActivityLoginBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class LoginTest {
    @Mock
    private TextInputEditText mockPasswordEditText, mockEmailEditText;
    @Mock
    private Button mockLoginButton;

    private LoginActivity loginActivity;

    @Rule
    public ActivityScenarioRule<LoginActivity> activityScenarioRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        // Get the activity instance from the scenario
        activityScenarioRule.getScenario().onActivity(loginActivity -> {
            // Set the mocked views in the LoginActivity using view binding
            loginActivity.bind = Mockito.mock(ActivityLoginBinding.class);
            Mockito.when(loginActivity.bind.emailId).thenReturn(mockEmailEditText);
            Mockito.when(loginActivity.bind.passwordId).thenReturn(mockPasswordEditText);
            Mockito.when(loginActivity.bind.loginBtn).thenReturn((MaterialButton) mockLoginButton);
        });
    }


    @Test
    public void testValidLoginCredentials() {
        // Set the text in the email and password EditTexts
        Mockito.when(mockEmailEditText.getText().toString()).thenReturn("admin@gmail.com");
        Mockito.when(mockPasswordEditText.getText().toString()).thenReturn("123");

        // Perform a click on the login button
        Mockito.doAnswer(invocation -> {
            // Simulate the login process
            loginActivity.loginUser("admin@gmail.com", "123", "mockedFirebaseToken");
            return null;
        }).when(mockLoginButton).performClick();

        // Verify that the login process is successful
        Mockito.verify(loginActivity).loginUser("admin@gmail.com", "123", "mockedFirebaseToken");
        // Add additional assertions as needed
    }

    @Test
    public void testEmptyEmail() {
        // Set an empty email and a valid password
        Mockito.when(mockEmailEditText.getText().toString()).thenReturn("");
        Mockito.when(mockPasswordEditText.getText().toString()).thenReturn("123");

        // Perform a click on the login button
        Mockito.doAnswer(invocation -> {
            // Simulate the login process
            loginActivity.loginUser("", "123", "mockedFirebaseToken");
            return null;
        }).when(mockLoginButton).performClick();

        // Verify that the appropriate error message is set on the email EditText
        Mockito.verify(mockEmailEditText).setError("Empty");
        // Add additional assertions as needed
    }

    @Test
    public void testEmptyPassword() {
        // Set a valid email and an empty password
        Mockito.when(mockEmailEditText.getText().toString()).thenReturn("admin@gmail.com");
        Mockito.when(mockPasswordEditText.getText().toString()).thenReturn("");

        // Perform a click on the login button
        Mockito.doAnswer(invocation -> {
            // Simulate the login process
            loginActivity.loginUser("admin@gmail.com", "", "mockedFirebaseToken");
            return null;
        }).when(mockLoginButton).performClick();

        // Verify that the appropriate error message is set on the password EditText
        Mockito.verify(mockPasswordEditText).setError("Empty");
        // Add additional assertions as needed
    }
}
