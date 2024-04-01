package gg.botlabs

class AudioConsumer internal constructor(private val player: Player) : Thread("audio-consumer") {

    override fun run() {
        val start = System.currentTimeMillis()
        var i: Long = 0

        while (true) {
            player.tryProvide()

            val targetTime = ((start / INTERVAL) + i + 1) * INTERVAL
            val diff = targetTime - System.currentTimeMillis()
            i++

            try {
                if (diff > 0) sleep(diff / 2)
            } catch (_: InterruptedException) {
                break
            }
        }
    }

    companion object {
        private const val INTERVAL = 20 // A frame is 20ms
    }
}