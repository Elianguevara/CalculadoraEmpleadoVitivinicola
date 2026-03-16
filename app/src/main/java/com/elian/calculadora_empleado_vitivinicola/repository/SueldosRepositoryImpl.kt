package com.elian.calculadora_empleado_vitivinicola.repository

import com.elian.calculadora_empleado_vitivinicola.model.CategoriaBodega
import com.elian.calculadora_empleado_vitivinicola.model.CategoriaViña
import com.elian.calculadora_empleado_vitivinicola.model.TarifasBodega
import com.elian.calculadora_empleado_vitivinicola.model.TarifasViña
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fuente de verdad de los montos salariales.
 * Para actualizar por paritaria: solo editar los valores numéricos de este archivo.
 * La lógica de cálculo vive en el ViewModel y no debe tocarse.
 */
@Singleton
class SueldosRepositoryImpl @Inject constructor() : SueldosRepository {

    // ─────────────────────────────────────────────────────────────────────────
    // VIÑA — CCT 154/91 — Vigencia: Febrero 2026
    // ─────────────────────────────────────────────────────────────────────────
    override fun getTarifasViña() = TarifasViña(
        vigencia = "Febrero 2026",
        tramosLabel = listOf(
            "0 a 3 años",   "3 a 6 años",   "6 a 9 años",   "9 a 12 años",
            "12 a 15 años", "15 a 18 años", "18 a 21 años", "21 a 24 años",
            "24 a 27 años", "27 a 30 años", "Más de 30 años"
        ),
        categorias = listOf(
            // ⚠ Obrero Común SIEMPRE en índice 0 — es la base de referencia.
            CategoriaViña(
                id = "obrero_comun",
                nombre = "Obrero Común",
                basicosPorTramo = listOf(
                    416_735.0, 427_154.0, 437_572.0, 447_991.0,
                    458_409.0, 468_827.0, 479_246.0, 489_664.0,
                    500_082.0, 510_501.0, 520_919.0
                )
            ),
            CategoriaViña(
                id = "obrero_especializado",
                nombre = "Obrero Especializado",
                basicosPorTramo = listOf(
                    437_572.0, 448_512.0, 459_451.0, 470_390.0,
                    481_329.0, 492_269.0, 503_208.0, 514_147.0,
                    525_087.0, 536_026.0, 546_965.0
                )
            ),
            CategoriaViña(
                id = "tractorista",
                nombre = "Tractorista / Top. / Chofer",
                basicosPorTramo = listOf(
                    479_246.0, 491_227.0, 503_208.0, 515_189.0,
                    527_170.0, 539_151.0, 551_133.0, 563_114.0,
                    575_095.0, 587_076.0, 599_057.0
                )
            ),
            CategoriaViña(
                id = "mecanico",
                nombre = "Mecánico",
                basicosPorTramo = listOf(
                    520_919.0, 533_942.0, 546_965.0, 559_988.0,
                    573_011.0, 586_034.0, 599_057.0, 612_080.0,
                    625_103.0, 638_126.0, 651_149.0
                )
            ),
        ),
        porcentajeEncargado        = 0.30,
        porcentajeCapataz          = 0.35,
        sumaNORemunerativa         = 179_375.0,
        refrigerio                 = 143_001.0,
        porcentajePremioAsistencia = 0.05,
        porcentajeSepelio          = 0.40,
        porcentajeJubilacion       = 0.11,
        porcentajeLey19032         = 0.03,
        porcentajeObraSocial       = 0.03,
        porcentajeAporteSolidario  = 0.015,   // no afiliado
        porcentajeCuotaSindical    = 0.02,    // afiliado
    )

