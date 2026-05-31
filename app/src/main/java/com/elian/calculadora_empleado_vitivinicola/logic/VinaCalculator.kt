package com.elian.calculadora_empleado_vitivinicola.logic

import com.elian.calculadora_empleado_vitivinicola.viewmodel.ItemRecibo
import com.elian.calculadora_empleado_vitivinicola.model.*
import java.math.BigDecimal
import java.math.RoundingMode

class VinaCalculator {
    fun calculate(input: VinaInput, basicObreroComun: Double): CalculationResult {
        val basic = BigDecimal(input.basico)
        val basicOC = BigDecimal(basicObreroComun)
        
        val itemsHaberes = mutableListOf<ItemRecibo>()
        itemsHaberes.add(ItemRecibo("Sueldo Básico (Categoría + Rango)", basic.toDouble()))
        
        // Adicional por Jerarquía (Encargado/Capataz)
        // Se calcula estrictamente sobre el sueldo establecido para el obrero común de la misma antigüedad.
        if (input.esEncargado) {
            val amount = basicOC.multiply(BigDecimal("0.30"))
            itemsHaberes.add(ItemRecibo("Adicional Encargado (30% s/OC)", amount.setScale(2, RoundingMode.HALF_UP).toDouble()))
        }
        if (input.esCapataz) {
            val amount = basicOC.multiply(BigDecimal("0.35"))
            itemsHaberes.add(ItemRecibo("Adicional Capataz (35% s/OC)", amount.setScale(2, RoundingMode.HALF_UP).toDouble()))
        }

        // Premio Mensual por Asistencia (Monto Fijo)
        if (input.tieneAsistencia) {
            itemsHaberes.add(ItemRecibo("Premio Asistencia (Fijo)", input.tarifas.premioAsistencia))
        }

        val brutoRemunerativo = itemsHaberes.fold(BigDecimal.ZERO) { acc, item -> acc.add(BigDecimal(item.monto)) }

        // Descuentos
        val jubilacion = brutoRemunerativo.multiply(BigDecimal("0.11"))
        val ley19032 = brutoRemunerativo.multiply(BigDecimal("0.03"))
        val obraSocial = brutoRemunerativo.multiply(BigDecimal("0.03"))
        
        // Sindicato: 1.5% únicamente sobre el Básico de convenio de la categoría
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
        val itemsNoRemunerativos = listOf(
            ItemRecibo("Asignación No Rem. Viña", input.tarifas.asignacionNoRem),
            ItemRecibo("Refrigerio (No Rem)", input.tarifas.refrigerio)
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
