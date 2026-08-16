package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.BorderLine
import com.example.ui.theme.NavyPrimary
import kotlin.math.abs

@Composable
fun QrCodeView(
    data: String,
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    darkColor: Color = NavyPrimary,
    lightColor: Color = Color.White
) {
    val matrixSize = 21
    val grid = remember(data) {
        generateQrMatrix(data, matrixSize)
    }

    Box(
        modifier = modifier
            .size(size)
            .background(lightColor, RoundedCornerShape(12.dp))
            .border(1.dp, BorderLine, RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Canvas(modifier = Modifier.size(size - 20.dp)) {
            val cellWidth = this.size.width / matrixSize
            val cellHeight = this.size.height / matrixSize

            for (r in 0 until matrixSize) {
                for (c in 0 until matrixSize) {
                    if (grid[r][c]) {
                        drawRect(
                            color = darkColor,
                            topLeft = Offset(c * cellWidth, r * cellHeight),
                            size = Size(cellWidth + 0.5f, cellHeight + 0.5f)
                        )
                    }
                }
            }
        }
    }
}

private fun generateQrMatrix(data: String, size: Int): Array<BooleanArray> {
    val matrix = Array(size) { BooleanArray(size) }

    // Draw position detection patterns (3 corners)
    fun drawFinder(top: Int, left: Int) {
        for (r in 0..6) {
            for (c in 0..6) {
                val isOuter = r == 0 || r == 6 || c == 0 || c == 6
                val isInner = r in 2..4 && c in 2..4
                matrix[top + r][left + c] = isOuter || isInner
            }
        }
    }

    drawFinder(0, 0)
    drawFinder(0, size - 7)
    drawFinder(size - 7, 0)

    // Timing patterns
    for (i in 8 until size - 8) {
        matrix[6][i] = i % 2 == 0
        matrix[i][6] = i % 2 == 0
    }

    // Fill data pseudo-randomly based on hash
    val hash = data.hashCode()
    var bitIndex = 0
    val bytes = data.toByteArray()

    for (r in 0 until size) {
        for (c in 0 until size) {
            // Skip finder patterns
            if ((r < 8 && c < 8) || (r < 8 && c >= size - 8) || (r >= size - 8 && c < 8)) continue
            if (r == 6 || c == 6) continue

            val byteVal = if (bytes.isNotEmpty()) bytes[bitIndex % bytes.size].toInt() else 0
            val bit = ((byteVal xor (hash shr (bitIndex % 16))) and (1 shl (bitIndex % 8))) != 0
            val pattern = ((r * 7 + c * 13 + hash) % 3 == 0)
            matrix[r][c] = bit xor pattern
            bitIndex++
        }
    }

    return matrix
}
