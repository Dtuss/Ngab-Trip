package com.example.ngabtrips

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanBuatScreen(navController: NavController, userName: String) {
    val scope = rememberCoroutineScope()

    // Form State
    var groupName by remember { mutableStateOf("") }
    var selectedDestination by remember { mutableStateOf<Destination?>(null) }
    var meetingPoint by remember { mutableStateOf("") }
    var departureDate by remember { mutableStateOf("") }
    var maxMembers by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }
    // State untuk menyimpan tanggal yang dipilih dalam bentuk milidetik
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }

    // Fungsi untuk memformat milidetik menjadi String "YYYY-MM-DD" untuk Supabase
    fun formatMillisToSupabaseDate(millis: Long?): String {
        if (millis == null) return ""
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        // Set TimeZone ke UTC agar konsisten saat menyimpan ke database
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date(millis))
    }

    // Fungsi untuk memformat milidetik menjadi String yang lebih mudah dibaca untuk UI
    fun formatMillisToDisplayDate(millis: Long?): String {
        if (millis == null) return ""
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format(Date(millis))
    }

    // Dropdown State
    var expanded by remember { mutableStateOf(false) }
    var destinationList by remember { mutableStateOf(listOf<Destination>()) }

    // UI State
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Load Destination List
    LaunchedEffect(Unit) {
        try {
            destinationList = SupabaseClient.client
                .from("destinations")
                .select()
                .decodeList<Destination>()
        } catch (e: Exception) {
            errorMessage = "Gagal memuat destinasi: ${e.message}"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buat Grup Baru") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()) // Membuat kolom bisa di-scroll
        ) {
            Spacer(Modifier.height(20.dp))

            // Input Nama Grup
            OutlinedTextField(
                value = groupName,
                onValueChange = { groupName = it },
                label = { Text("Nama Grup") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(16.dp))

            // Dropdown Destinasi
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedDestination?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Pilih Destinasi") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    destinationList.forEach { dest ->
                        DropdownMenuItem(
                            text = { Text(dest.name) },
                            onClick = {
                                selectedDestination = dest
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Input Meeting Point
            OutlinedTextField(
                value = meetingPoint,
                onValueChange = { meetingPoint = it },
                label = { Text("Meeting Point") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(16.dp))

            // Input Tanggal Berangkat
            Box {
                OutlinedTextField(
                    value = formatMillisToDisplayDate(selectedDateMillis), // Tampilkan tanggal yang diformat
                    onValueChange = { /* Dibuat kosong karena read-only */ },
                    label = { Text("Tanggal Berangkat") },
                    readOnly = true, // Jadikan read-only
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Pilih Tanggal"
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                // Box transparan untuk mendeteksi klik di seluruh area TextField
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { showDatePicker = true }
                )
            }

            if (showDatePicker) {
                // `rememberDatePickerState` menyimpan state dari date picker, seperti tanggal yang dipilih
                val datePickerState = rememberDatePickerState()
                DatePickerDialog(
                    onDismissRequest = {
                        // Aksi saat dialog ditutup (misalnya klik di luar dialog)
                        showDatePicker = false
                    },
                    confirmButton = {
                        // Tombol untuk mengkonfirmasi tanggal yang dipilih
                        TextButton(
                            onClick = {
                                // Simpan tanggal yang dipilih (dalam milidetik UTC) ke state
                                selectedDateMillis = datePickerState.selectedDateMillis
                                // Tutup dialog
                                showDatePicker = false
                            }
                        ) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        // Tombol untuk membatalkan
                        TextButton(
                            onClick = {
                                showDatePicker = false
                            }
                        ) {
                            Text("Batal")
                        }
                    }
                ) {
                    // Komponen UI DatePicker itu sendiri
                    DatePicker(state = datePickerState)
                }
            }

            Spacer(Modifier.height(16.dp))

            // Input Max Anggota
            OutlinedTextField(
                value = maxMembers,
                onValueChange = { maxMembers = it },
                label = { Text("Maksimal Anggota") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(24.dp))

            // Error Text
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }

            // Tombol Simpan
            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                onClick = {
                    val departureDateString = formatMillisToSupabaseDate(selectedDateMillis)
                    if (groupName.isBlank() || selectedDestination == null || meetingPoint.isBlank() || departureDateString.isBlank() || maxMembers.isBlank()) {
                        errorMessage = "Semua field wajib diisi!"
                        return@Button
                    }

                    val maxMembersInt = maxMembers.toIntOrNull()
                    if (maxMembersInt == null) {
                        errorMessage = "Maksimal anggota harus berupa angka!"
                        return@Button
                    }

                    errorMessage = ""
                    isLoading = true

                    // SIMPAN KE DATABASE
                    scope.launch {
                        try {
                            // Buat objek Map untuk dikirim ke Supabase
                            val newGroup = CreateGroupRequest(
                                group_name = groupName,
                                destination_id = selectedDestination!!.destination_id,
                                creator_name = userName,
                                meeting_point = meetingPoint,
                                departure_date = departureDateString,
                                max_members = maxMembersInt,
                                member = listOf(userName)
                            )

                            SupabaseClient.client
                                .from("groups")
                                .insert(newGroup)

                            navController.popBackStack()
                        } catch (e: Exception) {
                            errorMessage = "Gagal menyimpan: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                }
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Simpan Grup")
                }
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}