<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Panel - Cineplanet</title>
        <link rel="stylesheet" href="css/estilos.css">
    </head>
    <body>
        <%@ include file="fragmentos/menu.jspf" %>
        <main class="contenedor">
            <%@ include file="fragmentos/mensajes.jspf" %>

            <div class="tarjeta">
                <h1>Hola, <c:out value="${nombreSesion}"/></h1>
                <p class="subtitulo">
                    Sesion iniciada como <b><c:out value="${usuarioSesion}"/></b>
                    con rol <b><c:out value="${rolSesion}"/></b>.
                </p>
            </div>

            <c:choose>
                <c:when test="${rolSesion eq 'ADMIN'}">
                    <div class="tarjetas">
                        <div class="tarjeta-mini">
                            <h3>Ventas</h3>
                            <p>Reporte de ventas por rango de fechas.</p>
                            <a class="boton pequeno" href="VentaServlet">Abrir ventas</a>
                        </div>
                        <div class="tarjeta-mini">
                            <h3>Auditoria</h3>
                            <p>Ultimos 100 eventos registrados.</p>
                            <a class="boton pequeno" href="AuditoriaServlet">Abrir auditoria</a>
                        </div>
                        <div class="tarjeta-mini">
                            <h3>Anular entrada</h3>
                            <p>Anulacion de entradas por DNI de cliente.</p>
                            <a class="boton pequeno secundario" href="AnularServlet">Buscar entrada</a>
                        </div>
                    </div>
                </c:when>
                <c:when test="${rolSesion eq 'CAJERO'}">
                    <div class="tarjetas">
                        <div class="tarjeta-mini">
                            <h3>Anular entrada</h3>
                            <p>Anulacion de entradas por DNI de cliente.</p>
                            <a class="boton pequeno" href="AnularServlet">Buscar entrada</a>
                        </div>
                        <div class="tarjeta-mini">
                            <h3>Reportes</h3>
                            <p>Solo el rol ADMIN puede ver Ventas y Auditoria.</p>
                            <span class="insignia malo">Sin acceso</span>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="tarjetas">
                        <div class="tarjeta-mini">
                            <h3>Cartelera</h3>
                            <p>Elige un cine y compra tus entradas.</p>
                            <a class="boton pequeno" href="TiendaServlet">Ver cartelera</a>
                        </div>
                        <div class="tarjeta-mini">
                            <h3>Mis entradas</h3>
                            <p>Revisa tus entradas compradas.</p>
                            <a class="boton pequeno secundario" href="MisEntradasServlet">Ver entradas</a>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </main>
    </body>
</html>
