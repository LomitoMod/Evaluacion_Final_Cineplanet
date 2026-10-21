/**
 * Validaciones en cliente para el formulario de Inicio de Sesión
 * Proyecto: Evaluacion_Final_Cineplanet
 * Utiliza constantes genéricas de web/js/constantes.js
 */
(function() {
    'use strict';

    document.addEventListener('DOMContentLoaded', function() {
        var form = document.getElementById('formLogin');
        if (!form) return;

        var usuario = document.getElementById('usuario');
        var clave = document.getElementById('clave');
        var C = window.CONSTANTES || {};

        function setError(input, spanId, mensaje) {
            input.classList.add('input-error');
            var span = document.getElementById(spanId);
            if (span) {
                span.textContent = mensaje;
            }
        }

        function clearError(input, spanId) {
            input.classList.remove('input-error');
            var span = document.getElementById(spanId);
            if (span) {
                span.textContent = '';
            }
        }

        // 1) VALIDACIÓN USUARIO O DNI
        function validarUsuario() {
            var val = usuario.value.trim();
            var max = C.TEXTO_SIZE_30 || 30;
            if (!val) {
                setError(usuario, 'error-usuario', 'El usuario o DNI es obligatorio.');
                return false;
            }
            if (val.length > max) {
                setError(usuario, 'error-usuario', 'El usuario o DNI no puede exceder los '
                        + max + ' caracteres.');
                return false;
            }
            /*if (C.USUARIO && !C.USUARIO.test(val)) {
                setError(usuario, 'error-usuario', 'Solo se permiten caracteres alfanuméricos y los símbolos @ . _ % + -');
                return false;
            }*/
            clearError(usuario, 'error-usuario');
            return true;
        }

        usuario.addEventListener('input', function() {
            if (usuario.classList.contains('input-error')) {
                validarUsuario();
            }
        });
        usuario.addEventListener('blur', validarUsuario);

        // 2) VALIDACIÓN CLAVE (exactamente la misma validación que en registro)
        function validarClave() {
            var val = clave.value;
            var min = C.TEXTO_SIZE_6 || 6;
            var max = C.TEXTO_SIZE_15 || 15;

            if (!val) {
                setError(clave, 'error-clave', 'La clave es obligatoria.');
                return false;
            }
            if (val.length < min || val.length > max) {
                setError(clave, 'error-clave', 'La clave debe tener entre ' + min
                        + ' y ' + max + ' caracteres.');
                return false;
            }
            if (!/[A-Z]/.test(val)) {
                setError(clave, 'error-clave', 'Debe incluir al menos 1 letra mayúscula.');
                return false;
            }
            if (!/[a-z]/.test(val)) {
                setError(clave, 'error-clave', 'Debe incluir al menos 1 letra minúscula.');
                return false;
            }
            if (!/\d/.test(val)) {
                setError(clave, 'error-clave', 'Debe incluir al menos 1 número.');
                return false;
            }
            if (!/[@$!%*?&#._-]/.test(val)) {
                setError(clave, 'error-clave', 'Debe incluir al menos 1 carácter especial (@$!%*?&#._-).');
                return false;
            }
            if (C.CLAVE && !C.CLAVE.test(val)) {
                setError(clave, 'error-clave', 'Solo se permiten caracteres alfanuméricos y @$!%*?&#._-');
                return false;
            }
            clearError(clave, 'error-clave');
            return true;
        }

        clave.addEventListener('input', function() {
            if (clave.classList.contains('input-error')) {
                validarClave();
            }
        });
        clave.addEventListener('blur', validarClave);

        // VALIDACIÓN GENERAL AL ENVIAR
        form.addEventListener('submit', function(e) {
            var v1 = validarUsuario();
            var v2 = validarClave();

            if (!(v1 && v2)) {
                e.preventDefault();
                var primerError = form.querySelector('.input-error');
                if (primerError) {
                    primerError.focus();
                }
            }
        });
    });
})();
