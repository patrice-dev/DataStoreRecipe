package com.codelog.datastorerecipe.ui.screens.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.codelog.datastorerecipe.ui.screens.viewmodels.SettingsViewModel

@Composable

fun PreferenceDSItem(viewModel: SettingsViewModel) {

    var selectedIndex by remember { mutableStateOf(0) }
    val options = listOf("true", "false")
    val pattern = remember { Regex("^\\d+\$") }

    val userSettingsPreferenceState by viewModel.userSettingsPreferenceState.collectAsState()
    val canSettingsPreference by viewModel.canSettingsPreference.collectAsState()


    Column (modifier = Modifier.fillMaxWidth()) {

        Spacer(modifier = Modifier.padding(16.dp))

        Text(modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
            text = "Preferences DataStore")

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
            Arrangement.SpaceBetween) {

            Text(text = "boolean preference")

            SingleChoiceSegmentedButtonRow {
                options.forEachIndexed { index, option ->


                    if(userSettingsPreferenceState.boolPref)
                        selectedIndex = 0;
                    else
                        selectedIndex = 1;

                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = options.size
                        ),
                        onClick = {

                            selectedIndex = index
                            viewModel.onBoolPrefChanged(if(index == 0) true else false)
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
            value = userSettingsPreferenceState.strFiled,
            onValueChange = { newValue ->

                viewModel.onStrFiledChanged(newValue)
            },
            label = { Text(text = "Enter your text :") }
        )

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            value = userSettingsPreferenceState.numField.toString(),
            onValueChange = { newValue ->

                if( newValue.matches(pattern)) {

                    viewModel.onNumFieldChanged(newValue.toInt())
                } else
                    viewModel.onNumFieldChanged(0)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text(text = "Enter your number :") }
        )

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
            Arrangement.End) {


            TextButton(
                onClick = { viewModel.onSavePreference() },
                modifier = Modifier.padding(),
                enabled = canSettingsPreference,
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