package com.example.ngabtrips

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage


object SupabaseClient {

    private const val SUPABASE_URL = "https://thjpnlydcyqvbwcaelnd.supabase.co"
    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InRoanBubHlkY3lxdmJ3Y2FlbG5kIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjE1MjEwMTAsImV4cCI6MjA3NzA5NzAxMH0.vK0WgQaK8wCuAvnJ4mSSwiR8BD_ecrxvaYCJjocUkA0"
    val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        install(Auth)
        install(Postgrest)
        install(Realtime)
        install(Storage)
    }
}