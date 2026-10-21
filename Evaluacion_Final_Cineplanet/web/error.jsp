<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="codigoError" value="${requestScope['javax.servlet.error.status_code']}"/>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Pagina no encontrada - Cineplanet</title>
        <link rel="stylesheet" href="css/estilos.css">
    </head>
    <body>
        <%@ include file="fragmentos/mensajes.jspf" %>
        <main class="contenedor">
            <div class="tarjeta ancho margen-superior">
                <c:choose>
                    <c:when test="${not empty codigo}">
                        <p class="codigo-error"><c:out value="${codigo}"/></p>
                    </c:when>
                    <c:when test="${not empty codigoError}">
                        <p class="codigo-error"><c:out value="${codigoError}"/></p>
                    </c:when>
                    <c:otherwise>
                        <p class="codigo-error">500</p>
                    </c:otherwise>
                </c:choose>
                <h2>Uups, algo salio mal</h2>
                <p class="ayuda">
                    La pagina solicitada no existe o no tienes permiso para verla.
                    Si el problema continua, comunicate con el administrador del sistema.
                </p>
                <p><a class="boton" href="login.jsp">Volver al inicio</a></p>
            </div>
        </main>
    </body>
</html>
