package com.elian.calculadora_empleado_vitivinicola.viewmodel

import androidx.lifecycle.ViewModel
import com.elian.calculadora_empleado_vitivinicola.model.Categoria
import com.elian.calculadora_empleado_vitivinicola.model.escalasAntiguedad
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SalarioViewModel @Inject constructor() : ViewModel() {

    // Salario base oficial
    private val salarioBase = 351023.0

    // Presentismo (se aplica siempre por defecto)
    private val presentismoPorcentaje = 0.05

    // Deducciones fijas
    private val descuentoBasePorcentaje = 0.016  // 1.6% del salario base
    private val descuentoFinal = 0.17           // 17% sobre el subtotal

    // Adicionales fijos
    private val adicionalNoRemunerativo = 147300.0
    private val refrigerio = 120452.0

    private val _salarioCalculado = MutableStateFlow(0.0)
    val salarioCalculado: StateFlow<Double> = _salarioCalculado.asStateFlow()

    // Nota: Se mantiene el cálculo de presentismo en el código, aunque ya no se muestre en la UI.
    private val _presentismoAplicado = MutableStateFlow(true)
    val presentismoAplicado: StateFlow<Boolean> = _presentismoAplicado.asStateFlow()

    /**
     * Calcula el salario final.
     *
     * @param categoria La categoría del empleado.
     * @param antiguedadIndex Índice de antigüedad según la lista escalasAntiguedad.
     * @param horasExtra100 Cantidad de horas extras al 100% (ingresadas manualmente, por defecto 0).
     * @param horasExtra50 Cantidad de horas extras al 50% (ingresadas manualmente, por defecto 0).
     */
    fun calcularSalario(
        categoria: Categoria,
        antiguedadIndex: Int,
        horasExtra100: Int = 0,
        horasExtra50: Int = 0
    ) {
        // 1) Factor de antigüedad
        val factorAntiguedad = escalasAntiguedad.getOrNull(antiguedadIndex) ?: 1.0

        // 2) Cálculo inicial: Porción de categoría + presentismo (siempre se suma)
        var salaryFinal = salarioBase * categoria.factor * factorAntiguedad +
                salarioBase * presentismoPorcentaje

        // 3) Restar 1.6% del salario base
        salaryFinal -= salarioBase * descuentoBasePorcentaje

        // 4) Restar 17% del resultado
        salaryFinal *= (1 - descuentoFinal)  // salaryFinal = salaryFinal * 0.83

        // 5) Sumar los adicionales fijos
        salaryFinal += adicionalNoRemunerativo + refrigerio

        // 6) Calcular valor del jornal y el valor de la hora
        val jornal = (salarioBase * categoria.factor) / 25.0
        val valorHora = jornal / 8.0

        // 7) Calcular pagos por horas extras
        val pagoExtra100 = valorHora * horasExtra100     // Horas extras al 100% (se paga 100% extra por hora)
        val pagoExtra50 = valorHora * horasExtra50 * 0.5   // Horas extras al 50% (se paga 50% extra por hora)

        // 8) Sumar los pagos extras al salario final
        salaryFinal += (pagoExtra100 + pagoExtra50)

        // Emitir el valor final
        _salarioCalculado.value = salaryFinal
        _presentismoAplicado.value = true  // Siempre se aplica el presentismo
    }
}
