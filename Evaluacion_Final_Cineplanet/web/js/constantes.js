/**
 * Constantes genéricas de validación para frontend (JavaScript).
 * Proyecto: Evaluacion_Final_Cineplanet
 * Sincronizado con util.ConstantesValidacion en Java y pattern en HTML.
 * Tamaños genéricos TEXTO_SIZE_N para mínimos, máximos o fijos.
 */
var CONSTANTES = (function() {
    'use strict';

    return {
        // Expresiones regulares genéricas
        DNI: /^\d{8}$/,
        TEXT_ALFANUMERICO_60: /^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑüÜ\s]{1,60}$/,
        CORREO: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/,
        TELEFONO: /^\d{1,15}$/,
        USUARIO: /^[a-zA-Z0-9@._%+-]{1,30}$/,
        CLAVE: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&#._-])[A-Za-z\d@$!%*?&#._-]{6,15}$/,

        // Tamaños genéricos (mínimos, máximos y longitudes fijas)
        TEXTO_SIZE_6: 6,
        TEXTO_SIZE_8: 8,
        TEXTO_SIZE_15: 15,
        TEXTO_SIZE_30: 30,
        TEXTO_SIZE_60: 60,
        TEXTO_SIZE_80: 80
    };
})();
