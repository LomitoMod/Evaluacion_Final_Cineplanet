<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Iniciar sesion - Cineplanet</title>
        <link rel="stylesheet" href="css/estilos.css">
    </head>
    <body class="centrado">
        <main class="tarjeta ancho">
            <h1>Cineplanet</h1>
            <p class="subtitulo">Evaluacion Final - Seguridad de Aplicaciones</p>

            <%@ include file="fragmentos/mensajes.jspf" %>

            <form method="post" action="LoginServlet" autocomplete="off">
                <label for="usuario">Usuario o DNI</label>
                <input type="text" id="usuario" name="usuario" maxlength="30" required autofocus>

                <label for="clave">Clave</label>
                <input type="password" id="clave" name="clave" maxlength="64" required>

                <div class="bloque-form">
                    <button type="submit">Ingresar</button>
                </div>
            </form>

            <p class="enlace"><a href="registro.jsp">No tienes cuenta? Registrate aqui</a></p>
            <p class="ayuda">
                Credenciales de prueba:<br>
                admin / Admin#2026 (ADMIN)<br>
                cajero / Cajero#2026 (CAJERO)<br>
                DNI 45678123 / Cliente#2026 (CLIENTE)
            </p>
        </main>
    </body>
</html>
