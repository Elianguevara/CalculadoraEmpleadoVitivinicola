package com.elian.calculadora_empleado_vitivinicola.model

data class CategoriaViña(
    val id: String,
    val nombre: String,
    /** 11 valores: índice 0 = "0 a 3 años", …, índice 10 = "Más de 30 años". */
    val basicosPorTramo: List<Double>,
)

/**
 * Todos los montos del convenio Viña (CCT 154/91) para una vigencia.
 * Para actualizar por paritaria: solo editar los valores en [SueldosRepositoryImpl].
 *
 * IMPORTANTE: [categorias][0] SIEMPRE debe ser el Obrero Común.
 * Es la base de referencia para Encargado, Capataz, Premio Asistencia y Sepelio.
 */
data class TarifasViña(
    val vigencia: String,

    /** Etiquetas de los 11 tramos de antigüedad, en orden de índice. */
    val tramosLabel: List<String>,

    /** Categorías con su básico propio por tramo. Obrero Común debe ser índice 0. */
    val categorias: List<CategoriaViña>,

    // Encargado y Capataz: % sobre el básico del Obrero Común del tramo activo
    val porcentajeEncargado: Double,
    val porcentajeCapataz: Double,

    // Sumas No Remunerativas (fijas, no varían por categoría ni tramo)
    val sumaNORemunerativa: Double,
    val refrigerio: Double,

    // Adicionales porcentuales — base siempre = básico del Obrero Común del tramo
    /** Premio Asistencia: % sobre básico Obrero Común */
    val porcentajePremioAsistencia: Double,
    /** Subsidio Sepelio: % sobre jornal del Obrero Común (basicoOC / 25) */
    val porcentajeSepelio: Double,

    // Retenciones de ley (sobre el bruto remunerativo)
    val porcentajeJubilacion: Double,
    val porcentajeLey19032: Double,
    val porcentajeObraSocial: Double,
    /** Aporte solidario: % sobre básico de la categoría del trabajador (si no afiliado) */
    val porcentajeAporteSolidario: Double,
)
