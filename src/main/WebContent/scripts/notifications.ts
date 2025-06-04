(function () {
    const notificationContainer = document.getElementById('notification-container');

    function createNotificationElement(message: string, type: string) {
        const notification = document.createElement('div');
        notification.classList.add('notification', type);
        notification.innerHTML = `
            <span>${message}</span>
            <span class="notification-close">&times;</span>
        `;
        return notification;
    }

    function showNotification(message: string, type: string = 'info', duration: number = 5000) {
        const notification = createNotificationElement(message, type);
        notificationContainer.prepend(notification);

        requestAnimationFrame(() => {
            notification.classList.add('show');
        });

        if (duration > 0) {
            console.log(duration);
            setTimeout(() => {
                hideNotification(notification);
            }, duration);
        }

        notification.querySelector('.notification-close').addEventListener('click', () => {
            hideNotification(notification);
        });
    }

    function hideNotification(notificationElement: HTMLDivElement) {
        notificationElement.classList.remove('show');
        notificationElement.addEventListener('transitionend', () => {
            notificationElement.remove();
        }, {once: true});
    }

    window["notify"] = {
        success: (message, duration) => showNotification(message, 'success', duration),
        error: (message, duration) => showNotification(message, 'error', duration),
        info: (message, duration) => showNotification(message, 'info', duration),
        warning: (message, duration) => showNotification(message, 'warning', duration),
    };
})()