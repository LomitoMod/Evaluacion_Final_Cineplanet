<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Elegir butacas - Cineplanet</title>
        <link rel="stylesheet" href="css/estilos.css">
    </head>
    <body>
        <%@ include file="fragmentos/menu.jspf" %>
        <main class="contenedor">
            <%@ include file="fragmentos/mensajes.jspf" %>

            <div class="tarjeta">
                <h1><c:out value="${funcion.tituloPelicula}"/></h1>
                <p class="subtitulo">
                    <c:out value="${funcion.nombreSede}"/> - Sala <c:out value="${funcion.sala}"/> -
                    <c:out value="${funcion.fechaHora}"/> - S/ <c:out value="${funcion.precio}"/> por entrada
                </p>

                <form method="post" action="CarritoServlet">
                    <input type="hidden" name="csrf" value="<c:out value='${sessionScope.csrf}'/>">
                    <input type="hidden" name="accion" value="entradas">
                    <input type="hidden" name="idFuncion" value="<c:out value='${funcion.idFuncion}'/>">

                    <div class="mapa">
                        <div class="pantalla">P A N T A L L A</div>

                        <c:forEach var="letra" items="${filas}">
                            <div class="fila">
                                <span class="etiqueta"><c:out value="${letra}"/></span>
                                <c:forEach begin="1" end="12" var="num">
                                    <c:set var="butaca" value="${letra}${num}"/>
                                    <c:choose>
                                        <c:when test="${estado[butaca] == 'ocupada'}">
                                            <label class="butaca ocupada">
                                                <input type="checkbox" name="butaca" value="${butaca}" disabled>
                                                <span><c:out value="${butaca}"/></span>
                                            </label>
                                        </c:when>
                                        <c:otherwise>
                                            <label class="butaca">
                                                <input type="checkbox" name="butaca" value="${butaca}">
                                                <span><c:out value="${butaca}"/></span>
                                            </label>
                                        </c:otherwise>
                                    </c:choose>
                                </c:forEach>
                            </div>
                        </c:forEach>

                        <div class="leyenda">
                            <span><i class="cuadro"></i> Libre</span>
                            <span><i class="cuadro ocupado"></i> Ocupada (no seleccionable)</span>
                            <span><i class="cuadro marcado"></i> Seleccionada</span>
                        </div>
                    </div>

                    <div class="bloque-form">
                        <button type="submit">Agregar al carrito</button>
                        <a class="boton secundario pequeno" href="CarritoServlet">Ver carrito</a>
                        <a class="boton secundario pequeno" href="TiendaServlet?idSede=<c:out value='${funcion.idSede}'/>">Volver a la cartelera</a>
                    </div>
                </form>

                <p class="ayuda">
                    Butacas disponibles: <c:out value="${funcion.butacasDisponibles}"/> de
                    <c:out value="${funcion.butacasTotales}"/>. Maximo 10 entradas por compra.
                </p>
            </div>
        </main>
    </body>
</html>
