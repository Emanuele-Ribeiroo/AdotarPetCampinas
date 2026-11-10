package com.adotarpet.campinas.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adotarpet.campinas.R
import com.adotarpet.campinas.viewmodel.LoginViewModel
import com.adotarpet.campinas.view.components.FormTextField
import com.adotarpet.campinas.view.theme.CreamBackground
import com.adotarpet.campinas.view.theme.CreamCard
import com.adotarpet.campinas.view.theme.Terracotta
import com.adotarpet.campinas.view.theme.TerracottaDark
import com.adotarpet.campinas.view.theme.TerracottaLight
import com.adotarpet.campinas.view.theme.TextSecondary
import com.adotarpet.campinas.view.theme.White

@Composable
fun LoginView() {
    // Estado e regras ficam no ViewModel; a tela só desenha e dispara eventos.
    val vm: LoginViewModel = viewModel()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.logo_adotar),
                contentDescription = "Adotar Pet Campinas",
                contentScale = ContentScale.Fit,
                modifier = Modifier.width(220.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text("Onde toda vida vale a pena", style = MaterialTheme.typography.bodyLarge, color = TextSecondary)

            Spacer(Modifier.height(32.dp))
            FormTextField(
                "E-mail", "voce@email.com", vm.email,
                modifier = Modifier.fillMaxWidth(),
                keyboardType = KeyboardType.Email
            ) { vm.onEmailChange(it) }
            FormTextField(
                "Senha", "Digite sua senha", vm.senha,
                modifier = Modifier.fillMaxWidth(),
                keyboardType = KeyboardType.Password,
                errorText = if (vm.erro) "E-mail ou senha incorretos" else null,
                visualTransformation = if (vm.mostrarSenha) VisualTransformation.None else PasswordVisualTransformation()
            ) { vm.onSenhaChange(it) }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = androidx.compose.foundation.layout.Arrangement.End) {
                TextButton(onClick = vm::alternarMostrarSenha) {
                    Text(if (vm.mostrarSenha) "Ocultar senha" else "Mostrar senha", color = Terracotta)
                }
            }

            Spacer(Modifier.height(8.dp))
            Button(
                onClick = vm::entrar,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Terracotta, contentColor = White),
                enabled = vm.podeEntrar
            ) {
                Text("Entrar")
            }

            Spacer(Modifier.height(12.dp))
            OutlinedButton(
                onClick = vm::entrarComoVisitante,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(50)
            ) {
                Text("Entrar como visitante", color = TerracottaDark)
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "Você pode ver os animais e enviar pedidos sem criar conta.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}
