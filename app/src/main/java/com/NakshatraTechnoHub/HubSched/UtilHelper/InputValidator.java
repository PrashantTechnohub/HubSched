package com.NakshatraTechnoHub.HubSched.UtilHelper;

public class InputValidator {
    public boolean validateInput(String empId, String name, String email, String mobile, String gender, String position, String userType, String password, String confirmPassword) {
        // Validate employee ID
        if (empId.isEmpty()) {
            return false;
        }

        // Validate name
        if (name.isEmpty()) {
            return false;
        }

        // Validate email format
        if (!isValidEmail(email)) {
            return false;
        }

        // Validate mobile number
        if (!isValidMobile(mobile)) {
            return false;
        }

        // Validate gender
        if (!isValidGender(gender)) {
            return false;
        }

        // Validate position
        if (position.isEmpty()) {
            return false;
        }

        // Validate user type
        if (userType.isEmpty()) {
            return false;
        }

        // Validate password length
        if (!isValidPasswordLength(password)) {
            return false;
        }

        // Validate password and confirm password match
        if (!password.equals(confirmPassword)) {
            return false;
        }

        return true;
    }

    private boolean isValidEmail(String email) {
        // Perform email validation logic here
        // You can use regular expressions or other methods to validate the email format
        // For simplicity, let's assume any non-empty string with an "@" symbol is considered valid
        return !email.isEmpty() && email.contains("@");
    }

    private boolean isValidMobile(String mobile) {
        // Perform mobile number validation logic here
        // You can use regular expressions or other methods to validate the mobile number format
        // For simplicity, let's assume any non-empty string with a length of 10 digits is considered valid
        return !mobile.isEmpty() && mobile.length() == 10 && mobile.matches("\\d+");
    }

    private boolean isValidGender(String gender) {
        // Perform gender validation logic here
        // You can define a list of valid genders and check if the provided gender is in the list
        // For simplicity, let's assume any non-empty string is considered valid
        return !gender.isEmpty();
    }

    private boolean isValidPasswordLength(String password) {
        // Validate password length
        int minLength = 6; // Minimum required password length
        return password.length() >= minLength;
    }
}
