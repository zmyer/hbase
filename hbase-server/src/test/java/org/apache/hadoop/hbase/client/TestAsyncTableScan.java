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
package org.apache.hadoop.hbase.client;

import com.google.common.base.Throwables;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Supplier;

import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
@Category({ LargeTests.class, ClientTests.class })
public class TestAsyncTableScan extends AbstractTestAsyncTableScan {

  private static final class SimpleScanResultConsumer implements ScanResultConsumer {

    private final List<Result> results = new ArrayList<>();

    private Throwable error;

    private boolean finished = false;

    @Override
    public synchronized boolean onNext(Result result) {
      results.add(result);
      return true;
    }

    @Override
    public synchronized void onError(Throwable error) {
      this.error = error;
      finished = true;
      notifyAll();
    }

    @Override
    public synchronized void onComplete() {
      finished = true;
      notifyAll();
    }

    public synchronized List<Result> getAll() throws Exception {
      while (!finished) {
        wait();
      }
      if (error != null) {
        Throwables.propagateIfPossible(error, Exception.class);
        throw new Exception(error);
      }
      return results;
    }
  }

  @Parameter
  public Supplier<Scan> scanCreater;

  @Parameters
  public static List<Object[]> params() {
    return Arrays.asList(new Supplier<?>[] { TestAsyncTableScan::createNormalScan },
      new Supplier<?>[] { TestAsyncTableScan::createBatchScan },
      new Supplier<?>[] { TestAsyncTableScan::createSmallResultSizeScan },
      new Supplier<?>[] { TestAsyncTableScan::createBatchSmallResultSizeScan });
  }

  private static Scan createNormalScan() {
    return new Scan();
  }

  private static Scan createBatchScan() {
    return new Scan().setBatch(1);
  }

  // set a small result size for testing flow control
  private static Scan createSmallResultSizeScan() {
    return new Scan().setMaxResultSize(1);
  }

  private static Scan createBatchSmallResultSizeScan() {
    return new Scan().setBatch(1).setMaxResultSize(1);
  }

  @Override
  protected Scan createScan() {
    return scanCreater.get();
  }

  @Override
  protected List<Result> doScan(Scan scan) throws Exception {
    AsyncTable table = ASYNC_CONN.getTable(TABLE_NAME, ForkJoinPool.commonPool());
    SimpleScanResultConsumer consumer = new SimpleScanResultConsumer();
    table.scan(scan, consumer);
    List<Result> results = consumer.getAll();
    if (scan.getBatch() > 0) {
      results = convertFromBatchResult(results);
    }
    return results;
  }

}
