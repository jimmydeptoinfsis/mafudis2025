document.addEventListener('DOMContentLoaded', function() {
    // 1. CÓDIGO PARA ACTIVAR TOOLTIPS
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // 2. CÓDIGO PARA MARCAR ENLACE ACTIVO
    const currentPath = window.location.pathname;
    const navLinks = document.querySelectorAll('.nav-link');
    navLinks.forEach(link => {
        link.classList.remove('active');
        if (link.getAttribute('href') === currentPath) {
            link.classList.add('active');
        }
    });

    // 3. LÓGICA DEL SIDEBAR PARA MARCAR EL LINK ACTIVO
    const sidebarNavLinks = document.querySelectorAll('.sidebar .nav-link');
    sidebarNavLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            sidebarNavLinks.forEach(l => l.classList.remove('active'));
            this.classList.add('active');

            // Si el enlace tiene onclick, no hacemos preventDefault
            if (!this.getAttribute('onclick')) {
                return true;
            }
        });
    });
});