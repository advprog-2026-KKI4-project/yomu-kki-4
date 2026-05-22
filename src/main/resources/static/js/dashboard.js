document.addEventListener('DOMContentLoaded', () => {
    const searchInput = document.getElementById('materialSearch');
    if (searchInput) {
        searchInput.addEventListener('input', function() {
            const query = this.value.toLowerCase();
            document.querySelectorAll('.module-item').forEach(item => {
                const title = item.querySelector('.module-title').innerText.toLowerCase();
                item.style.display = title.includes(query) ? 'flex' : 'none';
            });
        });
    }
});