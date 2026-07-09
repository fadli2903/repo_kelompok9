package com.pt_inti_app.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.pt_inti_app.model.*
import kotlinx.coroutines.tasks.await
import com.pt_inti_app.model.JatahCuti

object FirebaseRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    val currentUid: String? get() = auth.currentUser?.uid

    // Helper untuk mengambil tahun berjalan
    private fun tahunSekarang(): Int = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)

    // ---------- AUTH ----------
    suspend fun login(email: String, password: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) { false }
    }

    suspend fun getCurrentUserProfile(): User? {
        val uid = currentUid ?: return null
        val snapshot = db.collection("users").document(uid).get().await()
        return snapshot.toObject(User::class.java)
    }

    fun logout() = auth.signOut()

    // ---------- ABSENSI ----------
    suspend fun clockIn(karyawanId: String): Boolean {
        return try {
            val docRef = db.collection("absensi").document()
            val absensi = Absensi(
                absensiId = docRef.id,
                karyawanId = karyawanId,
                waktuCheckIn = System.currentTimeMillis()
            )
            docRef.set(absensi).await()
            true
        } catch (e: Exception) { false }
    }

    suspend fun clockOut(absensiId: String): Boolean {
        return try {
            db.collection("absensi").document(absensiId)
                .update("waktuCheckOut", System.currentTimeMillis())
                .await()
            true
        } catch (e: Exception) { false }
    }

    suspend fun getRiwayatAbsensi(karyawanId: String): List<Absensi> {
        val snapshot = db.collection("absensi")
            .whereEqualTo("karyawanId", karyawanId)
            .get().await()
        return snapshot.toObjects(Absensi::class.java)
    }

    // Untuk Atasan: lihatAbsensiTim()
    suspend fun getSemuaAbsensi(): List<Absensi> {
        val snapshot = db.collection("absensi").get().await()
        return snapshot.toObjects(Absensi::class.java)
    }

    // ---------- AKTIVITAS HARIAN ----------
    suspend fun simpanAktivitas(aktivitas: AktivitasHarian): Boolean {
        return try {
            val docRef = db.collection("aktivitas_harian").document()
            db.collection("aktivitas_harian").document(docRef.id)
                .set(aktivitas.copy(aktivitasId = docRef.id)).await()
            true
        } catch (e: Exception) { false }
    }

    // ---------- SURAT PENGAJUAN (CUTI, LEMBUR, DLL) ----------
    suspend fun ajukanSurat(surat: SuratPengajuan): Boolean {
        return try {
            val docRef = db.collection("surat_pengajuan").document()
            docRef.set(surat.copy(suratId = docRef.id)).await()
            true
        } catch (e: Exception) { false }
    }

    suspend fun getSuratByKaryawan(karyawanId: String): List<SuratPengajuan> {
        val snapshot = db.collection("surat_pengajuan")
            .whereEqualTo("karyawanId", karyawanId)
            .get().await()
        return snapshot.toObjects(SuratPengajuan::class.java)
    }

    // Untuk SDM: cekPengajuanYangDibutuhkanKaryawan()
    suspend fun getSemuaSurat(): List<SuratPengajuan> {
        val snapshot = db.collection("surat_pengajuan").get().await()
        return snapshot.toObjects(SuratPengajuan::class.java)
    }

    suspend fun updateStatusSurat(suratId: String, status: String): Boolean {
        return try {
            db.collection("surat_pengajuan").document(suratId)
                .update("statusApproval", status).await()
            true
        } catch (e: Exception) { false }
    }

    // Dipanggil Atasan saat klik "Setuju"
    suspend fun setujuiSurat(surat: SuratPengajuan): Boolean {
        return try {
            db.collection("surat_pengajuan").document(surat.suratId)
                .update("statusApproval", "DISETUJUI").await()

            // Potong jatah cuti HANYA saat disetujui (bukan saat diajukan)
            if (surat.kategori == "CUTI" && surat.potongJatah && surat.jumlahHari > 0) {
                val docId = "${surat.karyawanId}_${tahunSekarang()}"
                db.collection("jatah_cuti").document(docId)
                    .update("terpakai", FieldValue.increment(surat.jumlahHari.toLong()))
                    .await()
            }
            true
        } catch (e: Exception) { false }
    }

    // Dipanggil Atasan saat klik "Tolak" -> jatah TIDAK berkurang
    suspend fun tolakSurat(suratId: String): Boolean {
        return try {
            db.collection("surat_pengajuan").document(suratId)
                .update("statusApproval", "DITOLAK").await()
            true
        } catch (e: Exception) { false }
    }

    // ---------- JATAH CUTI ----------
    suspend fun getJatahCuti(karyawanId: String, namaKaryawan: String = ""): JatahCuti {
        val tahun = tahunSekarang()
        val docId = "${karyawanId}_$tahun"
        val doc = db.collection("jatah_cuti").document(docId).get().await()
        return if (doc.exists()) {
            doc.toObject(JatahCuti::class.java) ?: JatahCuti(karyawanId = karyawanId, tahun = tahun)
        } else {
            val baru = JatahCuti(
                karyawanId = karyawanId,
                karyawanNama = namaKaryawan,
                tahun = tahun,
                totalJatah = 12, // <- default jatah cuti/tahun, ganti di sini kalau mau beda
                terpakai = 0
            )
            db.collection("jatah_cuti").document(docId).set(baru).await()
            baru
        }
    }

    suspend fun getSemuaJatahCutiTahunIni(): List<JatahCuti> {
        val snapshot = db.collection("jatah_cuti")
            .whereEqualTo("tahun", tahunSekarang())
            .get().await()
        return snapshot.toObjects(JatahCuti::class.java)
    }

    // ---------- PROBLEM / HELPDESK ----------
    suspend fun buatTiketProblem(problem: Problem): Boolean {
        return try {
            val docRef = db.collection("problem").document()
            docRef.set(problem.copy(tiketId = docRef.id)).await()
            true
        } catch (e: Exception) { false }
    }

    suspend fun getTiketBaru(): List<Problem> {
        val snapshot = db.collection("problem")
            .whereEqualTo("statusTiket", "BARU")
            .get().await()
        return snapshot.toObjects(Problem::class.java)
    }

    // TimHelpDeskIT: kirimKonfirmasiPermasalahan()
    suspend fun konfirmasiPenyelesaian(tiketId: String, catatan: String): Boolean {
        return try {
            db.collection("problem").document(tiketId)
                .update(
                    mapOf(
                        "statusTiket" to "SELESAI",
                        "catatanPenyelesaian" to catatan,
                        "waktuSelesai" to System.currentTimeMillis()
                    )
                ).await()
            true
        } catch (e: Exception) { false }
    }
}