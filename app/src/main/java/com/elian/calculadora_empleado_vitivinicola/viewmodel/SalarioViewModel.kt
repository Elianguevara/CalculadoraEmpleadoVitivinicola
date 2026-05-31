package com.elian.calculadora_empleado_vitivinicola.viewmodel

import androidx.lifecycle.ViewModel
import com.elian.calculadora_empleado_vitivinicola.logic.*
import com.elian.calculadora_empleado_vitivinicola.model.*
import com.elian.calculadora_empleado_vitivinicola.repository.SueldosRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SalarioViewModel @Inject constructor(
    private val repository: SueldosRepository,
) : ViewModel() {

    private val _form = MutableStateFlow(FormState(periodo = Periodo.current()))
    val form: StateFlow<FormState> = _form.asStateFlow()

    private val _recibo = MutableStateFlow(ReciboUiState())
    val recibo: StateFlow<ReciboUiState> = _recibo.asStateFlow()

    // --- Setters ---
    fun onConvenioChange(v: Convenio) = _form.update { it.copy(convenio = v) }
    fun onPeriodoChange(v: Periodo) = _form.update { it.copy(periodo = v) }
    fun onCatBodegaChange(v: CategoriaBodega) = _form.update { it.copy(catBodega = v) }
    fun onAntiguedadBodegaChange(v: String) = _form.update { it.copy(antiguedadBodega = v) }
    fun onPresentismoCompletoChange(v: Boolean) = _form.update { it.copy(presentismoCompleto = v) }
    fun onPresentismoPerfectoChange(v: Boolean) = _form.update { it.copy(presentismoPerfecto = v) }
    fun onTieneTituloChange(v: Boolean) = _form.update { it.copy(tieneTitulo = v) }
    fun onManejoDineroChange(v: Boolean) = _form.update { it.copy(manejoDinero = v) }
    fun onHerramientasPropiasChange(v: Boolean) = _form.update { it.copy(herramientasPropias = v) }
    
    fun onCatVinaChange(v: CategoriaVina) = _form.update { it.copy(catVina = v) }
    fun onRangoVinaChange(v: RangoAntiguedadVina) = _form.update { it.copy(rangoVina = v) }
    fun onEsEncargadoChange(v: Boolean) = _form.update { it.copy(esEncargado = v) }
    fun onEsCapatazChange(v: Boolean) = _form.update { it.copy(esCapataz = v) }
    fun onAsistenciaVinaChange(v: Boolean) = _form.update { it.copy(tieneAsistenciaVina = v) }

    fun calcular() {
        val f = _form.value
        val result = if (f.convenio == Convenio.BODEGA) {
            val calc = BodegaCalculator()
            calc.calculate(BodegaInput(
                categoria = f.catBodega,
                aniosAntiguedad = f.antiguedadBodega.toIntOrNull() ?: 0,
                presentismoCompleto = f.presentismoCompleto,
                presentismoPerfecto = f.presentismoPerfecto,
                tieneTitulo = f.tieneTitulo,
                manejoDinero = f.manejoDinero,
                herramientasPropias = f.herramientasPropias,
                tarifas = repository.getTarifasBodega(f.periodo)
            ))
        } else {
            val calc = VinaCalculator()
            val basicoCat = repository.getBasicoVina(f.catVina, f.rangoVina, f.periodo)
            val basicoObreroComun = repository.getBasicoVina(CategoriaVina.OBRERO_COMUN, f.rangoVina, f.periodo)
            
            calc.calculate(
                input = VinaInput(
                    categoria = f.catVina,
                    rangoAntiguedad = f.rangoVina,
                    esEncargado = f.esEncargado,
                    esCapataz = f.esCapataz,
                    tieneAsistencia = f.tieneAsistenciaVina,
                    basico = basicoCat,
                    tarifas = repository.getTarifasVina(f.periodo)
                ),
                basicObreroComun = basicoObreroComun
            )
        }

        _recibo.value = ReciboUiState(
            neto = result.neto,
            haberes = result.itemsHaberes,
            noRemunerativos = result.itemsNoRemunerativos,
            retenciones = result.itemsDescuentos,
            calculado = true
        )
    }

    fun limpiar() {
        _form.update { FormState(convenio = it.convenio, periodo = Periodo.current()) }
        _recibo.value = ReciboUiState()
    }
}
