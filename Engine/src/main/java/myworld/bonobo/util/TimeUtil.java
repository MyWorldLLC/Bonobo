/*
 * Copyright 2022 MyWorld, LLC
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package myworld.bonobo.util;

public class TimeUtil {

    public static final double MILLIS_PER_SECOND = 1e3;
    public static final double NANOS_PER_SECOND = 1e9;
    public static final long NANOS_PER_MILLI = 1_000_000;

    public static long secondsToMillis(double seconds){
        return Math.round(seconds * MILLIS_PER_SECOND);
    }

    public static double millisToSeconds(long millis){
        return millis / MILLIS_PER_SECOND;
    }

    public static long millisToNanos(long millis){
        return millis * NANOS_PER_MILLI;
    }
}
