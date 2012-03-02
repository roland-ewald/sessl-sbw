package sessl.util
import scala.collection.mutable.ListBuffer

import sessl.Algorithm

/** Represents a (multi-)set of sessl algorithm representations. For convenience, the order
 *  with which the elements are added is currently preserved.
 *
 *  @param <T> the type of the algorithm contained in the list
 *  @author Roland Ewald
 */
case class AlgorithmSet[T <: Algorithm](initialAlgos: Seq[T]) {

  /** The list containing the algorithms.*/
  private val algorithmList = {
    val algos = ListBuffer[T]()
    algos ++= initialAlgos
    algos
  }

  /** Empty constructor for convenience.*/
  def this() = this(Seq())

  /** Adding elements to the set. */
  def <<(algos: Seq[T]) = { algorithmList ++= algos }
  def +=(algos: Seq[T]) = <<(algos)

  /** Get the defined algorithms. */
  lazy val algorithms: List[T] = algorithmList.toList

  /** Get the set of defined algorithms*/
  lazy val algorithmSet: Set[T] = {
    val set = Set(algorithms: _*)
    if (set.size != algorithms.size)
      println("Warning: there are duplicated elements in algorithms set:\n" + algorithms.mkString("\n")) //TODO: use logging here!
    set
  }

  /** Checks if algorithm set is empty. */
  def isEmpty = algorithmList.isEmpty

  /** Get the size. */
  def size = algorithmList.size
}

object AlgorithmSet {

  /** Simple static constructor. */
  def apply[T <: Algorithm](): AlgorithmSet[T] = new AlgorithmSet()
}