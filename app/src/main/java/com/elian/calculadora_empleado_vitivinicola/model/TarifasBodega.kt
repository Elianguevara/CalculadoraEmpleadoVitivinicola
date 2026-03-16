package com.elian.calculadora_empleado_vitivinicola.model

/**
 * Categoría de Bodega (CCT 85/89).
 * La [sumaNORemunerativa] es fija por categoría y NO escala con la antigüedad.
 */
data class CategoriaBodega(
    val id: String,
    val nombre: String,
    val basicoInicial: Double,
    val sumaNORemunerativa: Double,
)

/**
 * Todos los montos del convenio Bodega (CCT 85/89) para una vigencia.
 * Para actualizar por paritaria: solo editar los valores en [SueldosRepositoryImpl].
 *
 * La antigüedad se computa como regla derivada:
 *   basicoConAntiguedad = basicoInicial × (1 + porcentajeAntiguedadPorAnio × años)
 * No se necesita tabla — dos parámetros cubren todos los casos.
 */
data class TarifasBodega(
    val vigencia: String,
    val categorias: List<CategoriaBodega>,

    /** 1% de incremento por cada año de antigüedad sobre el básico inicial. */
    val porcentajeAntiguedadPorAnio: Double,
    /** Tope máximo de cómputo de antigüedad (30 años). */
    val aniosMaximoAntiguedad: Int,

    // Refrigerio No Remunerativo (igual para todas las categorías)
    val refrigerioNORemunerativo: Double,

    // Adicionales porcentuales — base = básico con antigüedad de la categoría
    val porcentajePresentismoCompleto: Double,
    val porcentajePresentismoPerfecto: Double,
    val porcentajeTitulo: Double,
    /** Seguro de Sepelio: % sobre jornal del trabajador (basicoConAntiguedad / 25) */
    val porcentajeSepelio: Double,

    // Retenciones de ley (sobre el bruto remunerativo)
    val porcentajeJubilacion: Double,
    val porcentajeLey19032: Double,
    val porcentajeObraSocial: Double,
    val porcentajeAporteSolidario: Double,
)
