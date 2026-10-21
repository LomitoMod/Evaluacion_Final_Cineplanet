<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Mis entradas - Cineplanet</title>
        <link rel="stylesheet" href="css/estilos.css">
    </head>
    <body>
        <%@ include file="fragmentos/menu.jspf" %>
        <main class="contenedor">
            <%@ include file="fragmentos/mensajes.jspf" %>

            <div class="tarjeta">
                <h1>Mis entradas</h1>
                <p class="subtitulo">Entradas compradas por <c:out value="${nombreSesion}"/></p>

                <c:choose>
                    <c:when test="${empty entradas}">
                        <p class="aviso">Todavia no tienes entradas registradas.</p>
                        <a class="boton" href="TiendaServlet">Comprar entradas</a>
                    </c:when>
                    <c:otherwise>
                        <table class="tabla">
                            <thead>
                                <tr>
                                    <th>N. entrada</th>
                                    <th>Pelicula</th>
                                    <th>Fecha funcion</th>
                                    <th>Sala</th>
                                    <th>Cine</th>
                                    <th>Butaca</th>
                                    <th>Precio</th>
                                    <th>Estado</th>
                                    <th>Compra</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="e" items="${entradas}">
                                    <tr>
                                        <td><c:out value="${e.idEntrada}"/></td>
                                        <td><c:out value="${e.tituloPelicula}"/></td>
                                        <td><c:out value="${e.fechaFuncion}"/></td>
                                        <td><c:out value="${e.sala}"/></td>
                                        <td><c:out value="${e.nombreSede}"/></td>
                                        <td><c:out value="${e.butaca}"/></td>
                                        <td>S/ <c:out value="${e.precioPagado}"/></td>
                                        <td>
                                            <span class="insignia ${e.estado eq 'ACTIVA' ? 'ok' : 'malo'}">
                                                <c:out value="${e.estado}"/>
                                            </span>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${e.idVenta > 0}">
                                                    <a href="ComprobanteServlet?id=<c:out value='${e.idVenta}'/>">
                                                        Ver comprobante</a>
                                                </c:when>
                                                <c:otherwise>-</c:otherwise>
                                            </c:choose>
                                        </td>
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
