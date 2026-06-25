@FXML
private void changePassword(ActionEvent event) {

    String currentPassword = passwordField.getText();
    String newPassword = newPasswordField.getText();

    if (currentPassword.isEmpty() || newPassword.isEmpty()) {
        showStatus("Both current and new password are required.", true);
        return;
    }

    if (newPassword.length() < 6) {
        showStatus("New password must be at least 6 characters.", true);
        return;
    }

    com.possystem.sajilopos.config.SessionManager session =
            com.possystem.sajilopos.config.SessionManager.getInstance();

    com.possystem.sajilopos.model.User currentUser =
            session.getCurrentUser();

    if (currentUser == null) {
        showStatus("No user logged in.", true);
        return;
    }

    boolean verified = org.mindrot.jbcrypt.BCrypt.checkpw(
            currentPassword,
            currentUser.getPasswordHash()
    );

    if (!verified) {
        showStatus("Current password is incorrect.", true);
        return;
    }

    String newHash = org.mindrot.jbcrypt.BCrypt.hashpw(
            newPassword,
            org.mindrot.jbcrypt.BCrypt.gensalt()
    );

    com.possystem.sajilopos.dao.UserDAO userDAO =
            new com.possystem.sajilopos.dao.UserDAO();

    boolean success =
            userDAO.changePassword(currentUser.getUserId(), newHash);

    if (success) {
        showStatus("Password changed successfully.", false);
        passwordField.clear();
        newPasswordField.clear();
    } else {
        showStatus("Failed to change password. Try again.", true);
    }
}