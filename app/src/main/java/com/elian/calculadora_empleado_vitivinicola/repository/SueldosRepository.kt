package com.elian.calculadora_empleado_vitivinicola.repository

import com.elian.calculadora_empleado_vitivinicola.model.TarifasBodega
import com.elian.calculadora_empleado_vitivinicola.model.TarifasViña

interface SueldosRepository {
    fun getTarifasViña(): TarifasViña
    fun getTarifasBodega(): TarifasBodega
}
