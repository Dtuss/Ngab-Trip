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
import com.example.ngabtrips.User



@Composable
fun LoginScreen(navController: NavController) {
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
                text = "Masuk ke Akun",
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
                                // Ambil data user dari tabel "users"
                                val users = SupabaseClient.client
                                    .from("users")
                                    .select()
                                    .decodeList<User>()

                                // Cek apakah username dan password cocok
                                val user = users.find { it.username == username && it.password == password }

                                if (user != null) {
                                    withContext(Dispatchers.Main) {
                                        // Navigasi ke halaman utama setelah login sukses
                                        navController.navigate("dashboard/${user.username}") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    }
                                } else {
                                    message = "❌ Username atau password salah!"
                                }
                            } catch (e: Exception) {
                                message = "Gagal: ${e.message}"
                            }
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Login")
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
fun PreviewLoginScreen() {
    NgabTripsTheme {
        val navController = rememberNavController()
        LoginScreen(navController)
    }
}
