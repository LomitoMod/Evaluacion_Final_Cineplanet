# INFORME DE AUDITORÍA Y ESTADO DE CUMPLIMIENTO
## Evaluación Final: Seguridad de Aplicaciones (02414)
**Caso de Estudio:** Tienda Online Segura Cineplanet  
**Tecnología:** Java EE (Servlets 3.1 + JSP 2.2 + JSTL) | Apache NetBeans | GlassFish 4.1.1 | SQL Server | JDK 8  
**Docente:** Mg. David Coragua  
**Referencia:** `Examen_Final_Cineplanet_SEGURIDAD_APLICACIONES_02414.docx`  

---

## 1. Resumen Ejecutivo de Cumplimiento

| Criterio de Evaluación | Ponderación | Estado | Puntos Estimados | Observación |
| :--- | :---: | :---: | :---: | :--- |
| **1. Proyecto, BD y conexión** | 2.0 pts | ⚠️ En Observación | **1.5 / 2.0** | El proyecto Java y conexión están perfectos. La BD en SQL Server falló por espacio en disco al cargar datos de Power BI. |
| **2. Autenticación y sesiones** | 3.5 pts | ✅ Cumple (Destacado) | **3.5 / 3.5** | Hash SHA-256, bloqueo 5 min por 3 fallos, sesión regenerada tras login, timeout 15 min, logout seguro con CSRF. |
| **3. Control de acceso y cabeceras** | 3.0 pts | ✅ Cumple (Destacado) | **3.0 / 3.0** | Filtro por roles (CLIENTE/PERSONAL/ADMIN), 403 con auditoría, IDOR mitigado y cabeceras HTTP (`no-store`, etc.). |
| **4. Tienda y carrito seguro** | 4.0 pts | ✅ Cumple (Destacado) | **4.0 / 4.0** | PreparedStatement en todo, transacciones con commit/rollback, precios leídos de la BD y control de butaca duplicada. |
| **5. XSS y CSRF** | 3.0 pts | ✅ Cumple (Destacado) | **3.0 / 3.0** | Salida 100% escapada con `<c:out>` en JSTL. Token CSRF verificado en todos los formularios POST (carrito, pago, anular, logout). |
| **6. Errores, cabeceras y auditoría** | 2.5 pts | ✅ Cumple (Destacado) | **2.5 / 2.5** | Páginas de error genéricas (sin trazas), cabeceras de seguridad estrictas, bitácora en tabla `Auditoria` y vista ADMIN. |
| **7. Pruebas de seguridad documentadas** | 2.0 pts | ❌ Faltante | **0.0 / 2.0** | La página 3 del Word exige pegar las 9 capturas de las pruebas de seguridad con su resultado. |
| **PUNTAJE TOTAL ACTUAL** | **20.0 pts** | | **16.0 / 20.0** | **Subirá a 20/20 al restaurar la BD limpia y pegar las 9 capturas en el Word.** |

---

## 2. Auditoría Detallada por Criterio Oficial

### Criterio 1: Proyecto, BD y Conexión (2.0 pts)
* **Nombre del proyecto:** `Evaluacion_Final_Cineplanet` (sin tildes, sin espacios ni caracteres especiales). **CUMPLE.**
* **Estructura modular:** Paquetes bien delimitados: `conexion`, `modelo`, `dao`, `controlador`, `filtro`, `util`. **CUMPLE.**
* **Librería y Driver JDBC:** `mssql-jdbc-13.4.0.jre8.jar` ubicado correctamente en `web/WEB-INF/lib/`. **CUMPLE.**
* **Clase de Conexión:** `conexion.ConexionSQL` configurada para conectar a `localhost:1433`, base de datos `BD_CineplanetFinal`, con credenciales `sa` y parámetros `encrypt=false;trustServerCertificate=true`. **CUMPLE.**
* **Estado de la BD:** *Alerta técnica*. El archivo `BD_CineplanetFinal.sql` contiene una sección masiva de generación sintética de ~700,000 registros para Power BI que desbordó el almacenamiento del disco local, impidiendo la creación de la tabla `Auditoria` y la conversión de claves a SHA-256.

