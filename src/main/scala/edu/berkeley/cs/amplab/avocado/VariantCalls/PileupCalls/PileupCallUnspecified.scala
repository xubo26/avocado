/*
 * Copyright (c) 2013. Regents of the University of California
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.berkeley.cs.amplab.avocado.calls.pileup

import edu.berkeley.cs.amplab.adam.models.{ADAMRod, ADAMVariantContext}
import edu.berkeley.cs.amplab.avocado.Avocado
import org.apache.spark.{SparkContext, Logging}
import org.apache.spark.rdd.RDD

/**
 * Generic placeholder for calling variants on reads. Should be used to designate that a section of the genome
 * should be called with pileups, but without designating a specific variant calling algorithm.
 */
class PileupCallUnspecified extends PileupCall {

  val callName = "unspecifiedPileup"
 
  /**
   * Empty calling method.
   */
  override def call (pileups: RDD [ADAMRod]): RDD [ADAMVariantContext] = {
    throw new IllegalArgumentException (callName + " is not callable.")
  }

  // Call is generic, so is not callable
  override def isCallable () = false
}
