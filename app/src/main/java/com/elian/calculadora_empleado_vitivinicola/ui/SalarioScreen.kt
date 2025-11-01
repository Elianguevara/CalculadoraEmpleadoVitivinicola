package com.elian.calculadora_empleado_vitivinicola.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.elian.calculadora_empleado_vitivinicola.model.categorias
import com.elian.calculadora_empleado_vitivinicola.model.escalasAntiguedad
import com.elian.calculadora_empleado_vitivinicola.viewmodel.SalarioViewModel
import com.elian.calculadora_empleado_vitivinicola.ui.theme.CalculadoraSalarioTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalarioScreen(viewModel: SalarioViewModel = viewModel()) {
    var categoriaSeleccionada by remember { mutableStateOf(categorias.first()) }
    var antiguedadSeleccionada by remember { mutableStateOf(0) }
    var horasExtra100Text by remember { mutableStateOf("") }
    var horasExtra50Text by remember { mutableStateOf("") }

    val salario by viewModel.salarioCalculado.collectAsState()

    CalculadoraSalarioTheme {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Calculadora de Salario", style = MaterialTheme.typography.titleLarge) }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // ComboBox Categoría
                var expandedCategoria by remember { mutableStateOf(false) }

                OutlinedTextField(
                    value = categoriaSeleccionada.nombre,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Selecciona Categoría") },
                    trailingIcon = {
                        IconButton(onClick = { expandedCategoria = !expandedCategoria }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Abrir menú")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                DropdownMenu(
                    expanded = expandedCategoria,
                    onDismissRequest = { expandedCategoria = false }
                ) {
                    categorias.forEach { categoria ->
                        DropdownMenuItem(
                            text = { Text(categoria.nombre) },
                            onClick = {
                                categoriaSeleccionada = categoria
                                expandedCategoria = false
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ComboBox Antigüedad
                var expandedAntiguedad by remember { mutableStateOf(false) }

                OutlinedTextField(
                    value = "${antiguedadSeleccionada * 3} años",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Selecciona Antigüedad") },
                    trailingIcon = {
                        IconButton(onClick = { expandedAntiguedad = !expandedAntiguedad }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Abrir menú")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                DropdownMenu(
                    expanded = expandedAntiguedad,
                    onDismissRequest = { expandedAntiguedad = false }
                ) {
                    escalasAntiguedad.indices.forEach { index ->
                        DropdownMenuItem(
                            text = { Text("${index * 3} años") },
                            onClick = {
                                antiguedadSeleccionada = index
                                expandedAntiguedad = false
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Campo para Horas Extras al 100%
                OutlinedTextField(
                    value = horasExtra100Text,
                    onValueChange = { horasExtra100Text = it },
                    label = { Text("Horas Extras al 100%") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Campo para Horas Extras al 50%
                OutlinedTextField(
                    value = horasExtra50Text,
                    onValueChange = { horasExtra50Text = it },
                    label = { Text("Horas Extras al 50%") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Botón calcular
                Button(
                    onClick = {
                        val horasExtra100 = horasExtra100Text.toIntOrNull() ?: 0
                        val horasExtra50 = horasExtra50Text.toIntOrNull() ?: 0
                        viewModel.calcularSalario(
                            categoria = categoriaSeleccionada,
                            antiguedadIndex = antiguedadSeleccionada,
                            horasExtra100 = horasExtra100,
                            horasExtra50 = horasExtra50
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Calcular Salario")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Mostrar el resultado del salario calculado
                Text(
                    "Salario Calculado: $${String.format("%,.2f", salario)}",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
