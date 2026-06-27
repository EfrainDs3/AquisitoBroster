# Guía de Instalación y Ejecución del Prototipo en React

Este proyecto ha sido re-estructurado como una aplicación **React** moderna empaquetada con **Vite**. 
Dado que `npx` y `npm` no están instalados de forma global en tu máquina actualmente, debes instalar **Node.js** primero. Sigue estos pasos sencillos para poner el prototipo en marcha:

---

## Paso 1: Instalar Node.js y npm

1.  **Descargar el instalador**:
    *   Ve al sitio oficial de Node.js: [https://nodejs.org/](https://nodejs.org/).
    *   Descarga la versión recomendada **LTS** (Long Term Support) para Windows.
2.  **Ejecutar la instalación**:
    *   Abre el instalador descargado (`.msi`).
    *   Sigue los pasos haciendo clic en "Next". Asegúrate de que la opción **"Add to PATH"** esté habilitada (se habilita por defecto).
    *   Finaliza la instalación.
3.  **Verificar la instalación**:
    *   Abre una nueva terminal (PowerShell o CMD) y escribe:
        ```bash
        node -v
        npm -v
        ```
    *   Deberían retornar números de versión (por ejemplo, `v20.x.x` y `10.x.x`).

---

## Paso 2: Descargar Dependencias del Proyecto

Una vez que tengas Node.js listo, abre la terminal en la carpeta de este proyecto (`c:\Users\santi\Documents\ING\OCTAVO-CICLO\REQUERIMIENTOS\broster\frontend`) y ejecuta:

```bash
npm install
```

Este comando leerá el archivo [package.json](file:///c:/Users/santi/Documents/ING/OCTAVO-CICLO/REQUERIMIENTOS/broster/frontend/package.json) que hemos creado e instalará automáticamente:
*   **React y React DOM** (para la lógica web).
*   **Vite** (como servidor de desarrollo ultrarrápido).
*   **Lucide React** (para la iconografía moderna e interactiva).
*   **Chart.js y React-Chartjs-2** (para los gráficos reactivos del Dashboard).

---

## Paso 3: Lanzar el Servidor de Desarrollo

Para ver el prototipo interactivo en tu navegador, ejecuta el siguiente comando en la misma carpeta:

```bash
npm run dev
```

En la consola aparecerá un enlace similar a este:
`  ➜  Local:   http://localhost:5173/`

*   **Abre ese enlace en tu navegador** y podrás interactuar con toda la UI reactiva de **Aquicito Broaster**.

---

## Estructura de Archivos del Frontend Creado

*   [package.json](file:///c:/Users/santi/Documents/ING/OCTAVO-CICLO/REQUERIMIENTOS/broster/frontend/package.json) - Define dependencias del proyecto.
*   [vite.config.js](file:///c:/Users/santi/Documents/ING/OCTAVO-CICLO/REQUERIMIENTOS/broster/frontend/vite.config.js) - Archivo de configuración del empaquetador Vite.
*   [index.html](file:///c:/Users/santi/Documents/ING/OCTAVO-CICLO/REQUERIMIENTOS/broster/frontend/index.html) - Punto de anclaje de la SPA de React.
*   [src/main.jsx](file:///c:/Users/santi/Documents/ING/OCTAVO-CICLO/REQUERIMIENTOS/broster/frontend/src/main.jsx) - Archivo de inicialización de React.
*   [src/index.css](file:///c:/Users/santi/Documents/ING/OCTAVO-CICLO/REQUERIMIENTOS/broster/frontend/src/index.css) - Hoja de estilos globales (colores premium, layout y scrollbars).
*   [src/App.jsx](file:///c:/Users/santi/Documents/ING/OCTAVO-CICLO/REQUERIMIENTOS/broster/frontend/src/App.jsx) - Componente React que administra el estado reactivo persistente (POS, Cocina, Caja, Inventario y Reportes).
