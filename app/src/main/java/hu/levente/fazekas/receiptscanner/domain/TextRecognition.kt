package hu.levente.fazekas.receiptscanner.domain

import android.content.Context
import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

private fun runTextRecognition(context: Context, selectedImage: Bitmap) {
    val image = InputImage.fromBitmap(selectedImage, 0)
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    recognizer.process(image)
        .addOnSuccessListener { texts ->
            val blocks = texts.textBlocks
            if (blocks.isEmpty()) {
                println("No text found")
            }
            val distances = mutableListOf<Pair<com.google.mlkit.vision.text.Text.Line, com.google.mlkit.vision.text.Text.Line>>()
            blocks.forEach {block1 ->
                block1.lines.forEach {line1 ->
                    blocks.forEach {block2 ->
                        block2.lines.forEach {line2 ->
                            val distance = line1.boundingBox?.top!! - line2.boundingBox?.top!!
                            if (distance in -150..150){
                                distances.add(line1 to line2)
                            }
                        }
                    }
                }
            }
            val distances1 = distances.filter { it.second.text.all { it.isDigit() || it.isWhitespace() } && !it.first.text.all { it.isDigit() || it.isWhitespace() } && it.first.text.containsAFACode() }
        }
        .addOnFailureListener { e -> // Task failed with an exception
            e.printStackTrace()
        }
}

private fun String.containsAFACode(): Boolean {
    return this.contains("COO") ||
            this.contains("CO0") ||
            this.contains("C0O") ||
            this.contains("C00") ||
            this.contains("A00") ||
            this.contains("AO0") ||
            this.contains("A0O") ||
            this.contains("AOO")
    //|| this.contains("x")

}