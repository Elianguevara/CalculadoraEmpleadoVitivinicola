package com.elian.calculadora_empleado_vitivinicola.ui

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.elian.calculadora_empleado_vitivinicola.model.*
import com.elian.calculadora_empleado_vitivinicola.viewmodel.*
import java.text.NumberFormat
import java.util.Locale

private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "AR"))
private fun formatCurrency(value: Double): String = currencyFormat.format(value)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalarioScreen(viewModel: SalarioViewModel = hiltViewModel()) {
    val form by viewModel.form.collectAsState()
    val recibo by viewModel.recibo.collectAsState()
    val scrollState = rememberScrollState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("CALCULO SALARIAL 2026", fontWeight = FontWeight.Black) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Selector de Convenio
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                Convenio.entries.forEachIndexed { index, convenio ->
                    SegmentedButton(
                        selected = form.convenio == convenio,
                        onClick = { viewModel.onConvenioChange(convenio) },
                        shape = SegmentedButtonDefaults.itemShape(index, Convenio.entries.size),
                        label = { Text(convenio.name, fontWeight = FontWeight.Bold, fontSize = 16.sp) }
                    )
                }
            }

            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Configuración", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    
                    PeriodoDropdown(form.periodo, viewModel::onPeriodoChange)

                    if (form.convenio == Convenio.BODEGA) {
                        CategoryBodegaDropdown(form.catBodega, viewModel::onCatBodegaChange)
                        
                        OutlinedTextField(
                            value = form.antiguedadBodega,
                            onValueChange = viewModel::onAntiguedadBodegaChange,
                            label = { Text("Años de Antigüedad", fontSize = 18.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text("Adicionales Remunerativos", fontWeight = FontWeight.Bold)
                        CheckboxRow("Presentismo Completo (10%)", form.presentismoCompleto, viewModel::onPresentismoCompletoChange)
                        CheckboxRow("Presentismo Perfecto (5%)", form.presentismoPerfecto, viewModel::onPresentismoPerfectoChange)
                        CheckboxRow("Título (5%)", form.tieneTitulo, viewModel::onTieneTituloChange)
                        CheckboxRow("Manejo de Dinero (5%)", form.manejoDinero, viewModel::onManejoDineroChange)
                        CheckboxRow("Herramientas Propias (10%)", form.herramientasPropias, viewModel::onHerramientasPropiasChange)
                    } else {
                        CategoryVinaDropdown(form.catVina, viewModel::onCatVinaChange)
                        RangoVinaDropdown(form.rangoVina, viewModel::onRangoVinaChange)
                        
                        Text("Jerarquías", fontWeight = FontWeight.Bold)
                        CheckboxRow("Es Encargado (+30%)", form.esEncargado, viewModel::onEsEncargadoChange)
                        CheckboxRow("Es Capataz (+35%)", form.esCapataz, viewModel::onEsCapatazChange)
                        CheckboxRow("Premio Asistencia", form.tieneAsistenciaVina, viewModel::onAsistenciaVinaChange)
                    }

                    Button(
                        onClick = { 
                            viewModel.calcular()
                            keyboardController?.hide()
                            focusManager.clearFocus()
                        },
                        modifier = Modifier.fillMaxWidth().height(60.dp),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) {
                        Text("CALCULAR", fontSize = 20.sp, fontWeight = FontWeight.Black)
                    }
                }
            }

            AnimatedVisibility(visible = recibo.calculado) {
                ReciboCard(recibo)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PeriodoDropdown(selected: Periodo, onSelect: (Periodo) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selected.label, onValueChange = {}, readOnly = true,
            label = { Text("Periodo Bimestral") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            Periodo.entries.forEach { p ->
                DropdownMenuItem(text = { Text(p.label) }, onClick = { onSelect(p); expanded = false })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryBodegaDropdown(selected: CategoriaBodega, onSelect: (CategoriaBodega) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selected.label, onValueChange = {}, readOnly = true,
            label = { Text("Categoría Bodega") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            CategoriaBodega.entries.forEach { c ->
                DropdownMenuItem(text = { Text(c.label) }, onClick = { onSelect(c); expanded = false })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryVinaDropdown(selected: CategoriaVina, onSelect: (CategoriaVina) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selected.label, onValueChange = {}, readOnly = true,
            label = { Text("Categoría Viña") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            CategoriaVina.entries.forEach { c ->
                DropdownMenuItem(text = { Text(c.label) }, onClick = { onSelect(c); expanded = false })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RangoVinaDropdown(selected: RangoAntiguedadVina, onSelect: (RangoAntiguedadVina) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selected.label, onValueChange = {}, readOnly = true,
            label = { Text("Antigüedad (Trienios)") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            RangoAntiguedadVina.entries.forEach { r ->
                DropdownMenuItem(text = { Text(r.label) }, onClick = { onSelect(r); expanded = false })
            }
        }
    }
}

@Composable
private fun CheckboxRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange, modifier = Modifier.scale(1.3f))
        Spacer(Modifier.width(8.dp))
        Text(label, fontSize = 16.sp)
    }
}

@Composable
private fun ReciboCard(recibo: ReciboUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(2.dp, Color.Black)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("DETALLE DE COBRO ESTIMADO", fontWeight = FontWeight.Black, fontSize = 18.sp)
            HorizontalDivider(thickness = 2.dp, color = Color.Black, modifier = Modifier.padding(vertical = 8.dp))
            
            SectionRecibo("REMUNERATIVOS", recibo.haberes, Color(0xFF2E7D32))
            SectionRecibo("NO REMUNERATIVOS", recibo.noRemunerativos, Color(0xFF1976D2))
            SectionRecibo("DESCUENTOS", recibo.retenciones, Color.Red)

            Spacer(Modifier.height(16.dp))
            Surface(color = Color.Black, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("NETO A COBRAR", color = Color.White, fontSize = 14.sp)
                    Text(formatCurrency(recibo.neto), color = Color.Yellow, fontSize = 32.sp, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
private fun SectionRecibo(title: String, items: List<ItemRecibo>, color: Color) {
    if (items.isEmpty()) return
    Text(title, fontWeight = FontWeight.Bold, color = color, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp))
    items.forEach { item ->
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(item.descripcion, fontSize = 14.sp, modifier = Modifier.weight(1f))
            Text(formatCurrency(item.monto), fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SalarioScreenPreview() {
    SalarioScreen()
}
