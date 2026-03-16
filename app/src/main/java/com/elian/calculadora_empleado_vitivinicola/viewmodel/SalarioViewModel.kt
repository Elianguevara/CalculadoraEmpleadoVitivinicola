package com.elian.calculadora_empleado_vitivinicola.viewmodel

import androidx.lifecycle.ViewModel
import com.elian.calculadora_empleado_vitivinicola.model.Convenio
import com.elian.calculadora_empleado_vitivinicola.repository.SueldosRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SalarioViewModel @Inject constructor(
    repository: SueldosRepository,
) : ViewModel() {

    private val tarifasViña   = repository.getTarifasViña()
    private val tarifasBodega = repository.getTarifasBodega()

    // Listas expuestas para poblar los dropdowns de la UI
    val categoriasViña   = tarifasViña.categorias
    val categoriasBodega = tarifasBodega.categorias
    val tramosViña       = tarifasViña.tramosLabel

    private val _form = MutableStateFlow(FormState())
    val form: StateFlow<FormState> = _form.asStateFlow()

    private val _recibo = MutableStateFlow(ReciboUiState())
    val recibo: StateFlow<ReciboUiState> = _recibo.asStateFlow()

    // ── Setters de formulario ─────────────────────────────────────────────────

    fun onConvenioChange(convenio: Convenio) =
        _form.update { it.copy(convenio = convenio) }

    fun onCategoriaViñaChange(id: String) =
        _form.update { it.copy(categoriaViñaId = id) }

    fun onTramoViñaChange(index: Int) =
        _form.update { it.copy(tramoViñaIndex = index) }

    fun onFuncionEspecialChange(funcion: FuncionEspecialViña) =
        _form.update { it.copy(funcionEspecial = funcion) }

    fun onAsistenciaChange(tiene: Boolean) =
        _form.update { it.copy(tieneAsistencia = tiene) }

    fun onCategoriaBodegaChange(id: String) =
        _form.update { it.copy(categoriaBodegaId = id) }

    fun onAniosAntiguedadChange(anios: Int) =
        _form.update { it.copy(aniosAntiguedad = anios) }

    fun onPresentismoChange(presentismo: PresentismoBodega) =
        _form.update { it.copy(presentismo = presentismo) }

    fun onTituloChange(tiene: Boolean) =
        _form.update { it.copy(tieneTitulo = tiene) }

    fun onAfiliadoChange(afiliado: Boolean) =
        _form.update { it.copy(estaAfiliado = afiliado) }

    fun onHorasExtra50Change(horas: Int) =
        _form.update { it.copy(horasExtra50 = horas) }

    fun onHorasExtra100Change(horas: Int) =
        _form.update { it.copy(horasExtra100 = horas) }

    // ── Acciones ─────────────────────────────────────────────────────────────

    fun calcular() {
        _recibo.value = when (_form.value.convenio) {
            Convenio.VIÑA   -> calcularViña(_form.value)
            Convenio.BODEGA -> calcularBodega(_form.value)
        }
    }

    fun limpiar() {
        // Preserva el convenio seleccionado; resetea todo lo demás (incl. horas extras)
        _form.update { FormState(convenio = it.convenio) }
        _recibo.value = ReciboUiState()
    }

    // ── Liquidación — Viña (CCT 154/91) ──────────────────────────────────────

    private fun calcularViña(f: FormState): ReciboUiState {
        val t = tarifasViña

        val basicoOC = t.categorias[0].basicosPorTramo[f.tramoViñaIndex]
        val jornalOC = basicoOC / 25.0

        val (basicoTrabajador, catLabel) = when (f.funcionEspecial) {
            FuncionEspecialViña.ENCARGADO ->
                basicoOC * (1 + t.porcentajeEncargado) to "Encargado"
            FuncionEspecialViña.CAPATAZ   ->
                basicoOC * (1 + t.porcentajeCapataz) to "Capataz"
            FuncionEspecialViña.NINGUNA   -> {
                val cat = t.categorias.find { it.id == f.categoriaViñaId } ?: t.categorias[0]
                cat.basicosPorTramo[f.tramoViñaIndex] to cat.nombre
            }
        }

        // ── Haberes (sin sub-items aún) ──────────────────────────────────────
        val haberes = mutableListOf<ItemRecibo>()
        haberes += ItemRecibo("Básico mensual", basicoTrabajador)
        if (f.tieneAsistencia) {
            haberes += ItemRecibo(
                "Premio Asistencia (5% OC)",
                basicoOC * t.porcentajePremioAsistencia
            )
        }

        // Horas extras — remunerativas → se incluyen en el totalBruto
        val valorHora  = basicoTrabajador / 25.0 / 8.0
        val montoHE50  = if (f.horasExtra50  > 0) valorHora * f.horasExtra50  * 1.5 else 0.0
        val montoHE100 = if (f.horasExtra100 > 0) valorHora * f.horasExtra100 * 2.0 else 0.0
        if (f.horasExtra50  > 0) haberes += ItemRecibo("H. Extra 50% (${f.horasExtra50} hs)",  montoHE50)
        if (f.horasExtra100 > 0) haberes += ItemRecibo("H. Extra 100% (${f.horasExtra100} hs)", montoHE100)

        val totalBruto = haberes.sumOf { it.monto }   // fijado antes de agregar sub-items

        // ── Retenciones ──────────────────────────────────────────────────────
        val retenciones = mutableListOf<ItemRecibo>()
        retenciones += ItemRecibo("Jubilación (11%)",       totalBruto * t.porcentajeJubilacion)
        retenciones += ItemRecibo("Ley 19032 — PAMI (3%)", totalBruto * t.porcentajeLey19032)
        retenciones += ItemRecibo("OSPAV (3%)",             totalBruto * t.porcentajeObraSocial)
        retenciones += ItemRecibo(
            "Subsidio Sepelio (40% jornal OC)",
            jornalOC * t.porcentajeSepelio
        )
        if (f.estaAfiliado) {
            retenciones += ItemRecibo(
                "Cuota Sindical (2%)",
                basicoTrabajador * t.porcentajeCuotaSindical
            )
        } else {
            retenciones += ItemRecibo(
                "Aporte Solidario (1.5%)",
                basicoTrabajador * t.porcentajeAporteSolidario
            )
        }
        val totalRetenciones = retenciones.sumOf { it.monto }

        // Sub-items informativos "neto de bolsillo" para las HE
        val tasaEfectiva = if (totalBruto > 0) totalRetenciones / totalBruto else 0.0
        if (f.horasExtra50  > 0) haberes += ItemRecibo(
            "  ↳ Neto de bolsillo HE 50%",
            montoHE50 * (1 - tasaEfectiva),
            esSubItem = true
        )
        if (f.horasExtra100 > 0) haberes += ItemRecibo(
            "  ↳ Neto de bolsillo HE 100%",
            montoHE100 * (1 - tasaEfectiva),
            esSubItem = true
        )

        // ── Sumas No Remunerativas ───────────────────────────────────────────
        val noRemunerativos = listOf(
            ItemRecibo("Suma No Remunerativa", t.sumaNORemunerativa),
            ItemRecibo("Refrigerio",           t.refrigerio),
        )
        val totalNOR = noRemunerativos.sumOf { it.monto }

        return ReciboUiState(
            vigencia            = t.vigencia,
            convenioLabel       = Convenio.VIÑA.label,
            categoriaLabel      = catLabel,
            antiguedadLabel     = t.tramosLabel.getOrElse(f.tramoViñaIndex) { "" },
            haberes             = haberes,
            totalBruto          = totalBruto,
            retenciones         = retenciones,
            totalRetenciones    = totalRetenciones,
            noRemunerativos     = noRemunerativos,
            totalNORemunerativo = totalNOR,
            sueldoNeto          = totalBruto - totalRetenciones + totalNOR,
            calculado           = true,
        )
    }

    // ── Liquidación — Bodega (CCT 85/89) ─────────────────────────────────────

    private fun calcularBodega(f: FormState): ReciboUiState {
        val t   = tarifasBodega
        val cat = t.categorias.find { it.id == f.categoriaBodegaId } ?: t.categorias[0]

        val anios               = f.aniosAntiguedad.coerceIn(0, t.aniosMaximoAntiguedad)
        val basicoConAntiguedad = cat.basicoInicial * (1 + t.porcentajeAntiguedadPorAnio * anios)

        // ── Haberes (sin sub-items aún) ──────────────────────────────────────
        val haberes = mutableListOf<ItemRecibo>()
        haberes += ItemRecibo("Básico mensual", cat.basicoInicial)
        if (anios > 0) {
            haberes += ItemRecibo(
                "Antigüedad ($anios año${if (anios == 1) "" else "s"}) (${anios}%)",
                basicoConAntiguedad - cat.basicoInicial
            )
        }
        when (f.presentismo) {
            PresentismoBodega.COMPLETO -> haberes += ItemRecibo(
                "Presentismo Completo (10%)",
                basicoConAntiguedad * t.porcentajePresentismoCompleto
            )
            PresentismoBodega.PERFECTO -> haberes += ItemRecibo(
                "Presentismo Perfecto (5%)",
                basicoConAntiguedad * t.porcentajePresentismoPerfecto
            )
            PresentismoBodega.NINGUNO  -> Unit
        }
        if (f.tieneTitulo) {
            haberes += ItemRecibo(
                "Bonificación Título Sec./Univ. (5%)",
                basicoConAntiguedad * t.porcentajeTitulo
            )
        }

        // Horas extras — remunerativas → se incluyen en el totalBruto
        val valorHora  = basicoConAntiguedad / 25.0 / 8.0
        val montoHE50  = if (f.horasExtra50  > 0) valorHora * f.horasExtra50  * 1.5 else 0.0
        val montoHE100 = if (f.horasExtra100 > 0) valorHora * f.horasExtra100 * 2.0 else 0.0
        if (f.horasExtra50  > 0) haberes += ItemRecibo("H. Extra 50% (${f.horasExtra50} hs)",  montoHE50)
        if (f.horasExtra100 > 0) haberes += ItemRecibo("H. Extra 100% (${f.horasExtra100} hs)", montoHE100)

        val totalBruto = haberes.sumOf { it.monto }   // fijado antes de agregar sub-items

        // ── Retenciones ──────────────────────────────────────────────────────
        val jornal      = basicoConAntiguedad / 25.0
        val retenciones = mutableListOf<ItemRecibo>()
        retenciones += ItemRecibo("Jubilación (11%)",       totalBruto * t.porcentajeJubilacion)
        retenciones += ItemRecibo("Ley 19032 — PAMI (3%)", totalBruto * t.porcentajeLey19032)
        retenciones += ItemRecibo("OSPAV (3%)",             totalBruto * t.porcentajeObraSocial)
        retenciones += ItemRecibo(
            "Seguro de Sepelio (40% jornal)",
            jornal * t.porcentajeSepelio
        )
        if (f.estaAfiliado) {
            retenciones += ItemRecibo(
                "Cuota Sindical (2%)",
                basicoConAntiguedad * t.porcentajeCuotaSindical
            )
        } else {
            retenciones += ItemRecibo(
                "Aporte Solidario (1.5%)",
                basicoConAntiguedad * t.porcentajeAporteSolidario
            )
        }
        val totalRetenciones = retenciones.sumOf { it.monto }

        // Sub-items informativos "neto de bolsillo" para las HE
        val tasaEfectiva = if (totalBruto > 0) totalRetenciones / totalBruto else 0.0
        if (f.horasExtra50  > 0) haberes += ItemRecibo(
            "  ↳ Neto de bolsillo HE 50%",
            montoHE50 * (1 - tasaEfectiva),
            esSubItem = true
        )
        if (f.horasExtra100 > 0) haberes += ItemRecibo(
            "  ↳ Neto de bolsillo HE 100%",
            montoHE100 * (1 - tasaEfectiva),
            esSubItem = true
        )

        // ── Sumas No Remunerativas ───────────────────────────────────────────
        val noRemunerativos = listOf(
            ItemRecibo("Suma No Remunerativa", cat.sumaNORemunerativa),
            ItemRecibo("Refrigerio",           t.refrigerioNORemunerativo),
        )
        val totalNOR = noRemunerativos.sumOf { it.monto }

        return ReciboUiState(
            vigencia            = t.vigencia,
            convenioLabel       = Convenio.BODEGA.label,
            categoriaLabel      = cat.nombre,
            antiguedadLabel     = "$anios año${if (anios == 1) "" else "s"}",
            haberes             = haberes,
            totalBruto          = totalBruto,
            retenciones         = retenciones,
            totalRetenciones    = totalRetenciones,
            noRemunerativos     = noRemunerativos,
            totalNORemunerativo = totalNOR,
            sueldoNeto          = totalBruto - totalRetenciones + totalNOR,
            calculado           = true,
        )
    }
}
