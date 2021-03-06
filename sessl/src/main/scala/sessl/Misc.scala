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
package sessl

/**
 * Miscellaneous (auxiliary) functions.
 *
 * @author Roland Ewald
 */
object Misc {

  /**
   * Checks whether trajectories contains numbers (only first element is checked for each).
   *  @param ts the trajectories to be tested
   */
  def requireNumericTrajectories(ts: Trajectory*) = require(ts.forall(_.head._2.isInstanceOf[Number]), "Trajectory values need to be numbers.")

  /**
   * Compute root mean squared error (RMSE) of values from two trajectories that are
   * assumed to have exactly the same support points on the x-axis.
   * @param t1 the first trajectory
   * @param t2 the second trajectory
   */
  def rmse(t1: Trajectory, t2: Trajectory): Double = Math.sqrt(mse(t1, t2))

  /**
   * Compute mean squared error (MSE) of values from two trajectories that are
   * assumed to have exactly the same support points on the x-axis.
   * @param t1 the first trajectory
   * @param t2 the second trajectory
   */
  def mse(t1: Trajectory, t2: Trajectory): Double = {
    requireNumericTrajectories(t1, t2)
    require((t1 zip t2).forall(ts => ts._1._1 == ts._2._1), "Trajectories need to have same support points for RMSE calculation.")
    util.math.Misc.mse(t1.map(doubleVal), t2.map(doubleVal))
  }

  /**
   * Returns the value of a time-stamped data element. Assumption: it is a number, s use with care.
   *  @param d the time-stamped data
   *  @return its value as Double
   */
  def doubleVal(d: TimeStampedData) = d._2.asInstanceOf[Number].doubleValue()

}