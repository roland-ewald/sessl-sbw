package sessl

import _root_.james.core.parameters.ParameterBlock
import _root_.james.core.factories.Factory
import _root_.james.core.parameters.ParameterizedFactory
import _root_.james.core.experiments.tasks.ComputationTaskIDObject
import _root_.james.SimSystem
import _root_.james.core.experiments.BaseExperiment
import java.util.logging.Level
import scala.collection.mutable.ListBuffer

package object james {

  /** The basic factory type.  */
  type Factory = _root_.james.core.factories.Factory

  /** The abstract factory type. */
  type AbstractFactory[T <: Factory] = _root_.james.core.factories.AbstractFactory[T]

  /** The parameter type. */
  type ParamBlock = _root_.james.core.parameters.ParameterBlock

  /** The parameterized factory. */
  type ParamFactory[X <: Factory] = _root_.james.core.parameters.ParameterizedFactory[X]

  /** The stop policy factory. */
  type StopFactory = _root_.james.core.experiments.tasks.stoppolicy.plugintype.ComputationTaskStopPolicyFactory

  /** The pair type in James II (there is a pre-defined pair type in Scala).*/
  type JamesPair[X, Y] = _root_.james.core.util.misc.Pair[X, Y]

  /** A reference to the registry. */
  lazy val Registry = SimSystem.getRegistry();

  /**
   * Some wrappers for {@link SimSystem#report}.
   */
  //TODO: Check why not all wrappers can have the same name in package object
  //  def report(t: Throwable) = SimSystem.report(t)
  //  def report(msg: String) = SimSystem.report(Level.INFO, msg)
  def reportDetails(level: Level, msg: String, t: Throwable) = SimSystem.report(level, msg, t)
  def report(level: Level, msg: String) = SimSystem.report(level, msg)

  /**
   * Get a factory from a registry.
   * @param abstrFactoryClass the class of the abstract factory
   * @param parameters the parameters to be used
   */
  def getFactory[T <: Factory](abstrFactoryClass: java.lang.Class[_ <: AbstractFactory[T]], parameters: ParameterBlock): T =
    SimSystem.getRegistry().getFactory(abstrFactoryClass, parameters)

  /**
   * Create a new random number generator.
   * @return new RNG
   */
  def nextRNG() = SimSystem.getRNGGenerator().getNextRNG()

  /**
   * Conversion Param => ParameterBlock.
   */
  implicit def paramToParameterBlock(parameter: Param): ParameterBlock = {
    val paramBlock = new ParameterBlock(parameter.value.getOrElse(null))
    val subBlocks = for (child <- parameter.childs) yield (child._1, paramToParameterBlock(child._2))
    for (subBlock <- subBlocks)
      paramBlock.addSubBl(subBlock._1, subBlock._2)
    return paramBlock
  }

  /**
   * Conversion ParameterBlock => Param.
   */
  implicit def parameterBlockToParam(paramBlock: ParameterBlock): Param = {
    val subBlockIt = paramBlock.getSubBlocks().entrySet().iterator()
    val childs = ListBuffer[(String, Param)]()
    while (subBlockIt.hasNext()) {
      val subBlockEntry = subBlockIt.next()
      childs += ((subBlockEntry.getKey(), parameterBlockToParam(subBlockEntry.getValue())))
    }
    new Param("", if (paramBlock.getValue() == null) None else Some(paramBlock.getValue()), childs.toList.toMap)
  }

  /** Conversion from computation task IDs to run IDs. */
  implicit def compTaskIDObjToRunID(compTaskID: ComputationTaskIDObject): Int = compTaskID.toString.hashCode
}