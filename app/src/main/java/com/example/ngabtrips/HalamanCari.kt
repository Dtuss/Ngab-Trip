package com.example.ngabtrips

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

val destinationImages = mapOf(
    "Karimun Jawa" to R.drawable.karimun_jawa,
    "Gunung Bromo" to R.drawable.gunung_bromo,
    "Dieng Plateau" to R.drawable.dieng_plateau,
    "Puncak" to R.drawable.puncak,
    "Waduk Jatiluhur" to R.drawable.waduk_jatiluhur,
    "Bukit Jaddih" to R.drawable.bukit_jaddih,
    "Tanah Lot" to R.drawable.tanah_lot,
)

fun getDestinationImage(name: String): Int? {
    return destinationImages[name]
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanCariScreen(navController: NavController, username: String) {

    val scope = rememberCoroutineScope()
    var destinations by remember { mutableStateOf<List<Destination>>(emptyList()) }
    var errorMessage by remember { mutableStateOf("") }
    var searchText by remember { mutableStateOf("") }

    // 🔄 Ambil data dari Supabase 1x
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val result = SupabaseClient.client
                    .from("destinations")
                    .select()
                    .decodeList<Destination>()

                destinations = result

            } catch (e: Exception) {
                errorMessage = "Gagal memuat destinasi: ${e.message}"
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cari Destinasi") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {

            if (errorMessage.isNotEmpty()) {
                Text(errorMessage, color = MaterialTheme.colorScheme.error)
            }

            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("Cari destinasi...") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            val filteredDestinations = destinations.filter {
                it.name.contains(searchText, ignoreCase = true) ||
                        it.location.contains(searchText, ignoreCase = true)
            }

            LazyColumn {
                items(filteredDestinations) { dest ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {

                        Column(modifier = Modifier.padding(16.dp)) {

                            val imgRes = getDestinationImage(dest.name)
                            if (imgRes != null) {
                                Image(
                                    painter = painterResource(imgRes),
                                    contentDescription = dest.name,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(dest.name, style = MaterialTheme.typography.titleMedium)
                            Text(dest.location, style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(dest.description, style = MaterialTheme.typography.bodySmall)

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    navController.navigate("groups/${dest.destination_id}/$username")
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Cari Rombongan")
                            }
                        }
                    }
                }
            }
        }
    }
}