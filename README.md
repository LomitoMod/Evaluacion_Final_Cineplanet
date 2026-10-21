# Evaluacion_Final_Cineplanet

Aplicación web Java EE (Servlets + JSP) del curso **Seguridad de Aplicaciones (02414)**.
Sistema de venta de entradas de cine con los controles del **OWASP Top 10** aplicados:
autenticación con hash, sesiones con expiración, control de acceso por roles,
prevención de inyección SQL, XSS y CSRF, integridad transaccional, manejo seguro
de errores y auditoría de eventos.

## Estructura

```
Examen Final/
├── BD_CineplanetFinal.sql          # Script de la base de datos (12 tablas + datos)
└── Evaluacion_Final_Cineplanet/    # Proyecto NetBeans (Ant)
    ├── src/java/
    │   ├── conexion/    ConexionSQL.java        # DataSource JDBC (SQL Server)
    │   ├── modelo/      Usuario, Cliente, Sede, Pelicula, Funcion, Producto,
    │   │                Venta, Entrada, DetalleVenta, Carrito
    │   ├── dao/         UsuarioDAO, ClienteDAO, VentaDAO, EntradaDAO, FuncionDAO,
    │   │                ProductoDAO, SedeDAO, AuditoriaDAO, CompraException
    │   ├── controlador/ Login, Logout, Registro, Tienda, Butacas, Carrito, Pago,
    │   │                Comprobante, MisEntradas, Venta, Auditoria, Anular
    │   ├── filtro/      SesionFilter (roles) y CabecerasFilter (cabeceras HTTP)
    │   └── util/        Seguridad.java (SHA-256, bloqueo, token CSRF)
    └── web/             login, registro, panel, tienda, butacas, carrito,
                         comprobante, misentradas, ventas, auditoria, anular, error
```

## Requisitos

- SQL Server 2019+ (con TCP habilitado en el puerto **1433**)
- JDK 8
- GlassFish 4.1.1 (o cualquier contenedor Servlet 3.0 / JSP 2.2)
- NetBeans con Apache Ant (para importar y ejecutar el proyecto)

## Puesta en marcha

1. **Base de datos**: ejecutar `BD_CineplanetFinal.sql` en SQL Server Management Studio
   (no modificar la estructura de las tablas).
2. **Conexión**: revisar `src/java/conexion/ConexionSQL.java`
   (`jdbc:sqlserver://localhost:1433;databaseName=BD_CineplanetFinal;user=sa;password=123;encrypt=false`)
   y ajustar usuario/clave si tu servidor es distinto.
3. **Proyecto**: en NetBeans → *Archivo ▸ Abrir Proyecto* → carpeta
   `Evaluacion_Final_Cineplanet` (asegurar en *Propiedades ▸ Servidor* que apunte a GlassFish).
4. **Compilar y desplegar**:
   ```bat
   set JAVA_HOME=C:\ruta\al\jdk8
   ant -f Evaluacion_Final_Cineplanet\build.xml dist
   asadmin deploy --force=true --name=Evaluacion_Final_Cineplanet Evaluacion_Final_Cineplanet\dist\Evaluacion_Final_Cineplanet.war
   ```
   (o *Run* desde NetBeans).
5. Abrir: `http://localhost:8080/Evaluacion_Final_Cineplanet/`

## Credenciales de prueba

| Rol | Usuario | Clave |
| --- | --- | --- |
| ADMIN | `admin` | `Admin#2026` |
| CAJERO | `cajero` | `Cajero#2026` |
| CLIENTE | DNI `45678123` | `Cliente#2026` |

Las claves se guardan únicamente como **hash SHA-256** (64 caracteres) en
`Usuario.clave` y `Cliente.clave`.

## Funcionalidad por rol

- **CLIENTE**: cartelera de los próximos 7 días, mapa de butacas, carrito, pago
  (TARJETA/YAPE/PLIN), comprobante y "Mis entradas".
- **CAJERO**: panel de boletería y anulación de entradas por DNI.
- **ADMIN**: todo lo anterior + reporte de ventas por rango de fechas y vista de auditoría.

## Controles de seguridad implementados

| Control | Dónde |
| --- | --- |
| Consultas con `PreparedStatement` en todos los DAO | `dao/*` |
| Hash SHA-256 de contraseñas | `util/Seguridad.java` |
| Sesión con expiración de 15 min + invalidación al cerrar sesión | `web/WEB-INF/web.xml`, `LogoutServlet` |
| Protección contra fijación de sesión (sesión nueva tras login) | `LoginServlet` |
| Bloqueo 5 min tras 3 intentos fallidos | `util/Seguridad.java`, evento `BLOQUEO` |
| Mensaje genérico de error de autenticación | `Seguridad.MSJ_GENERICO` |
| Control de acceso por rol + 403 con auditoría | `filtro/SesionFilter.java` |
| Token CSRF en todas las acciones POST | `Carrito`, `Pago`, `Anular`, `Logout` |
| Salida escapada con `<c:out>` (XSS) | todos los JSP |
| Precios siempre leídos de la BD (no del navegador) | `PagoServlet`, `VentaDAO` |
| Transacciones con `commit`/`rollback` y control de butaca duplicada | `VentaDAO.registrar` |
| Páginas de error genéricas 403/404/500 (sin trazas) | `web/error.jsp` |
| Auditoría de LOGIN_OK/FALLIDO, BLOQUEO, LOGOUT, COMPRA, ANULACION, ACCESO_DENEGADO, CSRF | `AuditoriaDAO` |
| Cabeceras: CSP estricta, X-Frame-Options DENY, nosniff, no-store, Referrer-Policy | `filtro/CabecerasFilter.java` |

## Pruebas de seguridad

| N.° | Prueba | Resultado |
| --- | --- | --- |
| 1 | Inyección SQL en el login (`' OR '1'='1' --`) | Acceso denegado, mensaje genérico |
| 2 | Fuerza bruta (3 claves incorrectas) | Bloqueo 5 min + evento `BLOQUEO` |
| 3 | IDOR cambiando `?id=` del comprobante | "Compra no encontrada" |
| 4 | CAJERO abriendo `/VentaServlet` | HTTP 403 + `ACCESO_DENEGADO` |
| 5 | XSS almacenado en el nombre del cliente | Se muestra como texto escapado |
| 6 | POST sin token CSRF | HTTP 403 + evento `CSRF` |
| 7 | Manipulación de precio con F12 | Total calculado con el precio de la BD |
| 8 | Doble venta de la misma butaca | Solo una compra; la otra recibe mensaje |
| 9 | Sesión tras logout + botón Atrás | Redirige al login |
