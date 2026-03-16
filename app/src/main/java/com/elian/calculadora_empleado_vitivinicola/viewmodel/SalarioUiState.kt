package com.elian.calculadora_empleado_vitivinicola.viewmodel

import com.elian.calculadora_empleado_vitivinicola.model.Convenio

enum class PresentismoBodega(val label: String) {
    NINGUNO("Sin presentismo"),
    COMPLETO("Completo (10%)"),
    PERFECTO("Perfecto (5%)"),
}

enum class FuncionEspecialViña(val label: String) {
    NINGUNA("Sin función especial"),
    ENCARGADO("Encargado (+30%)"),
    CAPATAZ("Capataz (+35%)"),
}

data class FormState(
    val convenio: Convenio = Convenio.VIÑA,
    // ── Viña ──────────────────────────────────────
    val categoriaViñaId: String = "obrero_comun",
    val tramoViñaIndex: Int = 0,
    val funcionEspecial: FuncionEspecialViña = FuncionEspecialViña.NINGUNA,
    val tieneAsistencia: Boolean = true,
    // ── Bodega ─────────────────────────────────────
    val categoriaBodegaId: String = "operario_comun",
    val aniosAntiguedad: Int = 0,
    val presentismo: PresentismoBodega = PresentismoBodega.NINGUNO,
    val tieneTitulo: Boolean = false,
    // ── Compartido ─────────────────────────────────
    val estaAfiliado: Boolean = false,
    // ── Horas extras (remunerativas) ───────────────
    val horasExtra50: Int = 0,
    val horasExtra100: Int = 0,
)

/**
 * Un ítem de la liquidación.
 * [esSubItem] = true indica una fila informativa (ej. "↳ Neto de bolsillo")
 * que NO contribuye al total de su sección.
 */
data class ItemRecibo(
    val descripcion: String,
    val monto: Double,
    val esSubItem: Boolean = false,
)

/** Estado completo del recibo generado para la UI. */
data class ReciboUiState(
    val vigencia: String = "",
    val convenioLabel: String = "",
    val categoriaLabel: String = "",
    val antiguedadLabel: String = "",
    // Sección haberes remunerativos (incluye sub-items informativos de HE)
    val haberes: List<ItemRecibo> = emptyList(),
    val totalBruto: Double = 0.0,
    // Sección descuentos / retenciones
    val retenciones: List<ItemRecibo> = emptyList(),
    val totalRetenciones: Double = 0.0,
    // Sección sumas no remunerativas
    val noRemunerativos: List<ItemRecibo> = emptyList(),
    val totalNORemunerativo: Double = 0.0,
    // Resultado final
    val sueldoNeto: Double = 0.0,
    val calculado: Boolean = false,
)
