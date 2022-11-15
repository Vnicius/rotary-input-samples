/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.rotary

import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.*
import com.example.rotary.theme.WearAppTheme
import kotlinx.coroutines.launch

/**
 * Simple "Hello, World" app meant as a starting point for a new project using Compose for Wear OS.
 *
 * Displays only a centered [Text] composable, and the actual text varies based on the shape of the
 * device (round vs. square/rectangular).
 *
 * If you plan to have multiple screens, use the Wear version of Compose Navigation. You can carry
 * over your knowledge from mobile and it supports the swipe-to-dismiss gesture (Wear OS's
 * back action). For more information, go here:
 * https://developer.android.com/reference/kotlin/androidx/wear/compose/navigation/package-summary
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WearAppTheme {
                ScalingLazyColumnSample()
//                PickerSample()
//                MultiPickerSample()
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ScalingLazyColumnSample() {
    val scrollState = rememberScalingLazyListState()
    val focusRequester = remember { FocusRequester() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize()
                .background(MaterialTheme.colors.background)
                .onRotaryScrollEvent {
                    coroutineScope.launch {
                        scrollState.scrollBy(it.verticalScrollPixels)
                    }
                    true
                }
                .focusRequester(focusRequester)
                .focusable(),
            state = scrollState,
            verticalArrangement = Arrangement.Center
        ) {
            items(100) { index ->
                Text(text = index.toString())
            }
        }

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PickerSample() {
    val pickerState = rememberPickerState(
        initialNumberOfOptions = 100,
        initiallySelectedOption = 50
    )
    val focusRequester = remember { FocusRequester() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(modifier = Modifier.fillMaxSize()) {
        Picker(
            state = pickerState,
            contentDescription = pickerState.selectedOption.toString(),
            modifier = Modifier.fillMaxSize()
                .onRotaryScrollEvent {
                    coroutineScope.launch {
                        pickerState.scrollBy(it.verticalScrollPixels)
                    }
                    true
                }
                .focusRequester(focusRequester)
                .focusable()
        ) {
            Text(text = it.toString(), color = MaterialTheme.colors.primary)
        }

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MultiPickerSample() {
    val picker1State = rememberPickerState(
        initialNumberOfOptions = 100,
        initiallySelectedOption = 50
    )
    val picker2State = rememberPickerState(
        initialNumberOfOptions = 100,
        initiallySelectedOption = 50
    )
    val focusRequester1 = remember { FocusRequester() }
    val focusRequester2 = remember { FocusRequester() }
    val coroutineScope = rememberCoroutineScope()
    var selectedPicker by remember { mutableStateOf(0) }

    Scaffold(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxSize()) {
            Picker(
                state = picker1State,
                contentDescription = picker1State.selectedOption.toString(),
                modifier = Modifier.fillMaxHeight()
                    .weight(1f)
                    .onRotaryScrollEvent {
                        coroutineScope.launch {
                            picker1State.scrollBy(it.verticalScrollPixels)
                        }
                        true
                    }
                    .focusRequester(focusRequester1)
                    .focusable(),
                readOnly = selectedPicker != 0,
                onSelected = { selectedPicker = 0 }
            ) {
                SelectableText(
                    text = it.toString(),
                    selected = selectedPicker == 0,
                    onSelected = { selectedPicker = 0 }
                )
            }

            Picker(
                state = picker2State,
                contentDescription = picker2State.selectedOption.toString(),
                modifier = Modifier.fillMaxHeight()
                    .weight(1f)
                    .onRotaryScrollEvent {
                        coroutineScope.launch {
                            picker2State.scrollBy(it.verticalScrollPixels)
                        }
                        true
                    }
                    .focusRequester(focusRequester2)
                    .focusable(),
                readOnly = selectedPicker != 1,
                onSelected = { selectedPicker = 1 }
            ) {
                SelectableText(
                    text = it.toString(),
                    selected = selectedPicker == 1,
                    onSelected = { selectedPicker = 1 }
                )
            }
        }

        LaunchedEffect(selectedPicker) {
            listOf(focusRequester1, focusRequester2)[selectedPicker].requestFocus()
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SelectableText(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onSelected: () -> Unit = {}
) {
    Text(
        text = text,
        modifier = if (selected) {
            modifier
        } else {
            modifier.pointerInteropFilter { event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    onSelected()
                }

                true
            }
        },
        color = MaterialTheme.colors.primary
    )
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearAppTheme {
        ScalingLazyColumnSample()
    }
}
