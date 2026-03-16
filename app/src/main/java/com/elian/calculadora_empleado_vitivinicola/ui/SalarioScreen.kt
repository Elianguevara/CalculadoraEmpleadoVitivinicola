package com.elian.calculadora_empleado_vitivinicola.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
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

// ─────────────────────────────────────────────────────────────────────────────
// Pantalla principal
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalarioScreen(viewModel: SalarioViewModel = hiltViewModel()) {
    val form   by viewModel.form.collectAsState()
    val recibo by viewModel.recibo.collectAsState()

    val scrollState   = rememberScrollState()
    val snackbarState = remember { SnackbarHostState() }
    val scope         = rememberCoroutineScope()

    CalculadoraSalarioTheme {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarState) },
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text  = stringResource(R.string.app_title),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text  = stringResource(R.string.app_subtitle),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.background,
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // ── Selector Viña / Bodega ────────────────────────────────────
                ConvenioSelector(
                    selected = form.convenio,
                    onSelect = {
                        viewModel.onConvenioChange(it)
                        viewModel.limpiar()
                    }
                )

                // ── Formulario ────────────────────────────────────────────────
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape     = MaterialTheme.shapes.extraLarge,
                    colors    = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                ) {
                    Column(
                        modifier            = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FormSectionTitle("Datos del trabajador")

                        // Animación al cambiar convenio
                        AnimatedContent(
                            targetState  = form.convenio,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label        = "form_transition"
                        ) { convenio ->
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                when (convenio) {
                                    Convenio.VIÑA   -> ViñaForm(form, viewModel)
                                    Convenio.BODEGA -> BodegaForm(form, viewModel)
                                }
                            }
                        }

                        FormSubSectionDivider(
                            icon  = Icons.Default.Schedule,
                            label = "Horas extras"
                        )

                        HorasExtrasSection(form, viewModel)

                        FormSubSectionDivider(
                            icon  = Icons.Default.AccountBalance,
                            label = "Sindicato"
                        )

                        SwitchRow(
                            label           = "Afiliado/a al sindicato",
                            sublabel        = "Sin aporte solidario",
                            checked         = form.estaAfiliado,
                            onCheckedChange = viewModel::onAfiliadoChange
                        )

                        Spacer(Modifier.height(4.dp))

                        // Botones
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick  = { viewModel.calcular() },
                                modifier = Modifier.weight(1f).height(52.dp),
                                shape    = MaterialTheme.shapes.large,
                            ) {
                                Text("Calcular", fontWeight = FontWeight.SemiBold)
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

                // ── Recibo de sueldo ──────────────────────────────────────────
                AnimatedVisibility(
                    visible = recibo.calculado,
                    enter   = fadeIn() + expandVertically(),
                    exit    = fadeOut() + shrinkVertically(),
                ) {
                    ReciboCard(recibo)
                }

                Text(
                    text     = stringResource(R.string.author_name),
                    style    = MaterialTheme.typography.labelSmall,
                    color    = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 4.dp, bottom = 16.dp)
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Selector de Convenio
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
                label    = {
                    Text(
                        text       = convenio.label,
                        fontWeight = if (selected == convenio) FontWeight.Bold else FontWeight.Normal,
                        maxLines   = 1,
                    )
                }
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Formulario — Viña (CCT 154/91)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ViñaForm(form: FormState, viewModel: SalarioViewModel) {
    SimpleDropdown(
        label        = "Categoría",
        opciones     = viewModel.categoriasViña.map { it.nombre },
        seleccionado = viewModel.categoriasViña
            .indexOfFirst { it.id == form.categoriaViñaId }.coerceAtLeast(0),
        onSelect     = { viewModel.onCategoriaViñaChange(viewModel.categoriasViña[it].id) },
        enabled      = form.funcionEspecial == FuncionEspecialViña.NINGUNA,
    )

    SimpleDropdown(
        label        = "Función especial",
        opciones     = FuncionEspecialViña.entries.map { it.label },
        seleccionado = FuncionEspecialViña.entries.indexOf(form.funcionEspecial),
        onSelect     = { viewModel.onFuncionEspecialChange(FuncionEspecialViña.entries[it]) }
    )

    SimpleDropdown(
        label        = "Tramo de antigüedad",
        opciones     = viewModel.tramosViña,
        seleccionado = form.tramoViñaIndex,
        onSelect     = viewModel::onTramoViñaChange
    )

    SwitchRow(
        label           = "Premio Asistencia",
        sublabel        = "5% del básico Obrero Común",
        checked         = form.tieneAsistencia,
        onCheckedChange = viewModel::onAsistenciaChange
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Formulario — Bodega (CCT 85/89)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun BodegaForm(form: FormState, viewModel: SalarioViewModel) {
    SimpleDropdown(
        label        = "Categoría",
        opciones     = viewModel.categoriasBodega.map { it.nombre },
        seleccionado = viewModel.categoriasBodega
            .indexOfFirst { it.id == form.categoriaBodegaId }.coerceAtLeast(0),
        onSelect     = { viewModel.onCategoriaBodegaChange(viewModel.categoriasBodega[it].id) }
    )

    OutlinedTextField(
        value         = if (form.aniosAntiguedad == 0) "" else form.aniosAntiguedad.toString(),
        onValueChange = {
            val n = it.filter(Char::isDigit).toIntOrNull()?.coerceIn(0, 30) ?: 0
            viewModel.onAniosAntiguedadChange(n)
        },
        label           = { Text("Años de antigüedad (0 – 30)") },
        placeholder     = { Text("0") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine      = true,
        modifier        = Modifier.fillMaxWidth(),
    )

    SimpleDropdown(
        label        = "Presentismo",
        opciones     = PresentismoBodega.entries.map { it.label },
        seleccionado = PresentismoBodega.entries.indexOf(form.presentismo),
        onSelect     = { viewModel.onPresentismoChange(PresentismoBodega.entries[it]) }
    )

    SwitchRow(
        label           = "Título sec. / universitario",
        sublabel        = "Bonificación 5%",
        checked         = form.tieneTitulo,
        onCheckedChange = viewModel::onTituloChange
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Sección horas extras (compartida entre convenios)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun HorasExtrasSection(form: FormState, viewModel: SalarioViewModel) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value         = if (form.horasExtra50 == 0) "" else form.horasExtra50.toString(),
            onValueChange = {
                val h = it.filter(Char::isDigit).toIntOrNull()?.coerceIn(0, 999) ?: 0
                viewModel.onHorasExtra50Change(h)
            },
            label           = { Text("HE al 50%") },
            placeholder     = { Text("0 hs") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine      = true,
            modifier        = Modifier.weight(1f),
        )
        OutlinedTextField(
            value         = if (form.horasExtra100 == 0) "" else form.horasExtra100.toString(),
            onValueChange = {
                val h = it.filter(Char::isDigit).toIntOrNull()?.coerceIn(0, 999) ?: 0
                viewModel.onHorasExtra100Change(h)
            },
            label           = { Text("HE al 100%") },
            placeholder     = { Text("0 hs") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine      = true,
            modifier        = Modifier.weight(1f),
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Card del recibo de sueldo
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ReciboCard(recibo: ReciboUiState) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape     = MaterialTheme.shapes.extraLarge,
        colors    = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
    ) {
        Column(
            modifier            = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            // Encabezado
            Text(
                "Liquidación de sueldo",
                style      = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color      = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.height(2.dp))
            ReciboHeaderChip(label = "Vigencia",   value = recibo.vigencia)
            ReciboHeaderChip(label = "Convenio",   value = recibo.convenioLabel)
            ReciboHeaderChip(label = "Categoría",  value = recibo.categoriaLabel)
            ReciboHeaderChip(label = "Antigüedad", value = recibo.antiguedadLabel)

            Spacer(Modifier.height(12.dp))

            // ── HABERES ──────────────────────────────────────────────────────
            SeccionRecibo(
                icon           = Icons.Default.AttachMoney,
                titulo         = "HABERES REMUNERATIVOS",
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                labelColor     = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.height(4.dp))
            recibo.haberes.forEach { ItemReciboRow(it) }
            Spacer(Modifier.height(4.dp))
            TotalRow(
                label = "Sueldo Bruto",
                monto = recibo.totalBruto,
                color = MaterialTheme.colorScheme.primary,
            )

            Spacer(Modifier.height(12.dp))

            // ── DESCUENTOS ────────────────────────────────────────────────────
            SeccionRecibo(
                icon           = Icons.Default.MoneyOff,
                titulo         = "DESCUENTOS",
                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.45f),
                labelColor     = MaterialTheme.colorScheme.error,
            )
            Spacer(Modifier.height(4.dp))
            recibo.retenciones.forEach {
                ItemReciboRow(it, montoColor = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(4.dp))
            TotalRow(
                label = "Total descuentos",
                monto = -recibo.totalRetenciones,
                color = MaterialTheme.colorScheme.error,
            )

            Spacer(Modifier.height(12.dp))

            // ── NO REMUNERATIVAS ──────────────────────────────────────────────
            SeccionRecibo(
                icon           = Icons.Default.AccountBalance,
                titulo         = "SUMAS NO REMUNERATIVAS",
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f),
                labelColor     = MaterialTheme.colorScheme.secondary,
            )
            Spacer(Modifier.height(4.dp))
            recibo.noRemunerativos.forEach { ItemReciboRow(it) }
            Spacer(Modifier.height(4.dp))
            TotalRow(
                label = "Total no remunerativo",
                monto = recibo.totalNORemunerativo,
                color = MaterialTheme.colorScheme.secondary,
            )

            Spacer(Modifier.height(16.dp))

            // ── SUELDO NETO ───────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.large)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically,
                ) {
                    Text(
                        text       = "SUELDO NETO",
                        style      = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color      = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    Text(
                        text       = formatCurrency(recibo.sueldoNeto),
                        style      = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color      = MaterialTheme.colorScheme.primary,
                        textAlign  = TextAlign.End,
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Componentes auxiliares de formulario
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun FormSectionTitle(text: String) {
    Text(
        text       = text,
        style      = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color      = MaterialTheme.colorScheme.primary,
    )
}

@Composable
private fun FormSubSectionDivider(icon: ImageVector, label: String) {
    Row(
        verticalAlignment   = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier            = Modifier.padding(top = 4.dp)
    ) {
        HorizontalDivider(
            modifier  = Modifier.weight(1f),
            color     = MaterialTheme.colorScheme.outlineVariant,
        )
        Icon(
            imageVector        = icon,
            contentDescription = null,
            modifier           = Modifier.size(14.dp),
            tint               = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text  = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color    = MaterialTheme.colorScheme.outlineVariant,
        )
    }
}

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
        expanded         = expanded,
        onExpandedChange = { if (enabled) expanded = !expanded },
        modifier         = Modifier.fillMaxWidth(),
    ) {
        OutlinedTextField(
            value         = opciones.getOrElse(seleccionado) { "" },
            onValueChange = {},
            readOnly      = true,
            enabled       = enabled,
            label         = { Text(label) },
            trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier      = Modifier.fillMaxWidth().menuAnchor(),
            colors        = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
            ),
        )
        ExposedDropdownMenu(
            expanded         = expanded,
            onDismissRequest = { expanded = false },
            modifier         = Modifier.heightIn(max = 300.dp),
        ) {
            opciones.forEachIndexed { index, texto ->
                DropdownMenuItem(
                    text    = { Text(texto) },
                    onClick = { onSelect(index); expanded = false },
                )
                if (index < opciones.lastIndex) HorizontalDivider()
            }
        }
    }
}

@Composable
private fun SwitchRow(
    label: String,
    sublabel: String = "",
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            if (sublabel.isNotEmpty()) {
                Text(
                    sublabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Componentes auxiliares del recibo
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ReciboHeaderChip(label: String, value: String) {
    Row(
        modifier              = Modifier.fillMaxWidth().padding(vertical = 1.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            "$label:",
            style  = MaterialTheme.typography.labelSmall,
            color  = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(72.dp),
        )
        Text(
            value,
            style      = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun SeccionRecibo(
    icon: ImageVector,
    titulo: String,
    containerColor: Color,
    labelColor: Color,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .background(containerColor)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(
            imageVector        = icon,
            contentDescription = null,
            modifier           = Modifier.size(14.dp),
            tint               = labelColor,
        )
        Text(
            text       = titulo,
            style      = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color      = labelColor,
        )
    }
}

@Composable
private fun ItemReciboRow(
    item: ItemRecibo,
    montoColor: Color = LocalContentColor.current,
) {
    val isSubItem = item.esSubItem
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start  = if (isSubItem) 12.dp else 0.dp,
                top    = if (isSubItem) 0.dp else 2.dp,
                bottom = if (isSubItem) 0.dp else 2.dp,
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text     = item.descripcion,
            style    = if (isSubItem) MaterialTheme.typography.labelSmall
                       else MaterialTheme.typography.bodySmall,
            color    = if (isSubItem) MaterialTheme.colorScheme.onSurfaceVariant
                       else LocalContentColor.current,
            fontStyle = if (isSubItem) FontStyle.Italic else FontStyle.Normal,
            modifier  = Modifier.weight(1f),
        )
        Text(
            text      = formatCurrency(item.monto),
            style     = if (isSubItem) MaterialTheme.typography.labelSmall
                        else MaterialTheme.typography.bodySmall,
            color     = if (isSubItem) MaterialTheme.colorScheme.tertiary else montoColor,
            fontStyle = if (isSubItem) FontStyle.Italic else FontStyle.Normal,
            textAlign = TextAlign.End,
        )
    }
}

@Composable
private fun TotalRow(label: String, monto: Double, color: Color) {
    Row(
        modifier              = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        Text(
            text       = label,
            style      = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text       = formatCurrency(monto),
            style      = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color      = color,
            textAlign  = TextAlign.End,
        )
    }
}