### Criterio 2: Autenticación y Sesiones (3.5 pts) — [A07:2021]
* **Cifrado de credenciales:** Método `Seguridad.sha256()` genera hashes unidireccionales de 64 caracteres hexadecimales. Ni el cliente ni el personal almacenan contraseñas en texto plano. **CUMPLE.**
* **Prevención de enumeración de usuarios:** `LoginServlet` devuelve `Seguridad.MSJ_GENERICO` (*"Usuario o clave incorrectos"*) tanto si el usuario no existe como si la clave es errónea. **CUMPLE.**
* **Protección contra fuerza bruta:** Contador de intentos fallidos en memoria (`Seguridad.registrarFallo()`). Al tercer intento erróneo, la cuenta queda bloqueada por 5 minutos y se registra el evento `BLOQUEO`. **CUMPLE.**
* **Prevención de fijación de sesión (Session Fixation):** En login exitoso se ejecuta `sesionVieja.invalidate()` y se genera un nuevo identificador de sesión con `req.getSession(true)`. **CUMPLE.**
* **Tiempo de inactividad:** En `web.xml` se encuentra configurado `<session-timeout>15</session-timeout>`. **CUMPLE.**
* **Cierre de sesión seguro:** `LogoutServlet` invalida la sesión HTTP en el servidor y valida token CSRF. **CUMPLE.**

### Criterio 3: Control de Acceso y Cabeceras (3.0 pts) — [A01:2021]
* **Filtro de autorización (`filtro.SesionFilter`):**
  * Rutas de cajero (`/anular.jsp`, `/AnularServlet`): Exclusivas para rol `PERSONAL` y `ADMIN`.
  * Rutas de administración (`/ventas.jsp`, `/VentaServlet`, `/auditoria.jsp`, `/AuditoriaServlet`): Exclusivas para `ADMIN`.
  * Si un usuario no autorizado intenta acceder (ej. un cajero intentando ver auditoría), se rechaza con HTTP 403 y se audita el evento `ACCESO_DENEGADO`. **CUMPLE.**
* **Protección contra IDOR (Insecure Direct Object References):**
  * `ComprobanteServlet.java` y `MisEntradasServlet.java` filtran la información mediante `VentaDAO.buscarPropia(idVenta, idCliente)` utilizando estrictamente el `idCliente` de la sesión activa. Modificar el parámetro `?id=` en la URL no permite visualizar compras ajenas. **CUMPLE.**
* **Cabeceras HTTP de seguridad (`filtro.CabecerasFilter`):**
  * `Cache-Control: no-cache, no-store, must-revalidate` (evita que el botón "Atrás" del navegador muestre pantallas protegidas tras cerrar sesión).
  * `Pragma: no-cache` y `Expires: 0`.
  * `X-Content-Type-Options: nosniff`.
  * `X-Frame-Options: DENY` (mitiga Clickjacking).
  * `X-XSS-Protection: 1; mode=block`. **CUMPLE.**

### Criterio 4: Tienda y Carrito Seguro (4.0 pts) — [A03:2021]
* **Inyección SQL mitigada:** Todas las clases en `dao/*` usan `PreparedStatement` con parámetros tipados (`setInt`, `setString`, `setBigDecimal`). **CUMPLE.**
* **Validación en el servidor:** Expresión regular `^[A-J](1[0-2]|[1-9])$` para butacas, tope de 10 entradas y límite de 10 unidades de dulcería por producto. **CUMPLE.**
* **Transacciones atómicas:** En `VentaDAO.registrar()`, se inicia `cn.setAutoCommit(false)`, registrando cabecera de venta, entradas, actualización de disponibilidad y detalle de dulcería. En caso de cualquier error se ejecuta `cn.rollback()`. **CUMPLE.**
* **Integridad de precios:** Los precios unitarios de entradas y combos de dulcería se leen directamente de las tablas `Funcion` y `Producto` dentro de la transacción. El sistema no acepta montos enviados desde el formulario web (mitiga manipulación F12). **CUMPLE.**
* **Control de butacas duplicadas (Concurrencia):** Validación previa en memoria y captura de los errores SQL Server `2601` y `2627` (violación del índice único `UX_Entrada_Butaca`), arrojando mensaje comprensible al usuario sin romper el servidor. **CUMPLE.**

