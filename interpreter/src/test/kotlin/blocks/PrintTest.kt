package interpreter.blocks

import interpreter.models.BlockManager
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.io.StringWriter
import java.io.BufferedWriter

class PrintTest {

    @Test
    fun testPrintBlockWithNullValue() {
        val stringWriter = StringWriter()
        val bufferedWriter = BufferedWriter(stringWriter)

        val printBlock = BlockManager.createPrintBlock(bufferedWriter)
        printBlock.execute()

        val output = stringWriter.toString()
        assertTrue(output.contains("null"), "Output should contain 'null'")
    }
}