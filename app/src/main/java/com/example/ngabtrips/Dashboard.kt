package com.example.ngabtrips

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.LocalContext


@Composable
fun DashboardScreen(username: String, navController: NavController) {
    val context = LocalContext.current
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Halo, $username")
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Mau Trip ke Mana nih...",
                modifier = Modifier.padding(bottom = 12.dp),
                style = MaterialTheme.typography.headlineSmall
            )
            Row {
                Button(onClick = { navController.navigate("halamanCari/$username") }) {
                    Text("Cari Destinasi")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { navController.navigate("halamanBuat/$username") }) {
                    Text("Buat Destinasi")
                }
            }
        }
        // Tombol Logout
        Button(
            onClick = {
                // Pindah ke MainActivity
                val intent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                context.startActivity(intent)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(70.dp)
        ) {
            Text("Logout", color = Color.White)
        }
    }
}

