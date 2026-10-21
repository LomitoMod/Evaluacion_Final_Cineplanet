<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="util.ConstantesValidacion"%>
<c:set var="USUARIO" value="${not empty applicationScope.USUARIO ? applicationScope.USUARIO : ConstantesValidacion.USUARIO}"/>
<c:set var="CLAVE" value="${not empty applicationScope.CLAVE ? applicationScope.CLAVE : ConstantesValidacion.CLAVE}"/>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Iniciar sesión - Cineplanet</title>
        <link rel="stylesheet" href="css/estilos.css">
    </head>
    <body class="centrado">
        <main class="tarjeta ancho">
            <h1>Cineplanet</h1>
            <p class="subtitulo">Evaluación Final - Seguridad de Aplicaciones</p>

            <%@ include file="fragmentos/mensajes.jspf" %>

            <form id="formLogin" method="post" action="LoginServlet" autocomplete="off" novalidate>
                <div class="campo-form">
                    <label for="usuario">Usuario o DNI <span class="obligatorio">*</span></label>
                    <input type="text" id="usuario" name="usuario"
                           maxlength="<c:out value='${ConstantesValidacion.TEXTO_SIZE_30}'/>" required
                           pattern="<c:out value='${USUARIO}'/>"
                           placeholder="Usuario o DNI"
                           value="<c:out value='${not empty valUsuario ? valUsuario : param.usuario}'/>" autofocus>
                    <span class="mensaje-error" id="error-usuario"></span>
                </div>

                <div class="campo-form">
                    <label for="clave">Clave <span class="obligatorio">*</span></label>
                    <input type="password" id="clave" name="clave"
                           minlength="<c:out value='${ConstantesValidacion.TEXTO_SIZE_6}'/>"
                           maxlength="<c:out value='${ConstantesValidacion.TEXTO_SIZE_15}'/>" required
                           pattern="<c:out value='${CLAVE}'/>"
                           placeholder="De 6 a 15 caracteres">
                    <span class="mensaje-error" id="error-clave"></span>
                    <div class="requisitos-clave">
                        6 a 15 caracteres: mín. 1 mayúscula, 1 minúscula, 1 número y 1 especial (@$!%*?&#._-)
                    </div>
                </div>

                <div class="bloque-form">
                    <button type="submit" id="btnIngresar">Ingresar</button>
                </div>
            </form>

            <p class="enlace"><a href="registro.jsp">¿No tienes cuenta? Regístrate aquí</a></p>
            <p class="ayuda">
                Credenciales de prueba:<br>
                admin / Admin#2026 (ADMIN)<br>
                cajero / Cajero#2026 (CAJERO)<br>
                DNI 45678123 / Cliente#2026 (CLIENTE)
            </p>
        </main>

        <script src="js/constantes.js"></script>
        <script src="js/login.js"></script>
    </body>
</html>
