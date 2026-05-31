package com.elian.calculadora_empleado_vitivinicola

import com.elian.calculadora_empleado_vitivinicola.logic.*
import com.elian.calculadora_empleado_vitivinicola.model.*
import org.junit.Assert.assertEquals
import org.junit.Test

class SalaryCalculationTest {

    @Test
    fun `test bodega calculation march 2026 - basic case`() {
        val calculator = BodegaCalculator()
        val tarifas = TarifasBodega(
            basicos = mapOf(CategoriaBodega.OP_COMUN to 525154.0),
            asignacionesNoRem = mapOf(CategoriaBodega.OP_COMUN to 173993.0),
            refrigerio = 177492.0,
            retencionSepelio = 14026.0
        )
        val input = BodegaInput(
            categoria = CategoriaBodega.OP_COMUN,
            aniosAntiguedad = 0,
            presentismoCompleto = true,
            presentismoPerfecto = true,
            tieneTitulo = false,
            manejoDinero = false,
            herramientasPropias = false,
            tarifas = tarifas
        )

        val result = calculator.calculate(input)

        // Bruto = 525154 (Basico) + 0 (Antiguedad) + 52515.4 (10% Pres) + 26257.7 (5% Pres) = 603927.1
        val expectedBruto = 525154.0 * (1 + 0.10 + 0.05)
        assertEquals(expectedBruto, result.brutoRemunerativo, 0.1)
    }

    @Test
    fun `test bodega seniority calculation`() {
        val calculator = BodegaCalculator()
        val basic = 100000.0
        val tarifas = TarifasBodega(
            basicos = mapOf(CategoriaBodega.OP_COMUN to basic),
            asignacionesNoRem = mapOf(CategoriaBodega.OP_COMUN to 0.0),
            refrigerio = 0.0,
            retencionSepelio = 0.0
        )
        
        // 20 years = 20%
        val input20 = BodegaInput(CategoriaBodega.OP_COMUN, 20, false, false, false, false, false, tarifas)
        val result20 = calculator.calculate(input20)
        assertEquals(basic * 0.20, result20.itemsHaberes.find { it.descripcion.contains("Antigüedad") }?.monto ?: 0.0, 0.1)

        // 28 years = 25% + 1.5% = 26.5%
        val input28 = BodegaInput(CategoriaBodega.OP_COMUN, 28, false, false, false, false, false, tarifas)
        val result28 = calculator.calculate(input28)
        assertEquals(basic * 0.265, result28.itemsHaberes.find { it.descripcion.contains("Antigüedad") }?.monto ?: 0.0, 0.1)
    }
}
