package com.elian.calculadora_empleado_vitivinicola.logic

import com.elian.calculadora_empleado_vitivinicola.model.*
import com.elian.calculadora_empleado_vitivinicola.viewmodel.ItemRecibo

data class BodegaInput(
    val categoria: CategoriaBodega,
    val aniosAntiguedad: Int,
    val presentismoCompleto: Boolean,
    val presentismoPerfecto: Boolean,
    val tieneTitulo: Boolean,
    val manejoDinero: Boolean,
    val herramientasPropias: Boolean,
    val tarifas: TarifasBodega
)

data class VinaInput(
    val categoria: CategoriaVina,
    val rangoAntiguedad: RangoAntiguedadVina,
    val esEncargado: Boolean,
    val esCapataz: Boolean,
    val tieneAsistencia: Boolean,
    val basico: Double,
    val tarifas: TarifasVina
)

data class CalculationResult(
    val brutoRemunerativo: Double,
    val totalNoRemunerativo: Double,
    val totalDescuentos: Double,
    val neto: Double,
    val itemsHaberes: List<ItemRecibo>,
    val itemsDescuentos: List<ItemRecibo>,
    val itemsNoRemunerativos: List<ItemRecibo>
)

interface SalaryCalculator {
    // We will use specialized methods or a common input if possible, but let's keep it simple for now
}
