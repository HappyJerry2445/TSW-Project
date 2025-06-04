(function () {
    var notificationContainer = document.getElementById('notification-container');
    function createNotificationElement(message, type) {
        var notification = document.createElement('div');
        notification.classList.add('notification', type);
        notification.innerHTML = "\n            <span>".concat(message, "</span>\n            <span class=\"notification-close\">&times;</span>\n        ");
        return notification;
    }
    function showNotification(message, type, duration) {
        if (type === void 0) { type = 'info'; }
        if (duration === void 0) { duration = 5000; }
        var notification = createNotificationElement(message, type);
        notificationContainer.prepend(notification);
        requestAnimationFrame(function () {
            notification.classList.add('show');
        });
        if (duration > 0) {
            console.log(duration);
            setTimeout(function () {
                hideNotification(notification);
            }, duration);
        }
        notification.querySelector('.notification-close').addEventListener('click', function () {
            hideNotification(notification);
        });
    }
    function hideNotification(notificationElement) {
        notificationElement.classList.remove('show');
        notificationElement.addEventListener('transitionend', function () {
            notificationElement.remove();
        }, { once: true });
    }
    window["notify"] = {
        success: function (message, duration) { return showNotification(message, 'success', duration); },
        error: function (message, duration) { return showNotification(message, 'error', duration); },
        info: function (message, duration) { return showNotification(message, 'info', duration); },
        warning: function (message, duration) { return showNotification(message, 'warning', duration); },
    };
})();
