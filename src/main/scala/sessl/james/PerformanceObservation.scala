package sessl.james

import scala.collection.mutable.Map
import james.core.experiments.taskrunner.ITaskRunner
import james.core.experiments.ComputationTaskRuntimeInformation
import sessl.AbstractPerformanceObservation
import sessl.PerfObsRunResultsAspect
import sessl.PerfObsRunResultsAspect
import sessl.Simulator
import sessl.SupportSimulatorConfiguration
import james.perfdb.util.ParameterBlocks
import james.core.parameters.ParameterBlock
import james.core.processor.plugintype.ProcessorFactory

/** Support for performance observation in James II.
 *  @author Roland Ewald
 */
trait PerformanceObservation extends AbstractPerformanceObservation {
  this: Experiment with SupportSimulatorConfiguration =>

  /** The run performances, associated with their run ids.*/
  private[this] val runPerformances = Map[Int, PerfObsRunResultsAspect]()

  /** The parameter block string representations, mapped back to their corresponding setups. */
  private[this] val setups = Map[String, Simulator]()

  override def configure() {
    super.configure()
    try {
      // Read out all defined algorithm setups
      simulatorSet.algorithms.foreach(algo => {
        val representation = ParameterBlocks.toUniqueString(ParamBlockGenerator.createParamBlock(algo.asInstanceOf[JamesIIAlgo[Factory]]))
        setups(representation) = algo
      })

      // Fill the run performances map with actual data from the execution listener
      exp.getExecutionController().addExecutionListener(new ExperimentExecutionAdapter() {
        
        override def simulationExecuted(taskRunner: ITaskRunner,
          crti: ComputationTaskRuntimeInformation, jobDone: Boolean): Unit = {
          val representation = ParameterBlocks.toUniqueString(
            ParameterBlock.getSubBlock(crti.getComputationTask().getConfig().getExecParams(), classOf[ProcessorFactory].getName()))
          if (setups.contains(representation)) {
            println("SUCCESS FOR:" + representation)
            runPerformances(crti.getComputationTaskID) =
              new PerfObsRunResultsAspect(setups(representation), crti.getRunInformation().getComputationTaskRunTime())
          } else println("WARNING: no setup found for parameter block representation: " + representation)
        }
      })

    } catch {
      case ex => ex.printStackTrace()
    }
  }

  override def collectResults(runId: Int, removeData: Boolean): PerfObsRunResultsAspect = {
    val runPerformance = if (removeData)
      runPerformances.remove(runId)
    else
      runPerformances.get(runId)
    runPerformance.getOrElse({
      println("Warning: no performance result for run with id '" + runId +
        "' - returning special results aspect to signal the failure."); new PerfObsRunResultsAspect(null, -1)
    }) //TODO: use logging here!
  }

}