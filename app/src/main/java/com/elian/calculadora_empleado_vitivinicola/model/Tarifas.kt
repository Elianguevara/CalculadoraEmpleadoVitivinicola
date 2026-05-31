package com.elian.calculadora_empleado_vitivinicola.model

data class TarifasBodega(
    val basicos: Map<CategoriaBodega, Double>,
    val asignacionesNoRem: Map<CategoriaBodega, Double>,
    val refrigerio: Double,
    val retencionSepelio: Double
)

data class TarifasVina(
    val asignacionNoRem: Double,
    val refrigerio: Double,
    val retencionSepelio: Double,
    val premioAsistencia: Double
)