    // ─────────────────────────────────────────────────────────────────────────
    // BODEGA — CCT 85/89 — Vigencia: Febrero 2026
    // ─────────────────────────────────────────────────────────────────────────
    override fun getTarifasBodega() = TarifasBodega(
        vigencia = "Febrero 2026",
        categorias = listOf(
            // ── Página 1: Operarios Base ──────────────────────────────────────
            CategoriaBodega(
                id = "operario_comun",
                nombre = "Operario Común",
                basicoInicial      = 502_540.0,
                sumaNORemunerativa = 166_501.0,
            ),
            CategoriaBodega(
                id = "operario_especializado",
                nombre = "Operario Especializado",
                basicoInicial      = 522_642.0,
                sumaNORemunerativa = 173_162.0,
            ),
            CategoriaBodega(
                id = "medio_oficial",
                nombre = "1/2 Oficial",
                basicoInicial      = 552_794.0,
                sumaNORemunerativa = 183_152.0,
            ),
            CategoriaBodega(
                id = "ayudante_reparto",
                nombre = "Ayudante de Reparto",
                basicoInicial      = 572_896.0,
                sumaNORemunerativa = 189_812.0,
            ),

            // ── Página 2: Especializados, Choferes y Oficiales ───────────────
            CategoriaBodega(
                id = "operario_calificado_chofer_corta",
                nombre = "Operario Calificado / Chofer corta y media / Clarkista",
                basicoInicial      = 592_998.0,
                sumaNORemunerativa = 196_472.0,
            ),
            CategoriaBodega(
                id = "chofer_larga_distancia",
                nombre = "Chofer Larga Distancia / 1/2 Oficial Tonelero",
                basicoInicial      = 613_098.0,
                sumaNORemunerativa = 203_132.0,
            ),
            CategoriaBodega(
                id = "oficiales_mecanicos",
                nombre = "Oficiales / Mecánicos Tetrabrick / Destiladores",
                basicoInicial      = 633_201.0,
                sumaNORemunerativa = 209_792.0,
            ),
            CategoriaBodega(
                id = "oficiales_toneleros_encargados",
                nombre = "Oficiales Toneleros / Encargados de Sección",
                basicoInicial      = 653_302.0,
                sumaNORemunerativa = 216_452.0,
            ),

            // ── Página 3: Cadetes y Maestranza ───────────────────────────────
            CategoriaBodega(
                id = "cadete",
                nombre = "Cadete",
                basicoInicial      = 502_540.0,
                sumaNORemunerativa = 166_501.0,
            ),
            CategoriaBodega(
                id = "maestranza",
                nombre = "Maestranza",
                basicoInicial      = 522_642.0,
                sumaNORemunerativa = 173_162.0,
            ),
            CategoriaBodega(
                id = "auxiliar_general",
                nombre = "Auxiliar General",
                basicoInicial      = 577_921.0,
                sumaNORemunerativa = 191_477.0,
            ),

            // ── Página 4: Auxiliares y Administrativos ───────────────────────
            CategoriaBodega(
                id = "auxiliar_b",
                nombre = "Auxiliar 'B'",
                basicoInicial      = 603_048.0,
                sumaNORemunerativa = 199_802.0,
            ),
            CategoriaBodega(
                id = "auxiliar_a",
                nombre = "Auxiliar 'A'",
                basicoInicial      = 633_201.0,
                sumaNORemunerativa = 209_792.0,
            ),
            CategoriaBodega(
                id = "encargado_seccion_admin",
                nombre = "Encargado de Sección (Administrativo)",
                basicoInicial      = 653_302.0,
                sumaNORemunerativa = 216_452.0,
            ),
        ),
        porcentajeAntiguedadPorAnio   = 0.01,
        aniosMaximoAntiguedad         = 30,
        refrigerioNORemunerativo      = 169_851.0,
        porcentajePresentismoCompleto = 0.10,
        porcentajePresentismoPerfecto = 0.05,
        porcentajeTitulo              = 0.05,
        porcentajeSepelio             = 0.40,
        porcentajeJubilacion          = 0.11,
        porcentajeLey19032            = 0.03,
        porcentajeObraSocial          = 0.03,
        porcentajeAporteSolidario     = 0.015,  // no afiliado
        porcentajeCuotaSindical       = 0.02,   // afiliado
    )
}
