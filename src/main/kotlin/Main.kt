package gg.botlabs

import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import java.util.concurrent.Executors

object Main

fun main() {
    println("Press enter to start")
    System.`in`.read()

    val identifier = "https://soundcloud.com/zimmermusic/zimmer-summer-2023-tape"
    val apm = DefaultAudioPlayerManager()
    AudioSourceManagers.registerRemoteSources(apm)
    val track = PlaylistLoader(apm).loadTracksSync(identifier).first()

    val executor = Executors.newCachedThreadPool()
    repeat(1000) {
        Thread.sleep(10)
        val player = Player(track.makeClone(), apm, false)
        executor.submit { player.destroy() }
        println(it)
    }

    Thread.sleep(Long.MAX_VALUE)
}