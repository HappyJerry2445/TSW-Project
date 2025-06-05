package com.cardhaven.cardhaven.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Utility class for managing session-based notifications.
 */
public class NotificationUtil {

    private static final String NOTIFICATION_MESSAGE_ATTR = "notificationMessage";
    private static final String NOTIFICATION_TYPE_ATTR = "notificationType";

    /**
     * Sends a one-time notification message to the user via the session.
     * The message will typically be displayed on the next page load and then cleared.
     *
     * @param request The HttpServletRequest, used to get the HttpSession.
     * @param message The notification message to display.
     * @param type    The type of notification (e.g., "success", "info", "warning", "danger").
     */
    public static void sendNotification(HttpServletRequest request, String message, String type) {
        HttpSession session = request.getSession();
        session.setAttribute(NOTIFICATION_MESSAGE_ATTR, message);
        session.setAttribute(NOTIFICATION_TYPE_ATTR, type);
    }

    /**
     * Retrieves and clears any pending notification from the session.
     * This method is typically called by a JSP or filter after displaying the message.
     *
     * @param session The HttpSession to check for notifications.
     * @return A Notification object containing the message and type, or null if no notification is present.
     */
    public static Notification getAndClearNotification(HttpSession session) {
        String message = (String) session.getAttribute(NOTIFICATION_MESSAGE_ATTR);
        String type = (String) session.getAttribute(NOTIFICATION_TYPE_ATTR);

        if (message != null && type != null) {
            session.removeAttribute(NOTIFICATION_MESSAGE_ATTR);
            session.removeAttribute(NOTIFICATION_TYPE_ATTR);
            return new Notification(message, type);
        }
        return null;
    }

    /**
     * Simple DTO for holding notification details.
     */
    public static class Notification {
        private final String message;
        private final String type;

        public Notification(String message, String type) {
            this.message = message;
            this.type = type;
        }

        public String getMessage() {
            return message;
        }

        public String getType() {
            return type;
        }
    }
}
