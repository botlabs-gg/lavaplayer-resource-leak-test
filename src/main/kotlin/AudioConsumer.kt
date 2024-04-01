package gg.botlabs

import com.sun.org.slf4j.internal.LoggerFactory


class AudioConsumer internal constructor(private val player: Player) : Thread() {

    override fun run() {
        val start = System.currentTimeMillis()
        var i: Long = 0

        while (true) {
            player.tryProvide()

            val targetTime = ((start / INTERVAL) + i + 1) * INTERVAL
            val diff = targetTime - System.currentTimeMillis()
            i++

            synchronized(this) {
                try {
                    if (diff > 0) {
                        sleep(diff / 2)
                    }
                } catch (e: InterruptedException) {
                    throw RuntimeException(e)
                }
            }
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(AudioConsumer::class.java)
        private const val INTERVAL = 20 // A frame is 20ms
    }
}