
package pl.podkal.domowniczeqqq.shopping

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditShoppingItemDialog(
    shoppingItem: ShoppingItem?,
    onDismiss: () -> Unit,
    onSave: (ShoppingItem) -> Unit
) {
    val isEditMode = shoppingItem != null

    var name by remember { mutableStateOf(shoppingItem?.name ?: "") }
    var category by remember { mutableStateOf(shoppingItem?.category ?: "") }
    var quantity by remember { mutableStateOf(shoppingItem?.quantity?.toString() ?: "1") }
    var unit by remember { mutableStateOf(shoppingItem?.unit ?: "szt.") }

    var expandedCategoryDropdown by remember { mutableStateOf(false) }
    var expandedNameDropdown by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf(false) }
    var quantityError by remember { mutableStateOf(false) }

    val categories = remember { ShoppingItem.CATEGORIES.keys.toList() }
    val productSuggestions = remember(category) {
        if (category.isNotBlank() && ShoppingItem.CATEGORIES.containsKey(category)) {
            ShoppingItem.CATEGORIES[category] ?: emptyList()
        } else {
            emptyList()
        }
    }

    LaunchedEffect(category) {
        if (category.isNotBlank() && ShoppingItem.DEFAULT_UNITS.containsKey(category)) {
            unit = ShoppingItem.DEFAULT_UNITS[category].toString() ?: "szt."
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Dodaj nowy produkt",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                ExposedDropdownMenuBox(
                    expanded = expandedCategoryDropdown,
                    onExpandedChange = { expandedCategoryDropdown = it }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        readOnly = true,
                        label = { Text("Kategoria*") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoryDropdown) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCategoryDropdown,
                        onDismissRequest = { expandedCategoryDropdown = false }
                    ) {
                        categories.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    category = option
                                    expandedCategoryDropdown = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                ExposedDropdownMenuBox(
                    expanded = expandedNameDropdown && productSuggestions.isNotEmpty(),
                    onExpandedChange = { expandedNameDropdown = it }
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            nameError = false
                            expandedNameDropdown = true
                        },
                        label = { Text("Nazwa*") },
                        isError = nameError,
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    if (productSuggestions.isNotEmpty()) {
                        ExposedDropdownMenu(
                            expanded = expandedNameDropdown,
                            onDismissRequest = { expandedNameDropdown = false }
                        ) {
                            productSuggestions.forEach { suggestion ->
                                DropdownMenuItem(
                                    text = { Text(suggestion) },
                                    onClick = {
                                        name = suggestion
                                        expandedNameDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text("Ilość", modifier = Modifier.padding(vertical = 4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            val current = quantity.toIntOrNull() ?: 1
                            if (current > 1) {
                                quantity = (current - 1).toString()
                            }
                        }
                    ) {
                        Icon(Icons.Default.Remove, "Zmniejsz")
                    }

                    Text(
                        text = quantity,
                        style = MaterialTheme.typography.titleLarge
                    )

                    IconButton(
                        onClick = {
                            val current = quantity.toIntOrNull() ?: 1
                            quantity = (current + 1).toString()
                        }
                    ) {
                        Icon(Icons.Default.Add, "Zwiększ")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text("Jednostka", modifier = Modifier.padding(vertical = 4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("szt.", "kg", "g", "l", "ml").forEach { unitOption ->
                        FilterChip(
                            selected = unit == unitOption,
                            onClick = { unit = unitOption },
                            label = { Text(unitOption) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Anuluj")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (name.isBlank()) {
                                nameError = true
                                return@Button
                            }
                            val quantityNum = quantity.toDoubleOrNull()
                            if (quantityNum == null || quantityNum <= 0) {
                                quantityError = true
                                return@Button
                            }

                            onSave(
                                ShoppingItem(
                                    id = shoppingItem?.id ?: "",
                                    name = name,
                                    category = category,
                                    quantity = quantityNum,
                                    unit = unit,
                                    isChecked = shoppingItem?.isChecked ?: false
                                )
                            )
                        }
                    ) {
                        Text(if (isEditMode) "Zapisz" else "Dodaj")
                    }
                }
            }
        }
    }
}
