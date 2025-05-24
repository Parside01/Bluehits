package com.example.bluehits.ui.console

import java.io.OutputStream
import java.io.Writer
import java.nio.charset.Charset

class ConsoleWriteAdapter(private val writer: Writer, private val charset: Charset = Charset.defaultCharset()) : OutputStream() {
    override fun write(b: Int) {
        writer.write(b.toChar().toInt())
    }

    override fun write(b: ByteArray) {
        writer.write(String(b, charset))
    }

    override fun write(b: ByteArray, off: Int, len: Int) {
        writer.write(String(b, off, len, charset))
    }

    override fun flush() {
        writer.flush()
    }

    override fun close() {
        writer.close()
    }
}