package com.elian.calculadora_empleado_vitivinicola.repository

import com.elian.calculadora_empleado_vitivinicola.model.Periodo
import com.elian.calculadora_empleado_vitivinicola.model.TarifasBodega
import com.elian.calculadora_empleado_vitivinicola.model.TarifasVina

interface SueldosRepository {
    fun getTarifasBodega(periodo: Periodo): TarifasBodega
    fun getTarifasVina(periodo: Periodo): TarifasVina
    fun getBasicoVina(categoria: com.elian.calculadora_empleado_vitivinicola.model.CategoriaVina, rango: com.elian.calculadora_empleado_vitivinicola.model.RangoAntiguedadVina, periodo: Periodo): Double
}
