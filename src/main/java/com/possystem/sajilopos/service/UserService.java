public void requireAdmin() {
    if (!SessionManager.getInstance().isAdmin()) {
        throw new IllegalStateException("Access denied. ADMIN role required.");
    }
}

public void requireManagerOrAbove() {
    SessionManager sm = SessionManager.getInstance();
    if (!sm.isAdmin() && !sm.isManager()) {
        throw new IllegalStateException("Access denied. MANAGER or ADMIN role required.");
    }
}

public void requireLogin() {
    if (!SessionManager.getInstance().isLoggedIn()) {
        throw new IllegalStateException("Access denied. Please login first.");
    }
}
