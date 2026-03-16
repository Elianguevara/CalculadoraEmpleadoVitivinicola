package com.elian.calculadora_empleado_vitivinicola.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.elian.calculadora_empleado_vitivinicola.R
import com.elian.calculadora_empleado_vitivinicola.model.Convenio
import com.elian.calculadora_empleado_vitivinicola.ui.theme.CalculadoraSalarioTheme
import com.elian.calculadora_empleado_vitivinicola.viewmodel.FormState
import com.elian.calculadora_empleado_vitivinicola.viewmodel.FuncionEspecialViña
import com.elian.calculadora_empleado_vitivinicola.viewmodel.ItemRecibo
import com.elian.calculadora_empleado_vitivinicola.viewmodel.PresentismoBodega
import com.elian.calculadora_empleado_vitivinicola.viewmodel.ReciboUiState
import com.elian.calculadora_empleado_vitivinicola.viewmodel.SalarioViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

fun formatCurrency(value: Double): String =
    NumberFormat.getCurrencyInstance(Locale("es", "AR")).format(value)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalarioScreen(viewModel: SalarioViewModel = hiltViewModel()) {
    val form   by viewModel.form.collectAsState()
    val recibo by viewModel.recibo.collectAsState()

    val scrollState      = rememberScrollState()
    val snackbarState    = remember { SnackbarHostState() }
    val scope            = rememberCoroutineScope()

    CalculadoraSalarioTheme {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarState) },
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = stringResource(R.string.app_title),
                                style = MaterialTheme.typography.titleLarge,
                            )
                            Text(
                                text = stringResource(R.string.app_subtitle),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f))
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // ── Selector de Convenio ─────────────────────────────────────
                ConvenioSelector(
                    selected = form.convenio,
                    onSelect = {
                        viewModel.onConvenioChange(it)
                        viewModel.limpiar()
                    }
                )

                // ── Formulario de inputs ─────────────────────────────────────
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    shape     = MaterialTheme.shapes.extraLarge,
                    colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier            = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Datos del trabajador",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )

                        when (form.convenio) {
                            Convenio.VIÑA   -> ViñaForm(form, viewModel)
                            Convenio.BODEGA -> BodegaForm(form, viewModel)
                        }

                        // Switch compartido: afiliado al sindicato
                        SwitchRow(
                            label   = "Afiliado/a al sindicato",
                            checked = form.estaAfiliado,
                            onCheckedChange = viewModel::onAfiliadoChange
                        )

                        // Botones
                        Row(
                            modifier            = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick   = { viewModel.calcular() },
                                modifier  = Modifier.weight(1f).height(52.dp),
                                shape     = MaterialTheme.shapes.large,
                            ) {
                                Text("Calcular")
                            }
                            OutlinedButton(
                                onClick  = {
                                    viewModel.limpiar()
                                    scope.launch { snackbarState.showSnackbar("Campos restablecidos") }
                                },
                                modifier = Modifier.weight(1f).height(52.dp),
                                shape    = MaterialTheme.shapes.large,
                            ) {
                                Text("Limpiar")
                            }
                        }
                    }
                }

                // ── Recibo de sueldo ─────────────────────────────────────────
                AnimatedVisibility(
                    visible = recibo.calculado,
                    enter   = fadeIn() + expandVertically()
                ) {
                    ReciboCard(recibo)
                }

                Text(
                    text     = stringResource(R.string.author_name),
                    style    = MaterialTheme.typography.labelMedium,
                    color    = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 8.dp, bottom = 12.dp)
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Selector Viña / Bodega
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConvenioSelector(selected: Convenio, onSelect: (Convenio) -> Unit) {
    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        Convenio.entries.forEachIndexed { index, convenio ->
            SegmentedButton(
                selected = selected == convenio,
                onClick  = { onSelect(convenio) },
                shape    = SegmentedButtonDefaults.itemShape(index, Convenio.entries.size),
                label    = { Text(convenio.label, maxLines = 1) }
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Formulario — Viña
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ViñaForm(form: FormState, viewModel: SalarioViewModel) {
    // Categoría
    SimpleDropdown(
        label       = "Categoría",
        opciones    = viewModel.categoriasViña.map { it.nombre },
        seleccionado = viewModel.categoriasViña
            .indexOfFirst { it.id == form.categoriaViñaId }
            .coerceAtLeast(0),
        onSelect    = { viewModel.onCategoriaViñaChange(viewModel.categoriasViña[it].id) },
        enabled     = form.funcionEspecial == FuncionEspecialViña.NINGUNA
    )

    // Función especial (Encargado / Capataz)
    SimpleDropdown(
        label        = "Función especial",
        opciones     = FuncionEspecialViña.entries.map { it.label },
        seleccionado = FuncionEspecialViña.entries.indexOf(form.funcionEspecial),
        onSelect     = { viewModel.onFuncionEspecialChange(FuncionEspecialViña.entries[it]) }
    )

    // Tramo de antigüedad
    SimpleDropdown(
        label        = "Antigüedad",
        opciones     = viewModel.tramosViña,
        seleccionado = form.tramoViñaIndex,
        onSelect     = viewModel::onTramoViñaChange
    )

    // Premio asistencia
    SwitchRow(
        label           = "Premio Asistencia (5% OC)",
        checked         = form.tieneAsistencia,
        onCheckedChange = viewModel::onAsistenciaChange
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Formulario — Bodega
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BodegaForm(form: FormState, viewModel: SalarioViewModel) {
    // Categoría
    SimpleDropdown(
        label        = "Categoría",
        opciones     = viewModel.categoriasBodega.map { it.nombre },
        seleccionado = viewModel.categoriasBodega
            .indexOfFirst { it.id == form.categoriaBodegaId }
            .coerceAtLeast(0),
        onSelect     = { viewModel.onCategoriaBodegaChange(viewModel.categoriasBodega[it].id) }
    )

    // Años de antigüedad — campo numérico (fuente de verdad: ViewModel)
    OutlinedTextField(
        value         = if (form.aniosAntiguedad == 0) "" else form.aniosAntiguedad.toString(),
        onValueChange = {
            val anios = it.filter(Char::isDigit).toIntOrNull()?.coerceIn(0, 30) ?: 0
            viewModel.onAniosAntiguedadChange(anios)
        },
        label           = { Text("Años de antigüedad (0 – 30)") },
        placeholder     = { Text("0") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine      = true,
        modifier        = Modifier.fillMaxWidth(),
    )

    // Presentismo
    SimpleDropdown(
        label        = "Presentismo",
        opciones     = PresentismoBodega.entries.map { it.label },
        seleccionado = PresentismoBodega.entries.indexOf(form.presentismo),
        onSelect     = { viewModel.onPresentismoChange(PresentismoBodega.entries[it]) }
    )

    // Título
    SwitchRow(
        label           = "Título secundario / universitario (5%)",
        checked         = form.tieneTitulo,
        onCheckedChange = viewModel::onTituloChange
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Card del recibo de sueldo
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ReciboCard(recibo: ReciboUiState) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape     = MaterialTheme.shapes.extraLarge,
        colors    = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        )
    ) {
        Column(
            modifier            = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Encabezado del recibo
            Text(
                "Liquidación de sueldo",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text("Vigencia: ${recibo.vigencia}",   style = MaterialTheme.typography.bodySmall)
            Text("Convenio: ${recibo.convenioLabel}", style = MaterialTheme.typography.bodySmall)
            Text("Categoría: ${recibo.categoriaLabel}", style = MaterialTheme.typography.bodySmall)
            Text("Antigüedad: ${recibo.antiguedadLabel}", style = MaterialTheme.typography.bodySmall)

            Spacer(Modifier.height(4.dp))

            // ── HABERES ──────────────────────────────────────────────────────
            SeccionRecibo(titulo = "HABERES REMUNERATIVOS")
            recibo.haberes.forEach { ItemReciboRow(it) }
            TotalRow(
                label  = "Sueldo Bruto",
                monto  = recibo.totalBruto,
                color  = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(8.dp))

            // ── RETENCIONES ──────────────────────────────────────────────────
            SeccionRecibo(titulo = "DESCUENTOS")
            recibo.retenciones.forEach {
                ItemReciboRow(it, montoColor = MaterialTheme.colorScheme.error)
            }
            TotalRow(
                label  = "Total descuentos",
                monto  = -recibo.totalRetenciones,
                color  = MaterialTheme.colorScheme.error
            )

            Spacer(Modifier.height(8.dp))

            // ── NO REMUNERATIVO ──────────────────────────────────────────────
            SeccionRecibo(titulo = "SUMAS NO REMUNERATIVAS")
            recibo.noRemunerativos.forEach { ItemReciboRow(it) }
            TotalRow(
                label  = "Total no remunerativo",
                monto  = recibo.totalNORemunerativo,
                color  = MaterialTheme.colorScheme.secondary
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // ── SUELDO NETO ──────────────────────────────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    "SUELDO NETO",
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text       = formatCurrency(recibo.sueldoNeto),
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color      = MaterialTheme.colorScheme.primary,
                    textAlign  = TextAlign.End
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Componentes auxiliares
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SimpleDropdown(
    label: String,
    opciones: List<String>,
    seleccionado: Int,
    onSelect: (Int) -> Unit,
    enabled: Boolean = true,
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded          = expanded,
        onExpandedChange  = { if (enabled) expanded = !expanded },
        modifier          = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value          = opciones.getOrElse(seleccionado) { "" },
            onValueChange  = {},
            readOnly       = true,
            enabled        = enabled,
            label          = { Text(label) },
            trailingIcon   = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier       = Modifier.fillMaxWidth().menuAnchor(),
            colors         = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary
            )
        )
        ExposedDropdownMenu(
            expanded         = expanded,
            onDismissRequest = { expanded = false },
            modifier         = Modifier.heightIn(max = 300.dp)
        ) {
            opciones.forEachIndexed { index, texto ->
                DropdownMenuItem(
                    text    = { Text(texto) },
                    onClick = { onSelect(index); expanded = false }
                )
                if (index < opciones.lastIndex) HorizontalDivider()
            }
        }
    }
}

@Composable
private fun SwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun SeccionRecibo(titulo: String) {
    Text(
        text     = titulo,
        style    = MaterialTheme.typography.labelMedium,
        color    = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
    )
}

@Composable
private fun ItemReciboRow(
    item: ItemRecibo,
    montoColor: androidx.compose.ui.graphics.Color = LocalContentColor.current,
) {
    Row(
        modifier              = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text     = item.descripcion,
            style    = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f)
        )
        Text(
            text      = formatCurrency(item.monto),
            style     = MaterialTheme.typography.bodySmall,
            color     = montoColor,
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun TotalRow(label: String, monto: Double, color: androidx.compose.ui.graphics.Color) {
    Row(
        modifier              = Modifier.fillMaxWidth().padding(top = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        Text(
            text       = formatCurrency(monto),
            style      = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color      = color,
            textAlign  = TextAlign.End
        )
    }
}
