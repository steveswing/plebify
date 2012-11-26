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
package org.mashupbots.plebify.file

import org.mashupbots.plebify.core.EventData
import org.mashupbots.plebify.core.TaskExecutionRequest
import org.mashupbots.plebify.core.config.TaskExecutionConfig

import akka.camel.CamelMessage
import akka.camel.Producer

/**
 * Save content to file task
 *
 * Saves data to the specified file
 *
 * ==Parameters==
 *  - '''uri''': See [[http://camel.apache.org/file2.html Apache Camel file component]] for options.
 *  - '''template''': Optional template for the contents of the file. If not specified, the value of `Contents` will
 *    be saved.
 *
 * ==Event Data==
 *  - '''Content''': Contents to save to file
 *
 * @param config Task configuration
 */
class SaveFileTask(config: TaskExecutionConfig) extends Producer with akka.actor.ActorLogging {

  def endpointUri = config.params("uri")

  val template = config.params.get("template")

  /**
   * Transforms TaskExecutionRequest into a CamelMessage
   */
  override def transformOutgoingMessage(msg: Any) = msg match {
    case msg: TaskExecutionRequest => {

      val contents = if (template.isDefined) EventData.mergeTemplate(template.get, msg.eventNotification.data)
      else msg.eventNotification.data(EventData.Content)

      CamelMessage(contents, Map.empty)
    }
  }
}