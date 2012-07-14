/*******************************************************************************
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
 ******************************************************************************/
package sessl

/** Support to configure a random-number generator.
 *  @author Roland Ewald
 */
trait SupportRNGSetup {

  /** The random number generator to be used (if set).*/
  protected[sessl] var randomNumberGenerator: Option[RNG] = None

  /** Getting/setting the RNG. */
  def rng_=(rand: RNG) = { randomNumberGenerator = Some(rand) }
  def rng: RNG = { randomNumberGenerator.get }

}
