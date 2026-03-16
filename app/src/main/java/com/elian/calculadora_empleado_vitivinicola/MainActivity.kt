package com.elian.calculadora_empleado_vitivinicola

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.elian.calculadora_empleado_vitivinicola.ui.SalarioScreen
import com.elian.calculadora_empleado_vitivinicola.ui.theme.CalculadoraSalarioTheme



import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculadoraSalarioTheme {
                SalarioScreen()
            }
        }
    }
}