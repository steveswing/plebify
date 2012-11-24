//
// Copyright 2012 Vibul Imtarnasan and other Plebify contributors.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package org.mashupbots.plebify.http

import java.util.Date
import scala.collection.immutable.List.apply
import org.mashupbots.plebify.core.EventData
import org.mashupbots.plebify.core.EventNotification
import org.mashupbots.plebify.core.EventSubscriptionRequest
import akka.camel.CamelMessage
import akka.camel.Consumer
import org.apache.camel.Exchange

/**
 * Starts a websocket client and listens for incoming text frames
 *
 * ==Parameters==
 *  - '''uri''': See [[http://camel.apache.org/websocket.html Apache Camel websocket component]] for options.
 *  - '''mime-type''': Mime type of the incoming data. Defaults to 'text/plain'
 *
 * ==Event Data==
 *  - '''Date''': Timestamp when event occurred
 *  - '''Content''': Contents of the file
 *  - '''ContentLength''': Length of the content
 *  - '''ContentType''': defaults to 'text/plain' if not set in the above parameter
 *
 * @param request Subscription request
 */
class FrameReceivedEvent(request: EventSubscriptionRequest) extends Consumer with akka.actor.ActorLogging {

  def endpointUri = request.config.params("uri")
  require(endpointUri.startsWith("websocket:"), s"$endpointUri must start with 'websocket:'")

  val contentType = request.config.params.getOrElse("mime-type", "text/plain")

  def receive = {
    case msg: CamelMessage =>
      try {
        log.debug("FrameReceivedEvent: {}", msg)

        val content = if (msg.body == null) "" else msg.bodyAs[String]
        val data: Map[String, String] = Map(
          (EventData.Id, EventData.readCamelHeader(msg, Exchange.BREADCRUMB_ID)),
          (EventData.Date, EventData.dateTimeToString(new Date())),
          (EventData.Content, content),
          (EventData.ContentLength, content.length.toString),
          (EventData.ContentType, contentType))

        request.job ! EventNotification(request.config, data)
      } catch {
        case ex: Throwable =>
          log.error(ex, "Error processing {}", msg)
      }
  }
}