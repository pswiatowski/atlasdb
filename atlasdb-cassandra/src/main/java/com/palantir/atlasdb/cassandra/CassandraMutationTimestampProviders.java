/*
 * Copyright 2018 Palantir Technologies, Inc. All rights reserved.
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

package com.palantir.atlasdb.cassandra;

import java.util.Optional;
import java.util.function.LongSupplier;

public class CassandraMutationTimestampProviders {
    private CassandraMutationTimestampProviders() {
        // factory
    }

    /**
     * @return {@link CassandraMutationTimestampProvider} which behaves in line with existing behaviour.
     * As far as possible, users should switch to {@link this#singleLongSupplierBacked(LongSupplier)} with a
     * fresh timestamp source from AtlasDB to promote better Cassandra compaction behaviour.
     */
    public static CassandraMutationTimestampProvider legacy() {
        return new CassandraMutationTimestampProvider() {
            @Override
            public long getSweepSentinelWriteTimestamp() {
                return 0;
            }

            @Override
            public long getDeletionTimestamp(long atlasDeletionTimestamp) {
                return atlasDeletionTimestamp + 1;
            }
        };
    }

    public static CassandraMutationTimestampProvider singleLongSupplierBacked(LongSupplier longSupplier) {
        return new CassandraMutationTimestampProvider() {
            @Override
            public long getSweepSentinelWriteTimestamp() {
                return longSupplier.getAsLong();
            }

            @Override
            public long getDeletionTimestamp(long atlasDeletionTimestamp) {
                return longSupplier.getAsLong();
            }
        };
    }

    public static CassandraMutationTimestampProvider optionallyLongSupplierBacked(Optional<LongSupplier> longSupplier) {
        return longSupplier.map(CassandraMutationTimestampProviders::singleLongSupplierBacked)
                .orElseGet(CassandraMutationTimestampProviders::legacy);
    }
}
