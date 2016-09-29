/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
package org.apache.hadoop.hbase.shaded.util;

import org.apache.hadoop.hbase.classification.InterfaceAudience;

import com.google.protobuf.ByteString;
import com.google.protobuf.UnsafeByteOperations;

/**
 * Hack to workaround HBASE-10304 issue that keeps bubbling up when a mapreduce context.
 */
// Depends on protobuf-3.1.0 feature.
@InterfaceAudience.Private
public class ByteStringer {
  private ByteStringer() {
    super();
  }

  /**
   * Wraps a byte array in a {@link ByteString} without copying it.
   */
  public static ByteString wrap(final byte[] array) {
    return UnsafeByteOperations.unsafeWrap(array);
  }

  /**
   * Wraps a subset of a byte array in a {@link ByteString} without copying it.
   */
  public static ByteString wrap(final byte[] array, int offset, int length) {
    return UnsafeByteOperations.unsafeWrap(array, offset, length);
  }
}
