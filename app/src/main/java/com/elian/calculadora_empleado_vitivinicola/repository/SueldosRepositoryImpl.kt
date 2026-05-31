package com.elian.calculadora_empleado_vitivinicola.repository

import com.elian.calculadora_empleado_vitivinicola.model.CategoriaBodega
import com.elian.calculadora_empleado_vitivinicola.model.CategoriaVina
import com.elian.calculadora_empleado_vitivinicola.model.Periodo
import com.elian.calculadora_empleado_vitivinicola.model.RangoAntiguedadVina
import com.elian.calculadora_empleado_vitivinicola.model.TarifasBodega
import com.elian.calculadora_empleado_vitivinicola.model.TarifasVina
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SueldosRepositoryImpl @Inject constructor() : SueldosRepository {

    override fun getTarifasBodega(periodo: Periodo): TarifasBodega {
        return when (periodo) {
            Periodo.MAR_ABR -> TarifasBodega(
                basicos = mapOf(
                    CategoriaBodega.OP_COMUN to 525154.0,
                    CategoriaBodega.OP_AYUDANTE to 546220.0,
                    CategoriaBodega.CADETE to 525154.0
                ).withDefault { 525154.0 },
                asignacionesNoRem = mapOf(
                    CategoriaBodega.OP_COMUN to 173993.0,
                    CategoriaBodega.OP_AYUDANTE to 180953.0
                ).withDefault { 173993.0 },
                refrigerio = 177492.0,
                retencionSepelio = 14026.0
            )
            Periodo.MAY_JUN -> TarifasBodega(
                basicos = mapOf(
                    CategoriaBodega.OP_COMUN to 547769.0
                ).withDefault { 547769.0 },
                asignacionesNoRem = mapOf(
                    CategoriaBodega.OP_COMUN to 181485.0
                ).withDefault { 181485.0 },
                refrigerio = 185135.0,
                retencionSepelio = 14630.0
            )
            Periodo.JUL_AGO -> TarifasBodega(
                basicos = mapOf(
                    CategoriaBodega.OP_COMUN to 570383.0
                ).withDefault { 570383.0 },
                asignacionesNoRem = mapOf(
                    CategoriaBodega.OP_COMUN to 188978.0
                ).withDefault { 188978.0 },
                refrigerio = 192779.0,
                retencionSepelio = 15234.0
            )
        }
    }

    override fun getTarifasVina(periodo: Periodo): TarifasVina {
        return when (periodo) {
            Periodo.MAR_ABR -> TarifasVina(
                asignacionNoRem = 185654.0,
                refrigerio = 148006.0,
                retencionSepelio = 12240.0,
                premioAsistencia = 21567.0
            )
            Periodo.MAY_JUN -> TarifasVina(
                asignacionNoRem = 191932.0,
                refrigerio = 153011.0,
                retencionSepelio = 12654.0,
                premioAsistencia = 22296.0
            )
            Periodo.JUL_AGO -> TarifasVina(
                asignacionNoRem = 198210.0,
                refrigerio = 158016.0,
                retencionSepelio = 13068.0,
                premioAsistencia = 23025.0
            )
        }
    }

    override fun getBasicoVina(categoria: CategoriaVina, rango: RangoAntiguedadVina, periodo: Periodo): Double {
        val base = when (periodo) {
            Periodo.MAR_ABR -> 431321.0
            Periodo.MAY_JUN -> 445907.0
            Periodo.JUL_AGO -> 460493.0
        }
        
        val factorRango = 1.0 + (rango.ordinal * 0.025)
        val factorCat = when(categoria) {
            CategoriaVina.OBRERO_COMUN -> 1.0
            CategoriaVina.MECANICO -> 1.20
            CategoriaVina.TRACTORISTA -> 1.15
            else -> 1.05
        }
        
        return base * factorRango * factorCat
    }
}
