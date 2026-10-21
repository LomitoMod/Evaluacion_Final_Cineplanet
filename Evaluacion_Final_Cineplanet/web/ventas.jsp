<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Ventas - Cineplanet</title>
        <link rel="stylesheet" href="css/estilos.css">
    </head>
    <body>
        <%@ include file="fragmentos/menu.jspf" %>
        <main class="contenedor">
            <%@ include file="fragmentos/mensajes.jspf" %>

            <div class="tarjeta">
                <h1>Reporte de ventas</h1>
                <p class="subtitulo">Solo visible para el rol ADMIN</p>

                <form method="get" action="VentaServlet" class="grupo">
                    <label for="desde">Desde</label>
                    <input type="date" id="desde" name="desde" value="<c:out value='${desdeSel}'/>">
                    <label for="hasta">Hasta</label>
                    <input type="date" id="hasta" name="hasta" value="<c:out value='${hastaSel}'/>">
                    <button class="pequeno" type="submit">Filtrar</button>
                </form>

                <c:choose>
                    <c:when test="${empty ventas}">
                        <p class="aviso">No hay ventas en el rango seleccionado.</p>
                    </c:when>
                    <c:otherwise>
                        <table class="tabla">
                            <thead>
                                <tr>
                                    <th>N. venta</th>
                                    <th>Fecha</th>
                                    <th>Cliente</th>
                                    <th>DNI</th>
                                    <th>Cine</th>
                                    <th>Medio</th>
                                    <th>Total</th>
                                    <th>Estado</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="v" items="${ventas}">
                                    <tr>
                                        <td><c:out value="${v.idVenta}"/></td>
                                        <td><c:out value="${v.fechaVenta}"/></td>
                                        <td><c:out value="${v.nombreCliente}"/></td>
                                        <td><c:out value="${v.dniCliente}"/></td>
                                        <td><c:out value="${v.nombreSede}"/></td>
                                        <td><c:out value="${v.medioPago}"/></td>
                                        <td>S/ <c:out value="${v.total}"/></td>
                                        <td><c:out value="${v.estado}"/></td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                        <p class="total texto-derecha">
                            <c:out value="${ventas.size()}"/> ventas -
                            Acumulado: S/ <c:out value="${acumulado}"/>
                        </p>
                    </c:otherwise>
                </c:choose>
            </div>
        </main>
    </body>
</html>
