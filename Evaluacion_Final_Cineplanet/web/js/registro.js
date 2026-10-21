/**
 * Validaciones en cliente para el formulario de Registro de Clientes
 * Proyecto: Evaluacion_Final_Cineplanet
 * Utiliza constantes genéricas de web/js/constantes.js
 */
(function() {
    'use strict';

    document.addEventListener('DOMContentLoaded', function() {
        var form = document.getElementById('formRegistro');
        if (!form) return;

        var dni = document.getElementById('dni');
        var nombres = document.getElementById('nombres');
        var apellidos = document.getElementById('apellidos');
        var correo = document.getElementById('correo');
        var telefono = document.getElementById('telefono');
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

        // 1) VALIDACIÓN DNI
        function validarDni() {
            var val = dni.value.trim();
            var len = C.TEXTO_SIZE_8 || 8;
            if (!val) {
                setError(dni, 'error-dni', 'El DNI es obligatorio.');
                return false;
            }
            if (C.DNI && !C.DNI.test(val)) {
                setError(dni, 'error-dni', 'El DNI debe tener exactamente ' + len + ' dígitos numéricos.');
                return false;
            }
            clearError(dni, 'error-dni');
            return true;
        }

        dni.addEventListener('input', function() {
            var len = C.TEXTO_SIZE_8 || 8;
            this.value = this.value.replace(/\D/g, '').slice(0, len);
            if (this.value.length === len) {
                clearError(dni, 'error-dni');
            } else if (dni.classList.contains('input-error')) {
                validarDni();
            }
        });
        dni.addEventListener('blur', validarDni);

        // 2) VALIDACIÓN NOMBRES
        function validarNombres() {
            var val = nombres.value.trim();
            var max = C.TEXTO_SIZE_60 || 60;
            if (!val) {
                setError(nombres, 'error-nombres', 'Los nombres son obligatorios.');
                return false;
            }
            if (val.length > max) {
                setError(nombres, 'error-nombres', 'Los nombres no pueden exceder los ' + max + ' caracteres.');
                return false;
            }
            if (C.TEXT_ALFANUMERICO_60 && !C.TEXT_ALFANUMERICO_60.test(val)) {
                setError(nombres, 'error-nombres', 'Solo se permiten caracteres alfanuméricos y vocales con tildes.');
                return false;
            }
            clearError(nombres, 'error-nombres');
            return true;
        }

        nombres.addEventListener('input', function() {
            if (nombres.classList.contains('input-error')) {
                validarNombres();
            }
        });
        nombres.addEventListener('blur', validarNombres);

        // 3) VALIDACIÓN APELLIDOS
        function validarApellidos() {
            var val = apellidos.value.trim();
            var max = C.TEXTO_SIZE_60 || 60;
            if (!val) {
                setError(apellidos, 'error-apellidos', 'Los apellidos son obligatorios.');
                return false;
            }
            if (val.length > max) {
                setError(apellidos, 'error-apellidos', 'Los apellidos no pueden exceder los ' + max + ' caracteres.');
                return false;
            }
            if (C.TEXT_ALFANUMERICO_60 && !C.TEXT_ALFANUMERICO_60.test(val)) {
                setError(apellidos, 'error-apellidos', 'Solo se permiten caracteres alfanuméricos y vocales con tildes.');
                return false;
            }
            clearError(apellidos, 'error-apellidos');
            return true;
        }

        apellidos.addEventListener('input', function() {
            if (apellidos.classList.contains('input-error')) {
                validarApellidos();
            }
        });
        apellidos.addEventListener('blur', validarApellidos);

        // 4) VALIDACIÓN CORREO
        function validarCorreo() {
            var val = correo.value.trim();
            var max = C.TEXTO_SIZE_80 || 80;
            if (!val) {
                setError(correo, 'error-correo', 'El correo electrónico es obligatorio.');
                return false;
            }
            if (val.length > max) {
                setError(correo, 'error-correo', 'El correo no puede exceder los ' + max + ' caracteres.');
                return false;
            }
            if (C.CORREO && !C.CORREO.test(val)) {
                setError(correo, 'error-correo', 'Formato de correo no válido. Caracteres permitidos: ._%+-');
                return false;
            }
            clearError(correo, 'error-correo');
            return true;
        }

        correo.addEventListener('input', function() {
            if (correo.classList.contains('input-error')) {
                validarCorreo();
            }
        });
        correo.addEventListener('blur', validarCorreo);

        // 5) VALIDACIÓN TELÉFONO
        function validarTelefono() {
            var val = telefono.value.trim();
            var max = C.TEXTO_SIZE_15 || 15;
            if (!val) {
                clearError(telefono, 'error-telefono');
                return true;
            }
            if (C.TELEFONO && !C.TELEFONO.test(val)) {
                setError(telefono, 'error-telefono', 'El teléfono solo debe contener números (máximo ' + max + ' dígitos).');
                return false;
            }
            clearError(telefono, 'error-telefono');
            return true;
        }

        telefono.addEventListener('input', function() {
            var max = C.TEXTO_SIZE_15 || 15;
            this.value = this.value.replace(/\D/g, '').slice(0, max);
            if (telefono.classList.contains('input-error')) {
                validarTelefono();
            }
        });
        telefono.addEventListener('blur', validarTelefono);

        // 6) VALIDACIÓN CLAVE
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
            var v1 = validarDni();
            var v2 = validarNombres();
            var v3 = validarApellidos();
            var v4 = validarCorreo();
            var v5 = validarTelefono();
            var v6 = validarClave();

            if (!(v1 && v2 && v3 && v4 && v5 && v6)) {
                e.preventDefault();
                var primerError = form.querySelector('.input-error');
                if (primerError) {
                    primerError.focus();
                }
            }
        });
    });
})();
