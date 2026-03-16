package com.elian.calculadora_empleado_vitivinicola.viewmodel

import androidx.lifecycle.ViewModel
import com.elian.calculadora_empleado_vitivinicola.model.Categoria
import com.elian.calculadora_empleado_vitivinicola.model.escalasAntiguedad
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.math.max

data class SalarioBreakdown(
    val salarioBaseCalculado: Double = 0.0,
    val adicionalPresentismo: Double = 0.0,
    val adicionalAntiguedad: Double = 0.0,
    val subtotalBrutoRemunerativo: Double = 0.0,
    val descuentoSepelio: Double = 0.0,
    val descuentoAporteSolidario: Double = 0.0,
    val descuentoJubilacionLey: Double = 0.0,
    val subtotalNetoRemunerativo: Double = 0.0,
    val adicionalNoRemunerativo: Double = 0.0,
    val adicionalRefrigerio: Double = 0.0,
    val pagoExtra50: Double = 0.0,
    val pagoExtra100: Double = 0.0,
    val salarioFinalNeto: Double = 0.0,
    val pagoExtra50Neto: Double = 0.0,
    val pagoExtra100Neto: Double = 0.0
)

@HiltViewModel
class SalarioViewModel @Inject constructor() : ViewModel() {

    companion object {
        const val SALARIO_BASE_OFICIAL = 401009.0
        const val ADICIONAL_NO_REMUNERATIVO = 172776.0
        const val REFRIGERIO = 137604.0

        const val PRESENTISMO_PORCENTAJE = 0.05
        const val APORTE_SOLIDARIO_PORCENTAJE = 0.015

        const val DESCUENTO_JUBILACION_PORCENTAJE = 0.11
        const val DESCUENTO_LEY_19032_PORCENTAJE = 0.03
        const val DESCUENTO_OBRA_SOCIAL_PORCENTAJE = 0.03
        const val TOTAL_DESCUENTOS_LEY_PORCENTAJE =
            DESCUENTO_JUBILACION_PORCENTAJE + DESCUENTO_LEY_19032_PORCENTAJE + DESCUENTO_OBRA_SOCIAL_PORCENTAJE

        const val DIAS_MES_JORNAL = 25.0
        const val HORAS_JORNAL = 8.0
        const val FACTOR_EXTRA_50 = 1.5
        const val FACTOR_EXTRA_100 = 2.0
        const val SUBSIDIO_SEPELIO_PORCENTAJE = 0.4
    }

    private val _salarioBreakdown = MutableStateFlow(SalarioBreakdown())
    val salarioBreakdown: StateFlow<SalarioBreakdown> = _salarioBreakdown.asStateFlow()

    fun calcularSalario(
        categoria: Categoria,
        antiguedadIndex: Int,
        horasExtra100: Int,
        horasExtra50: Int
    ) {
        val factorAntiguedad = escalasAntiguedad.getOrNull(antiguedadIndex) ?: 1.0
        val salarioBasicoCategoria = SALARIO_BASE_OFICIAL * categoria.factor
        val baseConAntiguedad = salarioBasicoCategoria * factorAntiguedad
        val adicionalAntiguedadMonto = baseConAntiguedad - salarioBasicoCategoria
        val adicionalPresentismoMonto = SALARIO_BASE_OFICIAL * PRESENTISMO_PORCENTAJE

        val jornal = salarioBasicoCategoria / DIAS_MES_JORNAL
        val valorHoraOrdinaria = jornal / HORAS_JORNAL
        val pagoExtra50Monto = valorHoraOrdinaria * horasExtra50 * FACTOR_EXTRA_50
        val pagoExtra100Monto = valorHoraOrdinaria * horasExtra100 * FACTOR_EXTRA_100
        val pagoExtra50NetoMonto = pagoExtra50Monto * (1 - TOTAL_DESCUENTOS_LEY_PORCENTAJE)
        val pagoExtra100NetoMonto = pagoExtra100Monto * (1 - TOTAL_DESCUENTOS_LEY_PORCENTAJE)

        val subtotalBrutoRemunerativo = baseConAntiguedad +
                adicionalPresentismoMonto +
                pagoExtra50Monto +
                pagoExtra100Monto

        val descuentoSepelioMonto = (SALARIO_BASE_OFICIAL / DIAS_MES_JORNAL) * SUBSIDIO_SEPELIO_PORCENTAJE
        val aporteSolidarioMonto = salarioBasicoCategoria * APORTE_SOLIDARIO_PORCENTAJE
        val descuentoJubilacionMonto = subtotalBrutoRemunerativo * DESCUENTO_JUBILACION_PORCENTAJE
        val descuentoLey19032Monto = subtotalBrutoRemunerativo * DESCUENTO_LEY_19032_PORCENTAJE
        val descuentoObraSocialMonto = subtotalBrutoRemunerativo * DESCUENTO_OBRA_SOCIAL_PORCENTAJE

        val totalDescuentosRemunerativos = descuentoJubilacionMonto +
                descuentoLey19032Monto +
                descuentoObraSocialMonto +
                aporteSolidarioMonto

        val subtotalNetoRemunerativo = subtotalBrutoRemunerativo - totalDescuentosRemunerativos
        val salarioFinalNetoCalculado = subtotalNetoRemunerativo +
                ADICIONAL_NO_REMUNERATIVO +
                REFRIGERIO -
                descuentoSepelioMonto

        _salarioBreakdown.value = SalarioBreakdown(
            salarioBaseCalculado = baseConAntiguedad,
            adicionalPresentismo = adicionalPresentismoMonto,
            adicionalAntiguedad = adicionalAntiguedadMonto,
            subtotalBrutoRemunerativo = subtotalBrutoRemunerativo,
            descuentoSepelio = descuentoSepelioMonto,
            descuentoAporteSolidario = aporteSolidarioMonto,
            descuentoJubilacionLey = descuentoJubilacionMonto + descuentoLey19032Monto + descuentoObraSocialMonto,
            subtotalNetoRemunerativo = subtotalNetoRemunerativo,
            adicionalNoRemunerativo = ADICIONAL_NO_REMUNERATIVO,
            adicionalRefrigerio = REFRIGERIO,
            pagoExtra50 = pagoExtra50Monto,
            pagoExtra100 = pagoExtra100Monto,
            salarioFinalNeto = max(0.0, salarioFinalNetoCalculado),
            pagoExtra50Neto = pagoExtra50NetoMonto,
            pagoExtra100Neto = pagoExtra100NetoMonto
        )
    }
}
