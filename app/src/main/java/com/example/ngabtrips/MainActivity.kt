package com.example.ngabtrips

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ngabtrips.ui.theme.NgabTripsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NgabTripsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "start") {
                        composable("start") { StartScreen(navController) }
                        composable("register") { RegisterScreen(navController) }
                        composable("login") { LoginScreen(navController) }
                        composable(
                            "dashboard/{username}",
                            arguments = listOf(navArgument("username") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val username = backStackEntry.arguments?.getString("username")
                            if (username != null) {
                                DashboardScreen(username = username, navController = navController)
                            }
                        }
                        composable(
                            "halamanCari/{username}",
                            arguments = listOf(navArgument("username") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val username = backStackEntry.arguments?.getString("username") ?: ""
                            HalamanCariScreen(navController, username)
                        }
                        composable(
                            route = "halamanBuat/{username}",
                            arguments = listOf(navArgument("username") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val username = backStackEntry.arguments?.getString("username") ?: ""
                            HalamanBuatScreen(navController, username)
                        }
                        composable(
                            "groups/{destinationId}/{username}",
                            arguments = listOf(
                                navArgument("destinationId") { type = NavType.IntType },
                                navArgument("username") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val destinationId = backStackEntry.arguments?.getInt("destinationId") ?: 0
                            val username = backStackEntry.arguments?.getString("username") ?: ""
                            GroupsScreen(navController, destinationId, username)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StartScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Selamat Datang di NgabTrip!",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = { navController.navigate("login") }, // Navigasi ke halaman login
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login")
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { navController.navigate("register") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Daftar")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewStartScreen() {
    NgabTripsTheme {
        val navController = rememberNavController()
        StartScreen(navController)
    }
}
