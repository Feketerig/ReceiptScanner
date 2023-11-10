package hu.levente.fazekas.receiptscanner.database

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class ImageStorage(
    private val context: Context
) {
    suspend fun saveImage(bytes: ByteArray): String {
        return withContext(Dispatchers.IO){
            val fileName = UUID.randomUUID().toString() + ".jpg"
            context.openFileOutput(fileName, Context.MODE_PRIVATE).use {outputStream ->
                outputStream.write(bytes)
            }
            fileName
        }
    }

    suspend fun loadImage(fileName: String): ByteArray {
        return withContext(Dispatchers.IO){
            context.openFileInput(fileName).use {inputStream ->
                inputStream.readBytes()
            }
        }
    }

    suspend fun deleteImage(fileName: String){
        return withContext(Dispatchers.IO){
            context.deleteFile(fileName)
        }
    }
}