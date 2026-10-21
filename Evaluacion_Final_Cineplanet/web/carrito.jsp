<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Carrito de compras - Cineplanet</title>
        <link rel="stylesheet" href="css/estilos.css">
    </head>
    <body>
        <%@ include file="fragmentos/menu.jspf" %>
        <main class="contenedor">
            <%@ include file="fragmentos/mensajes.jspf" %>

            <c:choose>
                <c:when test="${vacio}">
                    <div class="tarjeta ancho margen-superior">
                        <h1>Tu carrito esta vacio</h1>
                        <p class="subtitulo">Agrega butacas o productos de la dulceria.</p>
                        <a class="boton" href="TiendaServlet">Ir a la cartelera</a>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="tarjeta">
                        <h1>Tu carrito</h1>
                        <p class="subtitulo">Revisa los items antes de pagar</p>

                        <c:if test="${not empty carrito.entradas}">
                            <h2>Entradas</h2>
                            <table class="tabla">
                                <thead>
                                    <tr>
                                        <th>Butaca</th>
                                        <th>Funcion</th>
                                        <th>Precio unitario</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="e" items="${carrito.entradas}">
                                        <tr>
                                            <td><c:out value="${e.butaca}"/></td>
                                            <td><c:out value="${carrito.detalleFuncion}"/></td>
                                            <td>S/ <c:out value="${funcion.precio}"/></td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                            <p class="dato"><b>Entradas:</b> <c:out value="${carrito.totalEntradas()}"/></p>
                        </c:if>

                        <c:if test="${not empty carrito.productos}">
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
                                    <c:forEach var="d" items="${carrito.productos}">
                                        <tr>
                                            <td><c:out value="${d.nombreProducto}"/></td>
                                            <td><c:out value="${d.categoria}"/></td>
                                            <td><c:out value="${d.cantidad}"/></td>
                                            <td>S/ <c:out value="${d.precioUnit}"/></td>
                                            <td>S/ <c:out value="${d.precioUnit * d.cantidad}"/></td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </c:if>

                        <p class="total texto-derecha">Total: S/ <c:out value="${total}"/></p>
                        <p class="ayuda">
                            El total se calcula en el servidor con los precios almacenados en la
                            base de datos; el navegador no envia precios al pagar.
                        </p>

                        <form method="post" action="CarritoServlet" class="inline">
                            <input type="hidden" name="csrf" value="<c:out value='${sessionScope.csrf}'/>">
                            <input type="hidden" name="accion" value="vaciar">
                            <button class="secundario" type="submit">Vaciar carrito</button>
                        </form>
                    </div>

                    <div class="tarjeta">
                        <h2>Pago</h2>
                        <form method="post" action="PagoServlet">
                            <input type="hidden" name="csrf" value="<c:out value='${sessionScope.csrf}'/>">
                            <label for="medioPago">Medio de pago</label>
                            <select id="medioPago" name="medioPago">
                                <option value="TARJETA">TARJETA</option>
                                <option value="YAPE">YAPE</option>
                                <option value="PLIN">PLIN</option>
                            </select>
                            <div class="bloque-form">
                                <button type="submit">Pagar ahora</button>
                                <a class="boton secundario pequeno" href="TiendaServlet">Seguir comprando</a>
                            </div>
                        </form>
                    </div>
                </c:otherwise>
            </c:choose>
        </main>
    </body>
</html>
