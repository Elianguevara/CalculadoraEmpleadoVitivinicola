package com.elian.calculadora_empleado_vitivinicola.model

enum class CategoriaBodega(val label: String) {
    OP_COMUN("Operario común (A)"),
    OP_AYUDANTE("Ayudante de reparto (B)"),
    OP_ESPECIALIZADO("Operario especializado (C)"),
    OP_MEDIO_OFICIAL("Medio oficial (D)"),
    OP_CALIFICADO("Operario calificado (E)"),
    CHOFER_LARGA("Chofer larga distancia (F)"),
    OFICIAL("Oficiales (G)"),
    ENCARGADO_H("Oficiales toneleros/Encargados (H)"),
    CADETE("Cadete (Q)"),
    MAESTRANZA("Maestranza (R)"),
    AUX_GRAL("Auxiliar General (S)"),
    AUX_B("Auxiliar B (T)"),
    AUX_A("Auxiliar A (U)"),
    ENCARGADO_V("Encargados de sección (V)")
}

enum class CategoriaVina(val label: String) {
    OBRERO_COMUN("Obrero Común"),
    OBRERO_ESPECIALIZADO("Obrero Especializado"),
    OBRERO_OFICIO("Obrero c/oficio"),
    TRACTORISTA("Tractorista/Topador/Chofer"),
    INJERTADOR("Injertador y Parral"),
    MECANICO("Mecánicos")
}

enum class RangoAntiguedadVina(val label: String) {
    R0_3("0 a 3 años"), R3_6("3 a 6 años"), R6_9("6 a 9 años"),
    R9_12("9 a 12 años"), R12_15("12 a 15 años"), R15_18("15 a 18 años"),
    R18_21("18 a 21 años"), R21_24("21 a 24 años"), R24_27("24 a 27 años"),
    R27_30("27 a 30 años"), R_MAS30("Más de 30 años")
}
