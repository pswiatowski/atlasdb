/*
 * Copyright 2017 Palantir Technologies, Inc. All rights reserved.
 *
 * Licensed under the BSD-3 License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.palantir.timelock.paxos;

import java.util.function.Supplier;

import com.palantir.atlasdb.timelock.TimeLockServices;
import com.palantir.atlasdb.timelock.paxos.ManagedTimestampService;
import com.palantir.lock.LockService;

public interface TimeLockServicesCreator {
    /**
     * Creates a TimeLockServices object for the given client, based on the provided timestamp service supplier
     * and lock service supplier.
     */
    TimeLockServices createTimeLockServices(
            String client,
            Supplier<ManagedTimestampService> rawTimestampServiceSupplier,
            Supplier<LockService> rawLockServiceSupplier);
}
