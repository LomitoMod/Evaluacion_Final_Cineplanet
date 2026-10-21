<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Auditoria - Cineplanet</title>
        <link rel="stylesheet" href="css/estilos.css">
    </head>
    <body>
        <%@ include file="fragmentos/menu.jspf" %>
        <main class="contenedor">
            <%@ include file="fragmentos/mensajes.jspf" %>

            <div class="tarjeta">
                <h1>Auditoria del sistema</h1>
                <p class="subtitulo">Ultimos 100 eventos registrados - Solo visible para el rol ADMIN</p>

                <c:choose>
                    <c:when test="${empty eventos}">
                        <p class="aviso">No hay eventos registrados todavia.</p>
                    </c:when>
                    <c:otherwise>
                        <table class="tabla">
                            <thead>
                                <tr>
                                    <th>N.</th>
                                    <th>Fecha</th>
                                    <th>Usuario</th>
                                    <th>Evento</th>
                                    <th>Detalle</th>
                                    <th>IP</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="ev" items="${eventos}">
                                    <tr>
                                        <td><c:out value="${ev.idAuditoria}"/></td>
                                        <td><c:out value="${ev.fecha}"/></td>
                                        <td><c:out value="${ev.usuario}"/></td>
                                        <td><span class="insignia"><c:out value="${ev.evento}"/></span></td>
                                        <td><c:out value="${ev.detalle}"/></td>
                                        <td><c:out value="${ev.ip}"/></td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:otherwise>
                </c:choose>
            </div>
        </main>
    </body>
</html>