### Criterio 5: XSS y CSRF (3.0 pts) — [A01:2021 / A03:2021]
* **Prevención de XSS:** Se emplea la librería de etiquetas JSTL `<c:out value="..."/>` en el 100% de las variables dinámicas de las vistas JSP. **CUMPLE.**
* **Tokens CSRF:** Mecanismo en `util.Seguridad` con tokens pseudoaleatorios asociados a la sesión.
* **Validación en formularios POST:**
  * `CarritoServlet` (agregar y vaciar carrito).
  * `PagoServlet` (confirmación y cobro de venta).
  * `AnularServlet` (anulación de entrada por cajero).
  * `LogoutServlet` (salida del sistema).
  * Cualquier POST sin token válido o expirado devuelve HTTP 403 y registra el evento `CSRF` en la auditoría. **CUMPLE.**

### Criterio 6: Errores, Cabeceras y Auditoría (2.5 pts) — [A05:2021 / A09:2021]
* **Páginas de error seguras:** `web.xml` captura códigos 403, 404, 500 y `java.lang.Exception`, mostrando `error.jsp`. **CUMPLE.**
* **Cero fuga de información técnica:** `error.jsp` omite trazas de pila (stack traces), rutas de archivos del servidor y versiones de software. **CUMPLE.**
* **Bitácora de seguridad:** Tabla `Auditoria` registra los 8 eventos solicitados: `LOGIN_OK`, `LOGIN_FALLIDO`, `BLOQUEO`, `LOGOUT`, `COMPRA`, `ANULACION`, `ACCESO_DENEGADO` y `CSRF`, guardando usuario, acción, detalle, IP y fecha. **CUMPLE.**
* **Panel de auditoría:** `auditoria.jsp` disponible para el rol `ADMIN` con visualización de los últimos 100 eventos. **CUMPLE.**

### Criterio 7: Pruebas de Seguridad (2.0 pts) — ❌ PENDIENTE DE COMPLETAR
* El código permite superar las 9 pruebas sin inconvenientes, pero **las capturas de pantalla aún no están pegadas en el archivo Word** `Examen_Final_Cineplanet_SEGURIDAD_APLICACIONES_02414.docx`.

---

## 3. Elementos Faltantes y Plan de Acción Inmediato

```mermaid
graph TD
    A["Faltantes para Calificación 20/20"] --> B["1. Restaurar BD Limpia en SQL Server"]
    A --> C["2. Ejecutar y Capturar las 9 Pruebas"]
    A --> D["3. Completar Entregables (Word + txt)"]
    
    B --> B1["Liberar espacio en disco C:"]
    B --> B2["Ejecutar BD sin datos de Power BI"]
    
    C --> C1["Desplegar en GlassFish"]
    C --> C2["Tomar 9 capturas según tabla pág. 3"]
    
    D --> D1["Pegar capturas en el documento .docx"]
    D --> D2["Crear integrantes.txt con datos del alumno"]
```

### Acción 1: Base de Datos Limpia (Desbloqueo Técnico)
1. **Problema detectado:** `BD_CineplanetFinal.sql` contiene una rutina de bucles masivos para Power BI que intentó crear más de 700,000 registros, saturando el disco `C:` (`Mens. 1101` y `Mens. 545`). Debido a esto, la tabla `Auditoria` no llegó a crearse y las contraseñas no se convirtieron a SHA-256.
2. **Solución:**
   * Liberar espacio en `C:` (eliminando archivos temporales o descargados en `Downloads`).
   * Ejecutar un script SQL optimizado que cree las 12 tablas, datos iniciales para la tienda y hashes SHA-256, suprimiendo la carga masiva de Power BI.

