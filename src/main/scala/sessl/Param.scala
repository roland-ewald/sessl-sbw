package sessl

/** Support for parameters. In contrast to variables, parameters can be nested hierarchically.
 *
 *  @see sessl.Variable
 *
 *  @author Roland Ewald
 *
 */
sealed case class Param(name: String, value: Option[Any], childs: Map[String, Param]) {
  def :/(newChilds: Param*) = new Param(name, value, childs ++ Param.createChildMap(newChilds))
}

/** Syntactic sugar to define simple parameter structures. */
sealed case class ParamName(name: String) {
  def ~>(value: Any) = Param(name, value)
}

/** Some factory methods. */
object Param {

  /** Create a new empty parameter. */
  def apply() = new Param("", None, Map())

  /** Create a named empty parameter. */
  def apply(name: String) = new Param(name, None, Map())

  /** Create a parameter with name and value, but without children. */
  def apply(name: String, value: Any) = new Param(name, Some(value), Map())

  /** Create a named parameter with children. */
  def apply(name: String, parameters: Param*) = new Param(name, None, createChildMap(parameters))

  /** Create a fully specified parameter (name, value, children). */
  def apply(name: String, value: Any, parameters: Param*) = {
    new Param(name, Some(value), createChildMap(parameters))
  }

  /** Creates a child map for a sequence of parameters. */
  private def createChildMap(parameters: Seq[Param]): Map[String, Param] = {
    val childs = collection.mutable.Map[String, Param]()
    for (parameter <- parameters)
      childs += ((parameter.name, parameter))
    childs.toMap
  }
}