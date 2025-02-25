package com.example.auth.auth.presentation.otpVerification.component

import android.view.KeyEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly

@Composable
fun OtpInputField(
    number: Int?,
    focusRequester: FocusRequester,
    onFocusChanged: (Boolean) -> Unit,
    onNumberChanged: (Int?) -> Unit,
    onKeyboardBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val text by remember(number) {
        mutableStateOf(
            TextFieldValue(
                text = number?.toString().orEmpty(),   // if text iss null we return empty string
                selection = TextRange(
                    index = if(number != null) 1 else 0   // controlling where to put the cursor (i.e if the text field not null put the cursor at index 1 i.e after the number)  else put it at index 0.
                )
            )
        )
    }
    var isFocused by remember {   // keeping track of whether the text field is focused or not
        mutableStateOf(false)
    }

    Box(
        modifier = modifier
            .border(
                width = 2.dp,
                color = if (isFocused) Color.Black else Color.LightGray,   // Border of the Box. Dynamic border color based on focus
                shape = MaterialTheme.shapes.medium                        // Rounded corners for the border
            )
            .background(color = Color.White, shape = MaterialTheme.shapes.medium),  // background of the Box
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(    // Using BasicTextField as we need custom text field and not the material 3 one.
            value = text,
            onValueChange = { newText ->
                val newNumber = newText.text
                if(newNumber.length <= 1 && newNumber.isDigitsOnly()) {   // allowing atmost 1 char in this input field and it should only be digits
                    onNumberChanged(newNumber.toIntOrNull())
                }
            },
            cursorBrush = SolidColor(Color.Black),
            singleLine = true,
            textStyle = TextStyle(
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                fontSize = 22.sp,          // font of text
                color = Color.Black
            ),
            keyboardOptions = KeyboardOptions(      // Specifying the keyboard type to open (i.e open number keyboard)
                keyboardType = KeyboardType.NumberPassword
            ),
            modifier = Modifier
                .padding(4.dp)
                .focusRequester(focusRequester)
                .onFocusChanged {
                    isFocused = it.isFocused
                    onFocusChanged(it.isFocused)
                }
                .onKeyEvent { event ->         // On prevent delete button it should jump to previous cell
                    val didPressDelete = event.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_DEL
                    if(didPressDelete && number == null) {    // if number is null and delete button is pressed, only then move to previous cell
                        onKeyboardBack()
                    }
                    false
                },
            decorationBox = { innerBox ->    // defining what to show when the box is empty and unfocused
                innerBox()
                if(!isFocused && number == null) {
                    Text(
                        text = "-",
                        textAlign = TextAlign.Center,
                        color = Color.LightGray,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize()
                    )
                }
            }
        )
    }
}