package com.bluelay.damda

import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_backup.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.channels.FileChannel

class BackupRestoreActivity : AppCompatActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_backup)

        tvBackup.setOnClickListener{
            try { /* sdcard/Android/data/com.bluelay.damda/files/damdaTest/DAMDA.db로 백업됨 */
                val sd: File? = getExternalFilesDir(null)
                val data: File = Environment.getDataDirectory()
                if (sd != null) {
                    if (sd.canWrite()) {
                        val BackupDir = File(sd, "damdaTest")
                        BackupDir.mkdir()
                        val currentDB = File(data, "//data//com.bluelay.damda//databases//DAMDA.db")
                        val backupDB = File(sd, "damdaTest/DAMDA.db")
                        val src: FileChannel = FileInputStream(currentDB).getChannel()
                        val dst: FileChannel = FileOutputStream(backupDB).getChannel()
                        dst.transferFrom(src, 0, src.size())
                        src.close()
                        dst.close()
                        Toast.makeText(applicationContext, "저장 OK", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(applicationContext, "저장 Fail", Toast.LENGTH_SHORT).show()
            }
        }

        tvRestore.setOnClickListener{
            try {
                val sd: File? = getExternalFilesDir(null)
                val data: File = Environment.getDataDirectory()

                if (sd != null) {
                    if (sd.canWrite()) {
                        val backupDB = File(sd, "//data//com.bluelay.damda//databases//DAMDA.db")
                        val currentDB = File(data, "damdaTest/DAMDA.db")

                        val src: FileChannel = FileInputStream(currentDB).getChannel()
                        val dst: FileChannel = FileOutputStream(backupDB).getChannel()

                        dst.transferFrom(src, 0, src.size())
                        src.close()
                        dst.close()

                        Toast.makeText(applicationContext, "복원 OK", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(applicationContext, "복원 Fail", Toast.LENGTH_SHORT).show()
            }
        }

    }
}