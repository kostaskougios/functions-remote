package examples

import io.helidon.logging.common.LogConfig
import io.helidon.webclient.websocket.WsClient
import io.helidon.webserver.WebServer
import io.helidon.webserver.websocket.WsRouting
import io.helidon.websocket.{WsListener, WsSession}

import java.net.URI

@main def tryHelidonWs(): Unit =
  LogConfig.configureRuntime()
  val Port           = 8082
  val serverListener = new WsListener {
    override def onMessage(session: WsSession, text: String, last: Boolean): Unit =
      println(s"Server: received $text")
  }
  val wsB            = WsRouting.builder().endpoint("/ws-test", serverListener)
  val server         = WebServer.builder
    .port(Port)
    .addRouting(wsB)
    .build
    .start
  val uri            = URI.create(s"ws://localhost:$Port")
  val wsClient       = WsClient.builder
    .baseUri(uri)
    .build

  val clientWsListener = new WsListener {
    override def onOpen(session: WsSession): Unit = {
      val s = "*Hello* " * 1024
      println(s"Client sending $s")
      try session.send(s, true)
      catch case t: Throwable => t.printStackTrace()
    }
  }
  wsClient.connect("/ws-test", clientWsListener)
  try
    println("Started")
    Thread.sleep(4000)
  finally server.stop()
