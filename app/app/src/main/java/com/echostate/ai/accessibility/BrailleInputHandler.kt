package com.echostate.ai.accessibility

import android.view.MotionEvent
import com.echostate.ai.viewmodel.EchoStateViewModel

class BrailleInputHandler(private val viewModel: EchoStateViewModel) {
    private val activeDots = mutableSetOf<Int>()

    // Basic Grade 1 English Braille Mapping
    private val brailleMap = mapOf(
        setOf(1) to 'a',
        setOf(1, 2) to 'b',
        setOf(1, 4) to 'c',
        setOf(1, 4, 5) to 'd',
        setOf(1, 5) to 'e',
        setOf(1, 2, 4) to 'f',
        setOf(1, 2, 4, 5) to 'g',
        setOf(1, 2, 5) to 'h',
        setOf(2, 4) to 'i',
        setOf(2, 4, 5) to 'j',
        setOf(1, 3) to 'k',
        setOf(1, 2, 3) to 'l',
        setOf(1, 3, 4) to 'm',
        setOf(1, 3, 4, 5) to 'n',
        setOf(1, 3, 5) to 'o',
        setOf(1, 2, 3, 4) to 'p',
        setOf(1, 2, 3, 4, 5) to 'q',
        setOf(1, 2, 3, 5) to 'r',
        setOf(2, 3, 4) to 's',
        setOf(2, 3, 4, 5) to 't',
        setOf(1, 3, 6) to 'u',
        setOf(1, 2, 3, 6) to 'v',
        setOf(2, 4, 5, 6) to 'w',
        setOf(1, 3, 4, 6) to 'x',
        setOf(1, 3, 4, 5, 6) to 'y',
        setOf(1, 3, 5, 6) to 'z',
        setOf(3) to ''', // Mocking space for dot 3 only in this simple map for UX testing
        setOf(6) to ' '  // Space character
    )

    fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_POINTER_DOWN, MotionEvent.ACTION_DOWN -> {
                val index = event.actionIndex
                val x = event.getX(index)
                val y = event.getY(index)
                val screenWidth = 2000f // Mock screen width, would be injected dynamically
                
                // Determine dot based on horizontal screen split
                // Left Hand: Dots 1, 2, 3 (Top to Bottom, Left side)
                // Right Hand: Dots 4, 5, 6 (Top to Bottom, Right side)
                val dot = if (x < screenWidth / 2) {
                    when {
                        y < 500f -> 1
                        y < 1000f -> 2
                        else -> 3
                    }
                } else {
                    when {
                        y < 500f -> 4
                        y < 1000f -> 5
                        else -> 6
                    }
                }
                activeDots.add(dot)
            }
            MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_UP -> {
                // When the last finger is lifted, process the cell
                if (activeDots.isNotEmpty() && event.pointerCount == 1) {
                    processBrailleCell(activeDots.toSet())
                    activeDots.clear()
                }
            }
        }
        return true
    }

    private fun processBrailleCell(dots: Set<Int>) {
        val character = brailleMap[dots]
        if (character != null) {
            viewModel.updateBrailleText(character)
        }
    }
}
