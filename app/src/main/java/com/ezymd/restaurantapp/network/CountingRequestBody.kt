package com.ezymd.restaurantapp.network

import okhttp3.MediaType
import okhttp3.RequestBody
import okio.*
import java.io.IOException

class CountingRequestBody(
    private val delegate: RequestBody,
    private val listener: RequestProgressListener
) : RequestBody() {
    override fun contentType(): MediaType? {
        return delegate.contentType()
    }

    override fun contentLength(): Long {
        try {
            return delegate.contentLength()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return -1
    }

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        val countingSink = CountingSink(sink)
        val bufferedSink = countingSink.buffer()
        delegate.writeTo(bufferedSink)
        bufferedSink.flush()
    }

    interface RequestProgressListener {
        fun onProgressUpdate(bytesWritten: Long, contentLength: Long)
    }

    internal inner class CountingSink(delegate: Sink?) : ForwardingSink(delegate!!) {
        private var bytesWritten: Long = 0

        @Throws(IOException::class)
        override fun write(source: Buffer, byteCount: Long) {
            super.write(source, byteCount)
            bytesWritten += byteCount
            listener.onProgressUpdate(bytesWritten, contentLength())
        }
    }
}