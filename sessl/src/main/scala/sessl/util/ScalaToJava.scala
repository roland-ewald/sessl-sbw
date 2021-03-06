/**
 * *****************************************************************************
 * Copyright 2012 Roland Ewald
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
package sessl.util

import java.util.ArrayList

import scala.collection.JavaConversions._

/**
 * Helper functions for some often-used Scala-to-Java conversions.
 *
 * Most of the time, you should instead use [[scala.collection.JavaConversions]] directly; these are just helpers with
 * more specific signatures, and for some special cases.
 */
object ScalaToJava {

  /** Creates an array list of java Double objects.*/
  def toDoubleList(values: Iterable[Double]): java.util.List[java.lang.Double] = {
    val rv = new ArrayList[java.lang.Double]()
    values.foreach(v => rv.add(v))
    rv
  }

  /** Creates an array list of java Integer objects.*/
  def toIntegerList(values: Iterable[Int]): java.util.List[java.lang.Integer] = {
    val rv = new ArrayList[java.lang.Integer]()
    values.foreach(v => rv.add(v))
    rv
  }

  /** Creates an array list.*/
  def toList[X](values: Iterable[X]): java.util.List[X] = seqAsJavaList(values.toSeq)

  /** Convert map. */
  def toMap[X, Y](map: Map[X, Y]): java.util.Map[X, Y] = mapAsJavaMap(map)

  /** Convert list to double array. */
  def toDoubleArray(values: Iterable[Double]): Array[java.lang.Double] = values.map(_.asInstanceOf[java.lang.Double]).toArray

  /** Convert list to (transposed) nested array. */
  def to2DTransposedJavaStringArray(valueLists: Seq[String]*): Array[Array[java.lang.String]] = {
    if (valueLists.length == 0)
      return Array.ofDim(0)
    val rv: Array[Array[java.lang.String]] = Array.ofDim(valueLists.head.length)
    for (i <- valueLists.head.indices)
      rv(i) = Array.ofDim(valueLists.length)
    for (i <- valueLists.head.indices; j <- valueLists.indices)
      rv(i)(j) = valueLists(j)(i)
    rv
  }

  /** Convert list to nested array. */
  def to2DJavaDoubleArray(valueLists: Iterable[Double]*) = {
    val rv: Array[Array[java.lang.Double]] = Array.ofDim(valueLists.length)
    for (i <- valueLists.indices)
      rv(i) = toDoubleArray(valueLists(i))
    rv
  }

}
