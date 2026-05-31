package com.elian.calculadora_empleado_vitivinicola.viewmodel

import com.elian.calculadora_empleado_vitivinicola.model.*

data class FormState(
    val convenio: Convenio = Convenio.BODEGA,
    val periodo: Periodo = Periodo.MAR_ABR,
    // Bodega
    val catBodega: CategoriaBodega = CategoriaBodega.OP_COMUN,
    val antiguedadBodega: String = "0",
    val presentismoCompleto: Boolean = true,
    val presentismoPerfecto: Boolean = false,
    val tieneTitulo: Boolean = false,
    val manejoDinero: Boolean = false,
    val herramientasPropias: Boolean = false,
    // Viña
    val catVina: CategoriaVina = CategoriaVina.OBRERO_COMUN,
    val rangoVina: RangoAntiguedadVina = RangoAntiguedadVina.R0_3,
    val esEncargado: Boolean = false,
    val esCapataz: Boolean = false,
    val tieneAsistenciaVina: Boolean = true
)

data class ItemRecibo(
    val descripcion: String,
    val monto: Double,
    val tipo: TipoConcepto = TipoConcepto.REMUNERATIVO // Reuse the one in logic or keep this
)

enum class TipoConcepto { REMUNERATIVO, NO_REMUNERATIVO, DESCUENTO }

data class ReciboUiState(
    val neto: Double = 0.0,
    val haberes: List<ItemRecibo> = emptyList(),
    val noRemunerativos: List<ItemRecibo> = emptyList(),
    val retenciones: List<ItemRecibo> = emptyList(),
    val calculado: Boolean = false
)
