// nav.js - Interatividade e marcação de página ativa
document.addEventListener("DOMContentLoaded", () => {
  const currentPath = window.location.pathname.split("/").pop() || "index.html";
  
  // Marca página ativa no menu
  const navLinks = document.querySelectorAll("nav.reversa-doc-nav a");
  navLinks.forEach(link => {
    const href = link.getAttribute("href");
    if (href === currentPath || (currentPath === "" && href === "index.html")) {
      link.classList.add("active");
      link.setAttribute("aria-current", "page");
    } else {
      link.classList.remove("active");
      link.removeAttribute("aria-current");
    }
  });

  // Atalhos de teclado (ex: [ ] ou setas para navegar entre páginas)
  window.addEventListener("keydown", (e) => {
    if (e.target.tagName === "INPUT" || e.target.tagName === "SELECT" || e.target.tagName === "TEXTAREA") {
      return;
    }
    // R para resetar câmera/visualização
    if (e.key === "r" || e.key === "R") {
      if (typeof window.resetCamera === "function") {
        window.resetCamera();
      }
    }
  });
});
