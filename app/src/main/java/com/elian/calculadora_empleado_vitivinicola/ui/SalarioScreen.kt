package com.elian.calculadora_empleado_vitivinicola.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.elian.calculadora_empleado_vitivinicola.R
import com.elian.calculadora_empleado_vitivinicola.model.categorias
import com.elian.calculadora_empleado_vitivinicola.model.escalasAntiguedad
import com.elian.calculadora_empleado_vitivinicola.ui.theme.CalculadoraSalarioTheme
import com.elian.calculadora_empleado_vitivinicola.viewmodel.SalarioViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

// 🔹 Formateo de moneda
fun formatCurrency(value: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "AR"))
    return format.format(value)
}

// 🔹 Filtro numérico
fun filterDigits(text: String): String = text.filter { it.isDigit() }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalarioScreen(viewModel: SalarioViewModel = viewModel()) {
    var categoriaSeleccionada by remember { mutableStateOf(categorias.first()) }
    var antiguedadIndexSeleccionado by remember { mutableIntStateOf(0) }
    var horasExtra100Text by remember { mutableStateOf("") }
    var horasExtra50Text by remember { mutableStateOf("") }
    var expandedCategoria by remember { mutableStateOf(false) }
    var expandedAntiguedad by remember { mutableStateOf(false) }

    val horas100Validas = horasExtra100Text.all { it.isDigit() }
    val horas50Validas = horasExtra50Text.all { it.isDigit() }
    val canCalculate = horas100Validas && horas50Validas
    val salarioInfo by viewModel.salarioBreakdown.collectAsState()
    val scrollState = rememberScrollState()

    // 🔹 Control del Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    CalculadoraSalarioTheme {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = stringResource(R.string.app_title),
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = stringResource(R.string.app_subtitle),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f))
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // --- CARD DE ENTRADA ---
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            stringResource(R.string.section_inputs),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )

                        // Categoría
                        ExposedDropdownMenuBox(
                            expanded = expandedCategoria,
                            onExpandedChange = { expandedCategoria = !expandedCategoria },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = categoriaSeleccionada.nombre,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(stringResource(R.string.label_category)) },
                                leadingIcon = { Icon(Icons.Default.Work, contentDescription = null) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoria) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = expandedCategoria,
                                onDismissRequest = { expandedCategoria = false },
                                modifier = Modifier.heightIn(max = 280.dp)
                            ) {
                                categorias.forEachIndexed { index, categoria ->
                                    DropdownMenuItem(
                                        text = { Text(categoria.nombre) },
                                        onClick = {
                                            categoriaSeleccionada = categoria
                                            expandedCategoria = false
                                        }
                                    )
                                    if (index < categorias.lastIndex) HorizontalDivider()
                                }
                            }
                        }

                        // Antigüedad
                        ExposedDropdownMenuBox(
                            expanded = expandedAntiguedad,
                            onExpandedChange = { expandedAntiguedad = !expandedAntiguedad },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = stringResource(R.string.years_template, antiguedadIndexSeleccionado * 3),
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(stringResource(R.string.label_seniority)) },
                                leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedAntiguedad) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = expandedAntiguedad,
                                onDismissRequest = { expandedAntiguedad = false },
                                modifier = Modifier.heightIn(max = 280.dp)
                            ) {
                                escalasAntiguedad.indices.forEach { index ->
                                    DropdownMenuItem(
                                        text = { Text(stringResource(R.string.years_template, index * 3)) },
                                        onClick = {
                                            antiguedadIndexSeleccionado = index
                                            expandedAntiguedad = false
                                        }
                                    )
                                    if (index < escalasAntiguedad.indices.last) HorizontalDivider()
                                }
                            }
                        }

                        OutlinedTextField(
                            value = horasExtra100Text,
                            onValueChange = { horasExtra100Text = filterDigits(it) },
                            label = { Text(stringResource(R.string.label_overtime_100)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            isError = !horas100Validas && horasExtra100Text.isNotEmpty()
                        )

                        OutlinedTextField(
                            value = horasExtra50Text,
                            onValueChange = { horasExtra50Text = filterDigits(it) },
                            label = { Text(stringResource(R.string.label_overtime_50)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            isError = !horas50Validas && horasExtra50Text.isNotEmpty()
                        )

                        // 🔹 Botones de acción
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = {
                                    val horasExtra100 = horasExtra100Text.toIntOrNull() ?: 0
                                    val horasExtra50 = horasExtra50Text.toIntOrNull() ?: 0
                                    viewModel.calcularSalario(
                                        categoria = categoriaSeleccionada,
                                        antiguedadIndex = antiguedadIndexSeleccionado,
                                        horasExtra100 = horasExtra100,
                                        horasExtra50 = horasExtra50
                                    )
                                },
                                enabled = canCalculate,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp),
                                shape = MaterialTheme.shapes.large,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (canCalculate)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Text(stringResource(R.string.button_calculate))
                            }

                            OutlinedButton(
                                onClick = {
                                    categoriaSeleccionada = categorias.first()
                                    antiguedadIndexSeleccionado = 0
                                    horasExtra100Text = ""
                                    horasExtra50Text = ""
                                    viewModel.calcularSalario(categoriaSeleccionada, 0, 0, 0)
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Campos restablecidos correctamente")
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp),
                                shape = MaterialTheme.shapes.large,
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text("Limpiar")
                            }
                        }
                    }
                }

                // --- CARD DE RESULTADOS ---
                AnimatedVisibility(visible = salarioInfo.salarioFinalNeto > 0.0) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest),
                        shape = MaterialTheme.shapes.extraLarge
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                stringResource(R.string.section_results),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                            BreakdownItem(stringResource(R.string.result_base_salary), salarioInfo.salarioBaseCalculado)
                            BreakdownItem(stringResource(R.string.result_seniority_bonus), salarioInfo.adicionalAntiguedad)
                            BreakdownItem(stringResource(R.string.result_presenteeism), salarioInfo.adicionalPresentismo)
                            BreakdownItem(stringResource(R.string.result_gross_subtotal), salarioInfo.subtotalBrutoRemunerativo, isSubtotal = true)

                            Spacer(modifier = Modifier.height(8.dp))
                            BreakdownItem(stringResource(R.string.result_deduction_solidarity), -salarioInfo.descuentoAporteSolidario)
                            BreakdownItem(stringResource(R.string.result_deduction_law), -salarioInfo.descuentoJubilacionLey)
                            BreakdownItem(stringResource(R.string.result_deduction_funeral), -salarioInfo.descuentoSepelio)
                            BreakdownItem(stringResource(R.string.result_net_rem_subtotal), salarioInfo.subtotalNetoRemunerativo, isSubtotal = true)

                            Spacer(modifier = Modifier.height(8.dp))
                            BreakdownItem(stringResource(R.string.result_non_remunerative), salarioInfo.adicionalNoRemunerativo)
                            BreakdownItem(stringResource(R.string.result_meal_allowance), salarioInfo.adicionalRefrigerio)
                            BreakdownItem(stringResource(R.string.result_overtime_50), salarioInfo.pagoExtra50)
                            BreakdownItem("  ↳ de bolsillo", salarioInfo.pagoExtra50Neto)
                            BreakdownItem(stringResource(R.string.result_overtime_100), salarioInfo.pagoExtra100)
                            BreakdownItem("  ↳ de bolsillo", salarioInfo.pagoExtra100Neto)

                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(stringResource(R.string.result_final_net), style = MaterialTheme.typography.titleMedium)
                                Text(
                                    text = formatCurrency(salarioInfo.salarioFinalNeto),
                                    style = MaterialTheme.typography.titleMedium,
                                    textAlign = TextAlign.End
                                )
                            }
                        }
                    }
                }

                Text(
                    text = stringResource(R.string.author_name),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 8.dp, bottom = 12.dp)
                )
            }
        }
    }
}

@Composable
fun BreakdownItem(label: String, value: Double, isSubtotal: Boolean = false) {
    val textStyle = if (isSubtotal) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.bodyMedium
    val valueColor = if (value < 0) MaterialTheme.colorScheme.error else LocalContentColor.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = textStyle)
        Text(
            text = formatCurrency(value),
            style = textStyle,
            color = valueColor,
            textAlign = TextAlign.End
        )
    }
}
