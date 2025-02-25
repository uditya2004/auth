package com.example.auth.auth.presentation.otpVerification


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import com.example.auth.auth.presentation.otpVerification.component.OtpInputField


//============
//OTP input Fields Box Row
//============
@Composable
fun OtpScreen(
    state: OtpState,
    focusRequesters: List<FocusRequester>,
    onAction: (OtpAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        //OTP 4 input Fields Box
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
        ) {
            state.code.forEachIndexed { index, number ->    // for each index in the list we create an input field box
                OtpInputField(
                    number = number,
                    focusRequester = focusRequesters[index],
                    onFocusChanged = { isFocused ->
                        if(isFocused) {
                            onAction(OtpAction.OnChangeFieldFocused(index))
                        }
                    },
                    onNumberChanged = { newNumber ->
                        onAction(OtpAction.OnEnterNumber(newNumber, index))
                    },
                    onKeyboardBack = {
                        onAction(OtpAction.OnKeyboardBack)
                    },
                    modifier = Modifier
                        .weight(1f)        // making each box equal width
                        .aspectRatio(1f)   // making each box square
                )
            }
        }

//        state.isValid?.let { isValid ->
//            Text(
//                text = if(isValid) "OTP is valid!" else "OTP is invalid!",
//                color = if(isValid) Color(0xff23c760) else Color.Red,
//                modifier = Modifier.padding(top = 6.dp),
//                fontSize = 16.sp
//            )
//        }
    }
}