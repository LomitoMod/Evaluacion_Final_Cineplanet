<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Registro de cliente - Cineplanet</title>
        <link rel="stylesheet" href="css/estilos.css">
    </head>
    <body class="centrado">
        <main class="tarjeta ancho">
            <h1>Registro de cliente</h1>
            <p class="subtitulo">Crea tu cuenta con tu DNI</p>

            <%@ include file="fragmentos/mensajes.jspf" %>

            <form method="post" action="RegistroServlet" autocomplete="off">
                <label for="dni">DNI (8 digitos)</label>
                <input type="text" id="dni" name="dni" maxlength="8" required>

                <label for="nombres">Nombres</label>
                <input type="text" id="nombres" name="nombres" maxlength="60" required>

                <label for="apellidos">Apellidos</label>
                <input type="text" id="apellidos" name="apellidos" maxlength="60" required>

                <label for="correo">Correo electronico</label>
                <input type="text" id="correo" name="correo" maxlength="80">

                <label for="telefono">Telefono (opcional)</label>
                <input type="text" id="telefono" name="telefono" maxlength="15">

                <label for="clave">Clave (minimo 8 caracteres)</label>
                <input type="password" id="clave" name="clave" maxlength="64" required>

                <div class="bloque-form">
                    <button type="submit">Registrarme</button>
                </div>
            </form>

            <p class="enlace"><a href="login.jsp">Volver al inicio de sesion</a></p>
        </main>
    </body>
</html>
