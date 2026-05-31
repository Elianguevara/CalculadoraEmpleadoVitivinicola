package com.elian.calculadora_empleado_vitivinicola.logic

import com.elian.calculadora_empleado_vitivinicola.viewmodel.ItemRecibo
import com.elian.calculadora_empleado_vitivinicola.model.CategoriaBodega
import java.math.BigDecimal
import java.math.RoundingMode

class BodegaCalculator {
    fun calculate(input: BodegaInput): CalculationResult {
        val basic = BigDecimal(input.tarifas.basicos[input.categoria] ?: 525154.0)
        
        // Antigüedad: 1% de 1 a 25 años; 0,5% de 26 a 30 años.
        val years = input.aniosAntiguedad.coerceIn(0, 30)
        val antiguedadAmount = if (years <= 25) {
            basic.multiply(BigDecimal(years)).multiply(BigDecimal("0.01"))
        } else {
            val firstPeriod = basic.multiply(BigDecimal("0.25")) // 25 * 0.01
            val secondPeriod = basic.multiply(BigDecimal(years - 25)).multiply(BigDecimal("0.005"))
            firstPeriod.add(secondPeriod)
        }

        val itemsHaberes = mutableListOf<ItemRecibo>()
        itemsHaberes.add(ItemRecibo("Sueldo Básico", basic.toDouble()))
        
        if (antiguedadAmount.compareTo(BigDecimal.ZERO) > 0) {
            itemsHaberes.add(ItemRecibo("Antigüedad ($years años)", antiguedadAmount.setScale(2, RoundingMode.HALF_UP).toDouble()))
        }

        // Adicionales Remunerativos (sobre el Básico)
        if (input.presentismoCompleto) {
            val amount = basic.multiply(BigDecimal("0.10"))
            itemsHaberes.add(ItemRecibo("Presentismo Completo (10%)", amount.setScale(2, RoundingMode.HALF_UP).toDouble()))
        }
        if (input.presentismoPerfecto) {
            val amount = basic.multiply(BigDecimal("0.05"))
            itemsHaberes.add(ItemRecibo("Presentismo Perfecto (5%)", amount.setScale(2, RoundingMode.HALF_UP).toDouble()))
        }
        if (input.tieneTitulo) {
            val amount = basic.multiply(BigDecimal("0.05"))
            itemsHaberes.add(ItemRecibo("Título (5%)", amount.setScale(2, RoundingMode.HALF_UP).toDouble()))
        }
        if (input.manejoDinero) {
            val amount = basic.multiply(BigDecimal("0.05"))
            itemsHaberes.add(ItemRecibo("Manejo de Dinero (5%)", amount.setScale(2, RoundingMode.HALF_UP).toDouble()))
        }
        if (input.herramientasPropias) {
            val amount = basic.multiply(BigDecimal("0.10"))
            itemsHaberes.add(ItemRecibo("Herramientas Propias (10%)", amount.setScale(2, RoundingMode.HALF_UP).toDouble()))
        }

        val brutoRemunerativo = itemsHaberes.fold(BigDecimal.ZERO) { acc, item -> acc.add(BigDecimal(item.monto)) }

        // Descuentos de Ley
        // Jubilación (11%), Ley 19032 (3%), Obra Social (3%) sobre el Sueldo Bruto
        val jubilacion = brutoRemunerativo.multiply(BigDecimal("0.11"))
        val ley19032 = brutoRemunerativo.multiply(BigDecimal("0.03"))
        val obraSocial = brutoRemunerativo.multiply(BigDecimal("0.03"))
        
        // Aporte Solidario / Sindicato: 1.5% únicamente sobre el Básico de convenio
        val sindicato = basic.multiply(BigDecimal("0.015"))
        
        val sepelio = BigDecimal(input.tarifas.retencionSepelio)

        val itemsDescuentos = listOf(
            ItemRecibo("Jubilación (11%)", jubilacion.setScale(2, RoundingMode.HALF_UP).toDouble()),
            ItemRecibo("Ley 19032 (3%)", ley19032.setScale(2, RoundingMode.HALF_UP).toDouble()),
            ItemRecibo("Obra Social (3%)", obraSocial.setScale(2, RoundingMode.HALF_UP).toDouble()),
            ItemRecibo("Sindicato (1.5% s/básico)", sindicato.setScale(2, RoundingMode.HALF_UP).toDouble()),
            ItemRecibo("Retención Sepelio", sepelio.toDouble())
        )
        val totalDescuentos = itemsDescuentos.fold(BigDecimal.ZERO) { acc, item -> acc.add(BigDecimal(item.monto)) }

        // No Remunerativos
        val asigNoRem = BigDecimal(input.tarifas.asignacionesNoRem[input.categoria] ?: 173993.0)
        val refrigerio = BigDecimal(input.tarifas.refrigerio)
        
        val itemsNoRemunerativos = listOf(
            ItemRecibo("Asignación No Rem. Extraordinaria", asigNoRem.toDouble()),
            ItemRecibo("Refrigerio (No Rem)", refrigerio.toDouble())
        )
        val totalNoRemunerativo = itemsNoRemunerativos.fold(BigDecimal.ZERO) { acc, item -> acc.add(BigDecimal(item.monto)) }

        val neto = brutoRemunerativo.subtract(totalDescuentos).add(totalNoRemunerativo)

        return CalculationResult(
            brutoRemunerativo = brutoRemunerativo.toDouble(),
            totalNoRemunerativo = totalNoRemunerativo.toDouble(),
            totalDescuentos = totalDescuentos.toDouble(),
            neto = neto.setScale(2, RoundingMode.HALF_UP).toDouble(),
            itemsHaberes = itemsHaberes,
            itemsDescuentos = itemsDescuentos,
            itemsNoRemunerativos = itemsNoRemunerativos
        )
    }
}
