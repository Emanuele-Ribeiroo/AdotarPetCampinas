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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
fun LoginView(onLoggedIn: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var mostrarSenha by remember { mutableStateOf(false) }
    var erro by remember { mutableStateOf(false) }
    val vm: LoginViewModel = viewModel()

    val tentarEntrar = {
        if (vm.login(email, senha)) onLoggedIn() else erro = true
    }

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
                "E-mail", "voce@email.com", email,
                modifier = Modifier.fillMaxWidth(),
                keyboardType = KeyboardType.Email
            ) {
                email = it.trim()
                erro = false
            }
            FormTextField(
                "Senha", "Digite sua senha", senha,
                modifier = Modifier.fillMaxWidth(),
                keyboardType = KeyboardType.Password,
                errorText = if (erro) "E-mail ou senha incorretos" else null,
                visualTransformation = if (mostrarSenha) VisualTransformation.None else PasswordVisualTransformation()
            ) {
                senha = it
                erro = false
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = androidx.compose.foundation.layout.Arrangement.End) {
                TextButton(onClick = { mostrarSenha = !mostrarSenha }) {
                    Text(if (mostrarSenha) "Ocultar senha" else "Mostrar senha", color = Terracotta)
                }
            }

            Spacer(Modifier.height(8.dp))
            Button(
                onClick = tentarEntrar,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Terracotta, contentColor = White),
                enabled = email.isNotBlank() && senha.isNotBlank()
            ) {
                Text("Entrar")
            }
        }
    }
}
