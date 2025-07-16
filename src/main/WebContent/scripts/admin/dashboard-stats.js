document.addEventListener('DOMContentLoaded', function () {
    const statsCards = {
        ordersCount: document.querySelectorAll('.stats-count')[0],
        usersCount: document.querySelectorAll('.stats-count')[1],
        productsCount: document.querySelectorAll('.stats-count')[2],
        reviewsCount: document.querySelectorAll('.stats-count')[3]
    };

    async function fetchDashboardStats() {
        try {
            const response = await fetch(`${APP_CONTEXT_PATH}/admin/stats`); // Assuming /admin/stats endpoint
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const stats = await response.json();

            // Update the UI with fetched data
            if (stats.ordersCount !== undefined) {
                statsCards.ordersCount.textContent = stats.ordersCount;
            }
            if (stats.usersCount !== undefined) {
                statsCards.usersCount.textContent = stats.usersCount;
            }
            if (stats.productsCount !== undefined) {
                statsCards.productsCount.textContent = stats.productsCount;
            }
            if (stats.reviewsCount !== undefined) {
                statsCards.reviewsCount.textContent = stats.reviewsCount;
            }

        } catch (error) {
            console.error('Errore nel recupero delle statistiche della dashboard:', error);
            // Fallback to default or show an error state if fetching fails
            Object.values(statsCards).forEach(card => card.textContent = 'N/A');
        }
    }

    fetchDashboardStats().then(() => {
        const refreshInterval = 10000;
        setInterval(fetchDashboardStats, refreshInterval);
    });

});
