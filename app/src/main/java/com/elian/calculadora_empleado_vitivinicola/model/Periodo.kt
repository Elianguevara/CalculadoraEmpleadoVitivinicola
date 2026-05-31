package com.elian.calculadora_empleado_vitivinicola.model

import java.util.Calendar

enum class Periodo(val label: String) {
    MAR_ABR("Marzo - Abril 2026"),
    MAY_JUN("Mayo - Junio 2026"),
    JUL_AGO("Julio - Agosto 2026");

    companion object {
        fun current(): Periodo {
            val month = Calendar.getInstance().get(Calendar.MONTH)
            return when (month) {
                Calendar.MARCH, Calendar.APRIL -> MAR_ABR
                Calendar.MAY, Calendar.JUNE -> MAY_JUN
                Calendar.JULY, Calendar.AUGUST -> JUL_AGO
                else -> MAR_ABR // Default
            }
        }
    }
}