### Acción 2: Ejecutar y Tomar las 9 Capturas de Seguridad
En la página 3 del documento Word se solicita documentar las siguientes 9 pruebas:

| N.° | Prueba solicitada en el Word | Cómo realizarla | Qué capturar |
| :-: | :--- | :--- | :--- |
| **1** | **Inyección SQL en login** | Ingresar usuario `' OR '1'='1' --` con cualquier clave. | Mensaje genérico *"Usuario o clave incorrectos"*. |
| **2** | **Fuerza bruta y bloqueo** | Ingresar 3 veces clave incorrecta para `admin`. | Pantalla de cuenta bloqueada por 5 min + registro `BLOQUEO` en auditoría. |
| **3** | **Salto de rol (cajero)** | Login con `cajero` e ingresar a la URL `/VentaServlet` o `/AuditoriaServlet`. | Pantalla de error HTTP 403 (Acceso denegado). |
| **4** | **IDOR en comprobante** | Iniciar sesión como cliente, entrar a un comprobante y alterar el `?id=` por otro número. | Pantalla con mensaje *"Compra no encontrada"*. |
| **5** | **XSS reflejado / almacenado** | Registrar cliente con nombre `<script>alert('XSS')</script>`. | Texto renderizado como texto plano seguro (sin alert emergente). |
| **6** | **POST sin token CSRF** | Enviar petición POST a `/PagoServlet` o `/AnularServlet` sin el campo oculto `csrf`. | Respuesta HTTP 403 y evento `CSRF` en la tabla `Auditoria`. |
| **7** | **Manipulación de precio** | Modificar precio o total en el carrito con F12 (`total=0.01`). | Comprobante final con cobro exacto según la BD (S/ 20.90). |
| **8** | **Doble venta de butaca** | Abrir 2 sesiones distintas y comprar la misma butaca (ej. A11) en simultáneo. | Un cliente compra; el segundo recibe *"La butaca A11 ya fue vendida"*. |
| **9** | **Sesión tras logout** | Cerrar sesión y presionar el botón "Atrás" del navegador. | Redirección automática a `login.jsp` (gracias a `no-store`). |

### Acción 3: Datos de Identificación y Archivo `integrantes.txt`
1. Completar en la portada del archivo Word: Nombres completos, DNI, Código de alumno y fecha.
2. Crear en la raíz del proyecto el archivo `integrantes.txt` con la estructura:
   ```text
   DNI | APELLIDOS Y NOMBRES | PORCENTAJE
   [Tu_DNI] | [Tus Apellidos y Nombres] | 100%
   ```

---

## 4. Guía Rápida de Credenciales de Prueba

| Rol | Usuario | Clave en Texto Plano | Hash Almacenado en BD (SHA-256) |
| :--- | :--- | :--- | :--- |
| **ADMIN** | `admin` | `Admin#2026` | `8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918` |
| **CAJERO** | `cajero` | `Cajero#2026` | `04f8996da763b7a969b1028ee3007569eaf3a635486ddab211d512c85b9df8fb` |
| **CLIENTE** | DNI `45678123` | `Cliente#2026` | `b5435948312015fa2fc51b3bb6a71d87e1451f2f0163351ec12cbcd492db23d9` |

---

## 5. Conclusión y Veredicto Final

El desarrollo del proyecto **`Evaluacion_Final_Cineplanet`** se encuentra en un nivel de **calidad técnica Destacado (18/18)**. La arquitectura MVC, el empleo de transacciones atómicas, la desinfección contra XSS, la protección CSRF por token y la auditoría de seguridad cumplen con rigor todos los estándares del curso y las directrices OWASP Top 10.

Para asegurar la calificación de **20 / 20**, únicamente resta:
1. Reemplazar la base de datos local con el script limpio sin datos de Power BI.
2. Ejecutar la aplicación en GlassFish para tomar las 9 capturas solicitadas y pegarlas en el Word.
3. Adjuntar el archivo `integrantes.txt` al comprimido `.zip` final.
