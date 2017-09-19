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

package com.palantir.atlasdb.timelock.perf;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.palantir.atlasdb.http.AtlasDbHttpClients;
import com.palantir.atlasdb.timelock.benchmarks.BenchmarksService;

public class BenchmarkRunnerBase {

    private static final String BENCHMARK_SERVER = readBenchmarkServerUri();

    private static final ObjectMapper MAPPER = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    protected static final BenchmarksService client = createClient();

    protected void runAndPrintResults(BiFunction<Integer, Integer, Map<String, Object>> test, int numClients,
            int numRequestsPerClient) {
        Map<String, Object> results = test.apply(numClients, numRequestsPerClient);
        printResults(results);
    }

    protected void runAndPrintResults(Supplier<Map<String, Object>> benchmark) {
        Map<String, Object> results = benchmark.get();
        printResults(results);
    }

    protected void printResults(Map<String, Object> results) {
        try {
            System.out.println(MAPPER.writeValueAsString(results));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected static final BenchmarksService createClient() {
        // TODO(nziebart): set a longer timeout here
        return AtlasDbHttpClients.createProxy(Optional.empty(), BENCHMARK_SERVER, BenchmarksService.class);
    }

    private static String readBenchmarkServerUri() {
        try {
            for (String line : Files.readLines(new File("../scripts/benchmarks/servers.txt"), Charsets.UTF_8)) {
                if (line.startsWith("CLIENT")) {
                    return "http://" + StringUtils.split(line, '=')[1] + ":9425";
                }
            }

            throw new IllegalStateException("CLIENT declaration not found in servers.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}