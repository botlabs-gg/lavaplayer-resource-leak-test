package gg.botlabs

import com.sun.org.slf4j.internal.LoggerFactory
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.floor


class AudioConsumer internal constructor(val player: Player) : Thread() {

    override fun run() {
        val start = System.currentTimeMillis()
        var i: Long = 0

        while (running) {
            if (player.canProvide()) {
                served.incrementAndGet()
            } else {
                missed.incrementAndGet()
            }

            val targetTime = ((start / INTERVAL) + i + 1) * INTERVAL
            val diff = targetTime - System.currentTimeMillis()
            i++

            if (diff < -5000) {
                endReason = EndReason.CANT_KEEP_UP
                break
            }

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

    class Results internal constructor(var served: Int, var missed: Int, var endReason: EndReason) {
        val lossPercentString: String
            get() {
                //log.info("Miss " + missed);
                //log.info("Serv " + served);
                var frac = 1 - (served.toDouble()) / ((served + missed).toDouble())
                frac = floor(frac * 10000) / 10000
                frac = frac * 100
                return "$frac%"
            }
    }

    enum class EndReason {
        NONE,
        CANT_KEEP_UP,
        MISSED_FRAMES
    }

    companion object {
        private val log = LoggerFactory.getLogger(AudioConsumer::class.java)

        private val served = AtomicInteger()
        private val missed = AtomicInteger()
        private var endReason = EndReason.NONE
        private var running = true
        private const val INTERVAL = 20 // A frame is 20ms

        val results: Results
            get() {
                val serv = served.getAndSet(0)
                val miss = missed.getAndSet(0)

                if ((serv + miss) / 100 < miss) {
                    endReason = EndReason.MISSED_FRAMES
                    running = false
                }

                return Results(serv, miss, endReason)
            }
    }
}