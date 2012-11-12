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
package org.mashupbots.plebify.core.config

import akka.actor.Extension
import com.typesafe.config.Config

/**
 * Event subscription configuration for a job. 
 * 
 * Details the condition that a notification will be sent to the specified job when this event fires.
 * 
 * @param id Unique id of this event subscription. Must be in the format `{connector id}-{event}[-optional-text]`.
 * @param description Description of this event subscription
 * @param params Parameters for this event subscription
 */
case class EventSubscriptionConfig(
  id: String,
  description: String,
  params: Map[String, String]) extends Extension {

  /**
   * Read configuration from AKKA's `application.conf`
   *
   * @param id Unique identifier of this event subscription. Must be in the format 
   *   `{connector id}-{event}[-optional-text]`.
   * @param config Raw akka configuration
   * @param keyPath Dot delimited key path to this trigger configuration
   */
  def this(id: String, config: Config, keyPath: String) = this(
    id,
    ConfigUtil.getString(config, s"$keyPath.description", ""),
    ConfigUtil.getParameters(config, keyPath, List("description")))

  private val splitId = id.split("-")
  require(splitId.length >= 2, s"job id '$id' must be in the format 'connector-event'")

  /**
   * Id of the connector to listen to
   */
  val connectorId = splitId(0)

  /**
   * Name of the connector event to which to subscribe 
   */
  val eventName = splitId(1)

}