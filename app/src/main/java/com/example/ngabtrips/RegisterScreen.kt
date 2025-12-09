package com.example.ngabtrips

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.ngabtrips.ui.theme.NgabTripsTheme
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Composable
fun RegisterScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Buat Akun Baru",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(30.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(30.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Batal")
                }
                Button(
                    onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val existingUsers = SupabaseClient.client
                                    .from("users")
                                    .select()
                                    .decodeList<User>()

                                if (existingUsers.any { it.username == username }) {
                                    message = "❌ Username sudah digunakan!"
                                } else {
                                    SupabaseClient.client
                                        .from("users")
                                        .insert(User(username = username, password = password))
                                    withContext(Dispatchers.Main) {
                                        navController.popBackStack()
                                    }
                                }
                            } catch (e: Exception) {
                                message = "Gagal: ${e.message}"
                            }
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Daftar")
                }
            }

            if (message.isNotEmpty()) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(text = message)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRegisterScreen() {
    NgabTripsTheme {
        RegisterScreen(rememberNavController())
    }
}
