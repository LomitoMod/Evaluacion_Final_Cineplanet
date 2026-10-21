<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="util.ConstantesValidacion"%>
<c:set var="DNI" value="${not empty applicationScope.DNI ? applicationScope.DNI : ConstantesValidacion.DNI}"/>
<c:set var="TEXT_ALFANUMERICO_60" value="${not empty applicationScope.TEXT_ALFANUMERICO_60 ? applicationScope.TEXT_ALFANUMERICO_60 : ConstantesValidacion.TEXT_ALFANUMERICO_60}"/>
<c:set var="CORREO" value="${not empty applicationScope.CORREO ? applicationScope.CORREO : ConstantesValidacion.CORREO}"/>
<c:set var="TELEFONO" value="${not empty applicationScope.TELEFONO ? applicationScope.TELEFONO : ConstantesValidacion.TELEFONO}"/>
<c:set var="CLAVE" value="${not empty applicationScope.CLAVE ? applicationScope.CLAVE : ConstantesValidacion.CLAVE}"/>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Registro de cliente - Cineplanet</title>
        <link rel="stylesheet" href="css/estilos.css">
    </head>
    <body class="centrado">
        <main class="tarjeta ancho margen-superior">
            <h1>Registro de cliente</h1>
            <p class="subtitulo">Crea tu cuenta segura con tu DNI</p>

            <%@ include file="fragmentos/mensajes.jspf" %>

            <form id="formRegistro" method="post" action="RegistroServlet" autocomplete="off" novalidate>
                <div class="campo-form">
                    <label for="dni">DNI (<c:out value="${ConstantesValidacion.TEXTO_SIZE_8}"/> dígitos) <span class="obligatorio">*</span></label>
                    <input type="text" id="dni" name="dni"
                           maxlength="<c:out value='${ConstantesValidacion.TEXTO_SIZE_8}'/>"
                           minlength="<c:out value='${ConstantesValidacion.TEXTO_SIZE_8}'/>"
                           pattern="<c:out value='${DNI}'/>" inputmode="numeric" required
                           placeholder="Ej. 45678123"
                           value="<c:out value='${valDni}'/>">
                    <span class="mensaje-error" id="error-dni"></span>
                </div>

                <div class="campo-form">
                    <label for="nombres">Nombres <span class="obligatorio">*</span></label>
                    <input type="text" id="nombres" name="nombres"
                           maxlength="<c:out value='${ConstantesValidacion.TEXTO_SIZE_60}'/>" required
                           pattern="<c:out value='${TEXT_ALFANUMERICO_60}'/>"
                           placeholder="Ej. Juan Carlos"
                           value="<c:out value='${valNombres}'/>">
                    <span class="mensaje-error" id="error-nombres"></span>
                </div>

                <div class="campo-form">
                    <label for="apellidos">Apellidos <span class="obligatorio">*</span></label>
                    <input type="text" id="apellidos" name="apellidos"
                           maxlength="<c:out value='${ConstantesValidacion.TEXTO_SIZE_60}'/>" required
                           pattern="<c:out value='${TEXT_ALFANUMERICO_60}'/>"
                           placeholder="Ej. Pérez Gómez"
                           value="<c:out value='${valApellidos}'/>">
                    <span class="mensaje-error" id="error-apellidos"></span>
                </div>

                <div class="campo-form">
                    <label for="correo">Correo electrónico <span class="obligatorio">*</span></label>
                    <input type="email" id="correo" name="correo"
                           maxlength="<c:out value='${ConstantesValidacion.TEXTO_SIZE_80}'/>" required
                           pattern="<c:out value='${CORREO}'/>"
                           placeholder="Ej. juan.perez@correo.com"
                           value="<c:out value='${valCorreo}'/>">
                    <span class="mensaje-error" id="error-correo"></span>
                </div>

                <div class="campo-form">
                    <label for="telefono">Teléfono (opcional)</label>
                    <input type="text" id="telefono" name="telefono"
                           maxlength="<c:out value='${ConstantesValidacion.TEXTO_SIZE_15}'/>"
                           pattern="<c:out value='${TELEFONO}'/>" inputmode="numeric"
                           placeholder="Ej. 987654321 (máx. 15 dígitos)"
                           value="<c:out value='${valTelefono}'/>">
                    <span class="mensaje-error" id="error-telefono"></span>
                </div>

                <div class="campo-form">
                    <label for="clave">Clave segura <span class="obligatorio">*</span></label>
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
                    <button type="submit" id="btnRegistrar">Registrarme</button>
                </div>
            </form>

            <p class="enlace"><a href="login.jsp">Volver al inicio de sesión</a></p>
        </main>

        <script src="js/constantes.js"></script>
        <script src="js/registro.js"></script>
    </body>
</html>
