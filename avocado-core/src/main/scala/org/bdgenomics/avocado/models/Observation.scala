/**
 * Licensed to Big Data Genomics (BDG) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The BDG licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bdgenomics.avocado.models

/**
 * A generic class that stores likelihoods and simple annotations.
 *
 * @param alleleForwardStrand The number of reads covering the allele observed
 *   on the forward strand.
 * @param otherForwardStrand The number of reads covering the site but not
 *   matching the allele observed on the forward strand.
 * @param squareMapQ The sum of the squares of the mapping qualities observed.
 * @param alleleLogLikelihoods The log likelihoods that 0...n copies of this
 *   allele were observed.
 * @param otherLogLikelihoods The log likelihoods that 0...n copies of another
 *   allele were observed.
 * @param alleleCoverage The total number of reads observed that cover the
 *   site and match the allele.
 * @param otherCoverage The total number of reads observed that cover the site
 *   but that do not match the allele.
 * @param totalCoverage The total number of reads that cover the site.
 * @param isRef True if this allele matches the reference.
 */
case class Observation(alleleForwardStrand: Int,
                       otherForwardStrand: Int,
                       squareMapQ: Double,
                       alleleLogLikelihoods: Array[Double],
                       otherLogLikelihoods: Array[Double],
                       alleleCoverage: Int,
                       otherCoverage: Int,
                       totalCoverage: Int = 1,
                       isRef: Boolean = true) {

  override def toString: String = {
    "Observation(%d, %d, %f, Array(%s), Array(%s), %d, %d, %d, %s)".format(
      alleleForwardStrand,
      otherForwardStrand,
      squareMapQ,
      alleleLogLikelihoods.mkString(","),
      otherLogLikelihoods.mkString(","),
      alleleCoverage,
      otherCoverage,
      totalCoverage,
      isRef)
  }

  /**
   * @return The total coverage of this site.
   */
  def coverage: Int = alleleCoverage + otherCoverage

  /**
   * @return The copy number of this site.
   */
  def copyNumber = alleleLogLikelihoods.length - 1

  assert(copyNumber == (otherLogLikelihoods.length - 1) &&
    copyNumber > 0)
  assert(squareMapQ >= 0.0)
  assert(alleleCoverage >= 0 && otherCoverage >= 0 && coverage >= 0 && totalCoverage > 0)
  assert(alleleForwardStrand >= 0 && alleleCoverage >= alleleForwardStrand &&
    otherForwardStrand >= 0 && otherCoverage >= otherForwardStrand)

  /**
   * @return Makes a copy where underlying arrays are not shared.
   */
  def duplicate(setRef: Option[Boolean] = None): Observation = {
    Observation(alleleForwardStrand,
      otherForwardStrand,
      squareMapQ,
      alleleLogLikelihoods.map(v => v),
      otherLogLikelihoods.map(v => v),
      alleleCoverage,
      otherCoverage,
      totalCoverage = totalCoverage,
      isRef = setRef.getOrElse(isRef))
  }

  /**
   * @return Returns this observation, but with allele/other swapped.
   *
   * @see null
   */
  def invert: Observation = {
    Observation(otherForwardStrand,
      alleleForwardStrand,
      squareMapQ,
      otherLogLikelihoods.map(v => v),
      alleleLogLikelihoods.map(v => v),
      otherCoverage,
      alleleCoverage,
      totalCoverage = totalCoverage,
      isRef = !isRef)
  }

  /**
   * @return Returns this observation, but with all allele related fields
   *   nulled out.
   *
   * @see invert
   */
  def nullOut: Observation = {
    Observation(0,
      0,
      0,
      Array.fill(alleleLogLikelihoods.length)({ 0.0 }),
      alleleLogLikelihoods.map(v => v),
      0,
      0,
      totalCoverage = totalCoverage,
      isRef = false)
  }

  /**
   * Merges two observations.
   *
   * @note This method destructively updates the first observation by modifying
   *   the underlying likelihood arrays in place.
   *
   * @param obs Observation to merge with.
   * @return Returns a new observation that is the sum of the two input
   *   observations.
   */
  def merge(obs: Observation): Observation = {
    assert(copyNumber == obs.copyNumber)
    assert(isRef == obs.isRef)

    (0 to copyNumber).foreach(i => {
      alleleLogLikelihoods(i) += obs.alleleLogLikelihoods(i)
      otherLogLikelihoods(i) += obs.otherLogLikelihoods(i)
    })

    Observation(alleleForwardStrand + obs.alleleForwardStrand,
      otherForwardStrand + obs.otherForwardStrand,
      squareMapQ + obs.squareMapQ,
      alleleLogLikelihoods,
      otherLogLikelihoods,
      alleleCoverage + obs.alleleCoverage,
      otherCoverage + obs.otherCoverage,
      totalCoverage = totalCoverage + obs.totalCoverage,
      isRef = isRef)
  }
}
