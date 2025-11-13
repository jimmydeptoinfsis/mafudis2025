// js/adm-scripts.js

// --- INICIO CÓDIGO A EJECUTAR DESPUÉS DE CARGAR EL HTML ---

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
        // Se quita la clase 'active' de todos los enlaces al cargar la página
        link.classList.remove('active');
        if (link.getAttribute('href') === currentPath) {
            link.classList.add('active');
        }
    });

    // 3. CÓDIGO QUE PROBABLEMENTE ESTÁ CAUSANDO EL ERROR
    // Busca en tu archivo js/adm-scripts.js original si tienes un código como este.
    // Si lo tienes, asegúrate de que esté DENTRO de este bloque 'DOMContentLoaded'.
    /*
    const menuToggle = document.getElementById('menu-toggle'); // O el ID que sea
    if (menuToggle) {
        menuToggle.addEventListener('click', function() {
            // Lógica para el menú...
        });
    }
    */

});

// --- FIN CÓDIGO A EJECUTAR DESPUÉS DE CARGAR EL HTML ---


// --- FUNCIONES GLOBALES ---
// Esta función DEBE estar fuera del 'DOMContentLoaded' para ser accesible globalmente por 'onclick'.

/**
 * Carga contenido dinámicamente en el área principal y actualiza la miga de pan.
 * @param {string} url La URL desde donde se cargará el fragmento de HTML.
 * @param {string} breadcrumbTitle El nuevo título para la miga de pan.
 */
function loadContent(url, breadcrumbTitle) {
    const mainContentArea = document.getElementById('main-content-area');
    const breadcrumbCurrentPage = document.querySelector('.breadcrumb-item.active');

    if (!mainContentArea) {
        console.error('Error: El área de contenido principal #main-content-area no fue encontrada.');
        return;
    }

    mainContentArea.innerHTML = '<div class="d-flex justify-content-center p-5"><div class="spinner-border" role="status"><span class="visually-hidden">Cargando...</span></div></div>';

    fetch(url)
        .then(response => {
            if (!response.ok) {
                throw new Error('Error en la respuesta del servidor: ' + response.statusText);
            }
            return response.text();
        })
        .then(html => {
            mainContentArea.innerHTML = html;
            if (breadcrumbCurrentPage) {
                breadcrumbCurrentPage.textContent = breadcrumbTitle;
            }

            const scripts = mainContentArea.getElementsByTagName('script');
            for (let i = 0; i < scripts.length; i++) {
                const oldScript = scripts[i];
                const newScript = document.createElement('script');
                Array.from(oldScript.attributes).forEach(attr => newScript.setAttribute(attr.name, attr.value));
                newScript.appendChild(document.createTextNode(oldScript.innerHTML));
                oldScript.parentNode.replaceChild(newScript, oldScript);
            }
        })
        .catch(error => {
            console.error('Error al cargar el contenido:', error);
            mainContentArea.innerHTML = '<div class="alert alert-danger">Ocurrió un error al cargar el contenido. Por favor, intente de nuevo.</div>';
        });
}