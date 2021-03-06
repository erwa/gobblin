/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.gobblin.hive;

import java.util.Arrays;
import lombok.AllArgsConstructor;

import java.io.IOException;

import org.apache.hadoop.hive.metastore.api.Partition;
import org.apache.hadoop.hive.metastore.api.Table;

import com.google.common.base.Optional;

import org.apache.gobblin.commit.CommitStep;
import org.apache.gobblin.hive.metastore.HiveMetaStoreUtils;


/**
 * {@link CommitStep} to deregister a Hive partition.
 */
@AllArgsConstructor
public class PartitionDeregisterStep implements CommitStep {

  private Table table;
  private Partition partition;
  private final Optional<String> metastoreURI;
  private final HiveRegProps props;

  @Override
  public boolean isCompleted() throws IOException {
    return false;
  }

  @Override
  public void execute() throws IOException {
    HiveTable hiveTable = HiveMetaStoreUtils.getHiveTable(this.table);
    try (HiveRegister hiveRegister = HiveRegister.get(this.props, this.metastoreURI)) {
      hiveRegister.dropPartitionIfExists(this.partition.getDbName(), this.partition.getTableName(),
      hiveTable.getPartitionKeys(), this.partition.getValues());
    }
  }

  @Override
  public String toString() {
    return String.format("Deregister partition %s.%s %s on Hive metastore %s.", this.partition.getDbName(),
        this.partition.getTableName(), Arrays.toString(this.partition.getValues().toArray()),
        this.metastoreURI.isPresent() ? this.metastoreURI.get() : "default");
  }
}
