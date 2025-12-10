package com.example.ngabtrips

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsScreen(navController: NavController, destinationId: Int, username: String) {
    val scope = rememberCoroutineScope()
    var groups by remember { mutableStateOf<List<Group>>(emptyList()) }
    var newComment by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    // Fungsi untuk mengambil/refresh data
    fun fetchGroups() {
        scope.launch {
            try {
                // Cukup satu kali query untuk mendapatkan semua data
                val result = SupabaseClient.client
                    .from("groups")
                    .select {
                        filter {
                            eq("destination_id", destinationId)
                        }
                    }
                    .decodeList<Group>()
                groups = result
                // Debugging: Cetak jumlah grup yang ditemukan
                println("Ditemukan ${result.size} grup untuk destinationId: $destinationId")
            } catch (e: Exception) {
                // Debugging: Cetak error jika deserialisasi atau fetch gagal
                println("Error fetching groups: ${e.message}")
            }
        }
    }

    LaunchedEffect(destinationId) {
        fetchGroups()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daftar Rombongan") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .padding(horizontal = 16.dp)) {
            Spacer(modifier = Modifier.height(16.dp))
            if (groups.isEmpty()) {
                Text("Belum ada rombongan untuk destinasi ini.")
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(groups) { group ->
                    // Logika pengecekan menjadi lebih sederhana
                    val isCreator = group.creator_name == username
                    // isMember sekarang secara implisit akan true jika user adalah kreator
                    val isMember = group.member.contains(username)
                    val hasCommented = group.comment.any { it.startsWith("$username|") }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(group.group_name, style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("📍 Meeting Point: ${group.meeting_point}")
                            Text("📅 Berangkat: ${group.departure_date}")
                            Text("👥 Anggota: ${group.member.size} / ${group.max_members}")
                            Spacer(modifier = Modifier.height(16.dp))
                            val validComments = group.comment.filter { it.isNotBlank() }
                            if (validComments.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Komentar:", style = MaterialTheme.typography.titleSmall)

                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    validComments.forEach { c ->
                                        val split = c.split("|")
                                        val user = split.getOrNull(0) ?: "unknown"
                                        val msg = split.getOrNull(1) ?: ""

                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = CardDefaults.cardColors(
                                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                                            )
                                        ) {
                                            Text(
                                                text = buildAnnotatedString {
                                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                        append(user)
                                                    }
                                                    append(": ")
                                                    append(msg)
                                                },
                                                modifier = Modifier.padding(12.dp),
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                    }
                                }
                            }
                            if (group.status == "finished") {
                                Text(
                                    "Trip Selesai",
                                    style = MaterialTheme.typography.labelLarge,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Input Komentar
                                OutlinedTextField(
                                    value = newComment,
                                    onValueChange = { newComment = it },
                                    label = { Text("Tambahkan Komentar") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    onClick = {
                                        scope.launch {
                                            isSubmitting = true
                                            try {
                                                val updatedComments = group.comment + "$username|$newComment"
                                                SupabaseClient.client.from("groups")
                                                    .update(mapOf("comment" to updatedComments)) {
                                                        filter { eq("group_id", group.group_id) }
                                                    }

                                                newComment = ""
                                                fetchGroups()
                                            } catch (e: Exception) {
                                                println("Gagal menambah komentar: ${e.message}")
                                            }
                                            isSubmitting = false
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = newComment.isNotBlank() && !isSubmitting && !hasCommented
                                ) {
                                    Text(if (isSubmitting) "Mengirim..." else "Kirim Komentar")
                                }
                            } else {
                                // --- PERUBAHAN 2: Logika if/else disederhanakan ---
                                // Jika user adalah kreator, ia juga seorang member, jadi kita cek isCreator dulu.
                                if (isCreator) {
                                    // Tombol untuk kreator
                                    Button(
                                        onClick = {
                                            scope.launch {
                                                SupabaseClient.client.from("groups")
                                                    .update(mapOf("status" to "finished")) {
                                                        filter { eq("group_id", group.group_id) }
                                                    }
                                                fetchGroups() // Refresh data
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                                    ) {
                                        Text("Selesaikan Trip", color = MaterialTheme.colorScheme.onErrorContainer)
                                    }
                                } else if (isMember) {
                                    // Jika user adalah anggota (dan sudah pasti bukan kreator)
                                    Text(
                                        "Anda sudah bergabung",
                                        style = MaterialTheme.typography.labelLarge,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                } else {
                                    // Jika user sama sekali bukan member
                                    Button(
                                        onClick = {
                                            scope.launch {
                                                try {
                                                    val updatedMembers = group.member + username
                                                    SupabaseClient.client.from("groups")
                                                        .update(mapOf("member" to updatedMembers)) {
                                                            filter { eq("group_id", group.group_id) }
                                                        }
                                                    fetchGroups()
                                                } catch (e: Exception) {
                                                    println("Gagal bergabung grup: ${e.message}")
                                                }
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        // --- PERUBAHAN 3: Hapus "+ 1" dari validasi grup penuh ---
                                        enabled = (group.member.size < group.max_members)
                                    ) {
                                        if (group.member.size < group.max_members) {
                                            Text("Gabung Grup")
                                        } else {
                                            Text("Grup Penuh")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
