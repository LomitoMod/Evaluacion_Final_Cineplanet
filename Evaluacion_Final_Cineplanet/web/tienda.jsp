<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Cartelera - Cineplanet</title>
        <link rel="stylesheet" href="css/estilos.css">
    </head>
    <body>
        <%@ include file="fragmentos/menu.jspf" %>
        <main class="contenedor">
            <%@ include file="fragmentos/mensajes.jspf" %>

            <c:choose>
                <c:when test="${empty sedeSel}">
                    <div class="tarjeta">
                        <h1>Elige tu cine</h1>
                        <p class="subtitulo">Cartelera de los proximos 7 dias</p>
                        <div class="tarjetas">
                            <c:forEach var="sede" items="${sedes}">
                                <div class="tarjeta-mini">
                                    <h3><c:out value="${sede.nombre}"/></h3>
                                    <p>
                                        <c:out value="${sede.ciudad}"/> -
                                        <c:out value="${sede.region}"/> -
                                        <c:out value="${sede.salas}"/> salas
                                    </p>
                                    <a class="boton pequeno"
                                       href="TiendaServlet?idSede=<c:out value='${sede.idSede}'/>">Ver funciones</a>
                                </div>
                            </c:forEach>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="tarjeta">
                        <h1><c:out value="${sedeSel.nombre}"/></h1>
                        <p class="subtitulo">
                            <c:out value="${sedeSel.ciudad}"/> - Funciones de los proximos 7 dias
                            &nbsp; <a href="TiendaServlet">Cambiar de cine</a>
                        </p>

                        <c:choose>
                            <c:when test="${empty funciones}">
                                <p class="aviso">No hay funciones disponibles con butacas libres en los proximos 7 dias.</p>
                            </c:when>
                            <c:otherwise>
                                <table class="tabla">
                                    <thead>
                                        <tr>
                                            <th>Pelicula</th>
                                            <th>Genero</th>
                                            <th>Fecha y hora</th>
                                            <th>Sala</th>
                                            <th>Precio</th>
                                            <th>Disponibles</th>
                                            <th></th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="f" items="${funciones}">
                                            <tr>
                                                <td><c:out value="${f.tituloPelicula}"/></td>
                                                <td><c:out value="${f.generoPelicula}"/></td>
                                                <td><c:out value="${f.fechaHora}"/></td>
                                                <td><c:out value="${f.sala}"/></td>
                                                <td>S/ <c:out value="${f.precio}"/></td>
                                                <td><c:out value="${f.butacasDisponibles}"/> de
                                                    <c:out value="${f.butacasTotales}"/></td>
                                                <td>
                                                    <a class="boton pequeno"
                                                       href="ButacasServlet?idFuncion=<c:out value='${f.idFuncion}'/>">Elegir butacas</a>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <div class="tarjeta">
                        <h2>Dulceria</h2>
                        <p class="subtitulo">Agrega combos, canchitas y bebidas a tu carrito</p>

                        <c:choose>
                            <c:when test="${empty productos}">
                                <p class="aviso">No hay productos activos.</p>
                            </c:when>
                            <c:otherwise>
                                <table class="tabla">
                                    <thead>
                                        <tr>
                                            <th>Producto</th>
                                            <th>Categoria</th>
                                            <th>Precio</th>
                                            <th>Cantidad</th>
                                            <th></th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="p" items="${productos}">
                                            <tr>
                                                <td><c:out value="${p.nombre}"/></td>
                                                <td><c:out value="${p.categoria}"/></td>
                                                <td>S/ <c:out value="${p.precio}"/></td>
                                                <td colspan="3">
                                                    <form class="inline" method="post" action="CarritoServlet">
                                                        <input type="hidden" name="csrf" value="<c:out value='${sessionScope.csrf}'/>">
                                                        <input type="hidden" name="accion" value="producto">
                                                        <input type="hidden" name="idProducto" value="<c:out value='${p.idProducto}'/>">
                                                        <input type="hidden" name="idSede" value="<c:out value='${sedeSel.idSede}'/>">
                                                        <select name="cantidad">
                                                            <c:forEach begin="1" end="10" var="n">
                                                                <option value="${n}">${n}</option>
                                                            </c:forEach>
                                                        </select>
                                                        <button class="pequeno" type="submit">Agregar</button>
                                                    </form>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </c:otherwise>
            </c:choose>
        </main>
    </body>
</html>
