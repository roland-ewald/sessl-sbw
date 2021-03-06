/**
 * *****************************************************************************
 * Copyright 2013 Roland Ewald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ****************************************************************************
 */
package sessl.james.formalismspecific

import org.jamesii.core.model.IModel
import org.jamesii.core.experiments.tasks.IComputationTask
import model.sr.ISRModel
import org.jamesii.core.observe.Mediator
import sessl.util.SimpleObserverHelper
import org.jamesii.core.observe.IObservable
import org.jamesii.core.experiments.optimization.parameter.IResponseObserver
import sessl.james.Experiment
import model.sr.snapshots.SRSnapshotObserver
import sessl.util.SimpleObservation
import sessl.james.SESSLInstrumenter
import sessl.util.ScalaToJava
import sessl.james.util.SimpleJAMESIIObserverHelper

/**
 * Handles the instrumentation for species-reaction models.
 *
 * @author Roland Ewald
 *
 */
class SRInstrumentationHandler extends InstrumentationHandler {

  override def applicable(task: IComputationTask): Boolean = task.getModel().isInstanceOf[ISRModel]

  override def configureObservers(task: IComputationTask, instrumenter: SESSLInstrumenter, outputDir:Option[String]): Seq[IResponseObserver[_ <: IObservable]] = {

    val model = task.getModel().asInstanceOf[ISRModel]
    val obsTimes = ScalaToJava.toDoubleArray(instrumenter.instrConfig.observationTimes)

    val varsToBeObserved = instrumenter.instrConfig.varsToBeObserved

    val observer = new SRSnapshotObserver[ISRModel](obsTimes, 1000) with SimpleJAMESIIObserverHelper[SimpleObservation] {

      configureTaskObservation(instrumenter, task)
      setConfig(instrumenter.instrConfig)
      
      /** If the SR snapshot observer decides to store the data, we have to collect it too.*/
      override def store() = {
        val state = model.getState()
        val time = model.getTime()
        val speciesMap = model.getObjectMapping()
        super.store()
        for (varToBeObserved <- sesslObsConfig.varsToBeObserved) {
          val amount = state.get(speciesMap.get(varToBeObserved))
          addValueFor(varToBeObserved, (time, amount))
        }
      }

    }

    Mediator.create(model)
    model.registerObserver(observer)
    Seq(observer)
  }
}