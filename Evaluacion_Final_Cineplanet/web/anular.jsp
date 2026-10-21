<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Anular entrada - Cineplanet</title>
        <link rel="stylesheet" href="css/estilos.css">
    </head>
    <body>
        <%@ include file="fragmentos/menu.jspf" %>
        <main class="contenedor">
            <%@ include file="fragmentos/mensajes.jspf" %>

            <div class="tarjeta">
                <h1>Anular entrada</h1>
                <p class="subtitulo">Busca al cliente por DNI y anula sus entradas activas</p>

                <form method="get" action="AnularServlet" class="grupo">
                    <label for="dni">DNI del cliente</label>
                    <input type="text" id="dni" name="dni" maxlength="8"
                           value="<c:out value='${dniSel}'/>" placeholder="Ej. 45678123">
                    <button class="pequeno" type="submit">Buscar</button>
                </form>

                <c:choose>
                    <c:when test="${empty dniSel}">
                        <p class="ayuda">Ingresa un DNI de 8 digitos para ver sus entradas.</p>
                    </c:when>
                    <c:when test="${empty entradas}">
                        <p class="aviso">No se encontraron entradas para el DNI <c:out value="${dniSel}"/>.</p>
                    </c:when>
                    <c:otherwise>
                        <table class="tabla">
                            <thead>
                                <tr>
                                    <th>N. entrada</th>
                                    <th>Cliente</th>
                                    <th>Pelicula</th>
                                    <th>Fecha funcion</th>
                                    <th>Butaca</th>
                                    <th>Precio</th>
                                    <th>Estado</th>
                                    <th>Accion</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="e" items="${entradas}">
                                    <tr>
                                        <td><c:out value="${e.idEntrada}"/></td>
                                        <td><c:out value="${e.nombreCliente}"/></td>
                                        <td><c:out value="${e.tituloPelicula}"/></td>
                                        <td><c:out value="${e.fechaFuncion}"/></td>
                                        <td><c:out value="${e.butaca}"/></td>
                                        <td>S/ <c:out value="${e.precioPagado}"/></td>
                                        <td>
                                            <span class="insignia ${e.estado eq 'ACTIVA' ? 'ok' : 'malo'}">
                                                <c:out value="${e.estado}"/>
                                            </span>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${e.estado eq 'ACTIVA'}">
                                                    <form class="inline" method="post" action="AnularServlet">
                                                        <input type="hidden" name="csrf"
                                                               value="<c:out value='${sessionScope.csrf}'/>">
                                                        <input type="hidden" name="idEntrada"
                                                               value="<c:out value='${e.idEntrada}'/>">
                                                        <button class="pequeno secundario" type="submit">Anular</button>
                                                    </form>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="insignia">Ya anulada</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                        <p class="ayuda">
                            La anulacion se realiza con un formulario POST protegido por token CSRF
                            y queda registrada en la tabla Auditoria.
                        </p>
                    </c:otherwise>
                </c:choose>
            </div>
        </main>
    </body>
</html>
