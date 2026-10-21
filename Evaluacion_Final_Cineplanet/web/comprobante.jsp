<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Comprobante - Cineplanet</title>
        <link rel="stylesheet" href="css/estilos.css">
    </head>
    <body>
        <%@ include file="fragmentos/menu.jspf" %>
        <main class="contenedor">
            <%@ include file="fragmentos/mensajes.jspf" %>

            <c:choose>
                <c:when test="${empty venta}">
                    <div class="tarjeta ancho margen-superior">
                        <h1>Compra no encontrada</h1>
                        <p class="subtitulo">Verifica el numero de compra o revisa Mis entradas.</p>
                        <a class="boton" href="MisEntradasServlet">Ver mis entradas</a>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="tarjeta">
                        <h1>Comprobante de compra N. <c:out value="${venta.idVenta}"/></h1>
                        <p class="subtitulo">Gracias por tu compra en Cineplanet</p>

                        <p class="dato"><b>Fecha:</b> <c:out value="${venta.fechaVenta}"/></p>
                        <p class="dato"><b>Cliente:</b> <c:out value="${venta.nombreCliente}"/>
                            (DNI <c:out value="${venta.dniCliente}"/>)</p>
                        <p class="dato"><b>Cine:</b> <c:out value="${venta.nombreSede}"/></p>
                        <p class="dato"><b>Medio de pago:</b> <c:out value="${venta.medioPago}"/></p>
                        <p class="dato"><b>Canal:</b> <c:out value="${venta.canal}"/></p>
                        <p class="dato"><b>Estado:</b>
                            <span class="insignia ${venta.estado eq 'PAGADA' ? 'ok' : 'malo'}">
                                <c:out value="${venta.estado}"/>
                            </span>
                        </p>

                        <c:if test="${not empty entradas}">
                            <h2>Entradas</h2>
                            <table class="tabla">
                                <thead>
                                    <tr>
                                        <th>Butaca</th>
                                        <th>Pelicula</th>
                                        <th>Fecha funcion</th>
                                        <th>Sala</th>
                                        <th>Precio</th>
                                        <th>Estado</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="e" items="${entradas}">
                                        <tr>
                                            <td><c:out value="${e.butaca}"/></td>
                                            <td><c:out value="${e.tituloPelicula}"/></td>
                                            <td><c:out value="${e.fechaFuncion}"/></td>
                                            <td><c:out value="${e.sala}"/></td>
                                            <td>S/ <c:out value="${e.precioPagado}"/></td>
                                            <td><c:out value="${e.estado}"/></td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </c:if>

                        <c:if test="${not empty productos}">
                            <h2>Dulceria</h2>
                            <table class="tabla">
                                <thead>
                                    <tr>
                                        <th>Producto</th>
                                        <th>Categoria</th>
                                        <th>Cantidad</th>
                                        <th>Precio unitario</th>
                                        <th>Subtotal</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="d" items="${productos}">
                                        <tr>
                                            <td><c:out value="${d.nombreProducto}"/></td>
                                            <td><c:out value="${d.categoria}"/></td>
                                            <td><c:out value="${d.cantidad}"/></td>
                                            <td>S/ <c:out value="${d.precioUnit}"/></td>
                                            <td>S/ <c:out value="${d.subtotal}"/></td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </c:if>

                        <p class="total texto-derecha">Total pagado: S/ <c:out value="${venta.total}"/></p>

                        <a class="boton secundario pequeno" href="MisEntradasServlet">Ver mis entradas</a>
                        <a class="boton secundario pequeno" href="TiendaServlet">Seguir comprando</a>
                    </div>
                </c:otherwise>
            </c:choose>
        </main>
    </body>
</html>
