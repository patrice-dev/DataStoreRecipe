package com.codelog.datastorerecipe.ui.screens.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.codelog.datastorerecipe.datastore.Userpreferences
import com.codelog.datastorerecipe.ui.screens.viewmodels.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProtoDSItem(viewModel: SettingsViewModel) {

    var selectedIndex by remember { mutableStateOf(0) }
    val options = listOf("true", "false")
    val pattern = remember { Regex("^\\d+\$") }

    var sliderPosition by remember { mutableFloatStateOf(0f) }

    val userSettingsPreferenceProtoState by viewModel.userSettingsProtoState.collectAsState()
    val canSettingsPreferenceProto by viewModel.canSettingsProto.collectAsState()

    sliderPosition = contrastLevelValue(userSettingsPreferenceProtoState.contrastLevel)


    Column (modifier = Modifier.fillMaxWidth()){
        Text(modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
            text = "Preferences Proto DataStore")
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
            Arrangement.SpaceBetween) {

            Text(text = "boolean preference")

            SingleChoiceSegmentedButtonRow {
                options.forEachIndexed { index, option ->

                    if(userSettingsPreferenceProtoState.boolPref)
                        selectedIndex = 0;
                    else
                        selectedIndex = 1;

                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = options.size
                        ),

                        onClick = { selectedIndex = index

                            viewModel.onBoolPrefProtoChanged(if(index == 0) true else false)
                        },
                        selected = index == selectedIndex,
                        label = { Text(text = option) },

                        )
                }
            }
        }

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            value = userSettingsPreferenceProtoState.strFiled,
            onValueChange = { newValue ->
                viewModel.onStrFiledProtoChanged(newValue)
            },
            label = { Text(text = "Enter your text :") }
        )

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            value = userSettingsPreferenceProtoState.numField.toString(),
            onValueChange = { newValue ->

                if( newValue.matches(pattern)) {

                    viewModel.onNumFieldProtoChanged(newValue.toInt())
                } else
                    viewModel.onNumFieldProtoChanged(0)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text(text = "Enter your number :") }
        )


        Column(modifier = Modifier.fillMaxWidth()
            .padding(8.dp)) {

            Slider(
                value =sliderPosition,
                steps = 1,
                valueRange = 0f..2f,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = Color.Gray
                ),
                thumb = {
                    Box(
                        Modifier
                            .size(28.dp)
                            .padding(4.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                    )
                },
                onValueChange = {

                    sliderPosition = it
                    viewModel.onContrastLevelProtoChanged(valueToContrastLevel(sliderPosition))
                }
            )
            Text(text = contrastLevelName(sliderPosition))
        }

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
            Arrangement.End) {

            TextButton(
                onClick = { viewModel.onSaveProtoPreference() },
                modifier = Modifier.padding(),
                enabled = canSettingsPreferenceProto,
                colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    disabledContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            ) { Text (text = "Save") }
        }
    }
}


/**
 * Converts the given contrast level to its corresponding float value.
 *
 * @param contrastLevel The contrast level of type Userpreferences.ContrastLevel to be converted.
 * @return A float value representing the contrast level.
 */
fun contrastLevelValue(contrastLevel: Userpreferences.ContrastLevel): Float {
    return when (contrastLevel) {
        Userpreferences.ContrastLevel.MIN -> 0f
        Userpreferences.ContrastLevel.MEDIUM -> 1f
        Userpreferences.ContrastLevel.HARD -> 2f
        Userpreferences.ContrastLevel.UNRECOGNIZED -> 0f
    }
}

/**
 * Determines the contrast level name based on the provided slider value.
 *
 * @param sliderValue The value of the slider, ranging from 0.0 to 2.0.
 *                     - 0.0 corresponds to "Min".
 *                     - 1.0 corresponds to "Medium".
 *                     - 2.0 corresponds to "Hard".
 *                     Other values will return "Unknown".
 * @return A string representing the contrast level name ("Min", "Medium", "Hard", or "Unknown").
 */
fun contrastLevelName(sliderValue: Float): String {
    return when (sliderValue) {
        0f -> "Min"
        1f -> "Medium"
        2f -> "Hard"
        else -> "Unknown"
    }
}

/**
 * Converts a given float value to a corresponding contrast level from the
 * predefined Userpreferences.ContrastLevel enum.
 *
 * @param value The float value representing the desired contrast level.
 * It can be 0f, 1f, 2f, or any other value.
 * @return The corresponding contrast level from the Userpreferences.ContrastLevel enum.
 * Returns MIN for 0f, MEDIUM for 1f, HARD for 2f, and UNRECOGNIZED for any other value.
 */
fun valueToContrastLevel(value: Float): Userpreferences.ContrastLevel {
    return when (value) {
        0f -> Userpreferences.ContrastLevel.MIN
        1f -> Userpreferences.ContrastLevel.MEDIUM
        2f -> Userpreferences.ContrastLevel.HARD
        else -> Userpreferences.ContrastLevel.UNRECOGNIZED
    }
}
