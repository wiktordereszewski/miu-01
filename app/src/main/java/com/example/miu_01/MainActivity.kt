package com.example.miu_01
import com.example.miu_01.ui.theme.AppTheme

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val isDarkTheme = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = !isDarkTheme // jasne ikony na ciemnym tle i odwrotnie
        }

        setContent {
            AppTheme(dynamicColor = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DateCalculatorScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateCalculatorScreen() {
    // Stan aplikacji
    var selectedDate by remember { mutableStateOf(Calendar.getInstance()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var offsetValue by remember { mutableStateOf("0") }
    var offsetUnit by remember { mutableStateOf(TimeUnit.DAY) }
    var resultDate by remember { mutableStateOf(Calendar.getInstance()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showInfoDialog by remember { mutableStateOf(false) }

    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    fun calculateNewDate() {
        try {
            val offset = offsetValue.toInt()

            if (offset > 10000 || offset < -10000) {
                errorMessage = "Wartość przesunięcia musi być między -10000 a 10000"
                resultDate = null
                return
            }

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = selectedDate.timeInMillis

            when (offsetUnit) {
                TimeUnit.HOUR -> calendar.add(Calendar.HOUR, offset)
                TimeUnit.DAY -> calendar.add(Calendar.DAY_OF_YEAR, offset)
                TimeUnit.WEEK -> calendar.add(Calendar.WEEK_OF_YEAR, offset)
            }

            resultDate = calendar
            errorMessage = null
        } catch (_: NumberFormatException) {
            errorMessage = "Wprowadź poprawną wartość liczbową"
            resultDate = null
        }
    }

    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            title = { Text("Informacje o aplikacji") },
            text = {
                Column {
                    Text("Aplikacja umożliwia obliczenie daty przyszłej lub przeszłej względem wybranej daty początkowej.", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Jak korzystać z kalkulatora przesunięcia daty:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("1. Wybierz datę początkową klikając na pole daty. Możesz wybrać ją z kalendarza lub wpisać ręcznie. Format wprowadzania daty jest zależy od języka urządzenia.\nZakres dat: 01.01.1900 - 31.12.2100.")
                    Text("2. Wprowadź wartość przesunięcia (może być ujemna, wtedy data będzie przesunięta wstecz).\nZakres wartości przesunięcia: -10000 do 10000.")
                    Text("3. Wybierz jednostkę przesunięcia: godziny, dni lub tygodnie")
                    Text("4. Wynik zostanie automatycznie obliczony i wyświetlony. W przypadku błędu zostanie wyświetlony komunikat.")
                }
            },
            confirmButton = {
                TextButton(onClick = { showInfoDialog = false }) {
                    Text("Zamknij")
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = { Text("Kalkulator Daty") },
                actions = {
                    IconButton(onClick = { showInfoDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Informacje o aplikacji"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Kalkulator przesunięcia daty",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Pole wyboru daty początkowej
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = { showDatePicker = true }
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Data początkowa:",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = dateFormat.format(selectedDate.time),
                        fontSize = 18.sp
                    )
                }
            }

            if (showDatePicker) {
                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = selectedDate.timeInMillis
                )

                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        Button(
                            onClick = {
                                datePickerState.selectedDateMillis?.let {
                                    selectedDate.timeInMillis = it
                                    // Preserve the hours and minutes
                                    showDatePicker = false
                                    calculateNewDate()
                                }
                            }
                        ) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showDatePicker = false }) {
                            Text("Anuluj")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            // Pole przesunięcia
            OutlinedTextField(
                value = offsetValue,
                onValueChange = {
                    offsetValue = it
                    calculateNewDate()
                },
                label = { Text("Wartość przesunięcia") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            // Wybór jednostki
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Jednostka przesunięcia:",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TimeUnit.entries.forEach { unit ->
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp)
                                .selectable(
                                    selected = offsetUnit == unit,
                                    onClick = {
                                        offsetUnit = unit
                                        calculateNewDate()
                                    }
                                )
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            RadioButton(
                                selected = offsetUnit == unit,
                                onClick = null // obsłużone przez selectable
                            )
                            Text(
                                text = when(unit) {
                                    TimeUnit.HOUR -> "Godziny"
                                    TimeUnit.DAY -> "Dni"
                                    TimeUnit.WEEK -> "Tygodnie"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }

            // Wynik
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Wynik:",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center
                        )
                    } else if (resultDate != null) {
                        Text(
                            text = dateFormat.format(resultDate.time),
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

        }
    }
}

enum class TimeUnit {
    HOUR, DAY, WEEK
}