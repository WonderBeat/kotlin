/*
 * Copyright 2010-2013 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.jet.cli.common;

import org.jetbrains.jet.lang.resolve.BindingTraceContext;
import org.jetbrains.jet.util.slicedmap.ReadOnlySlice;
import org.jetbrains.jet.util.slicedmap.Slices;
import org.jetbrains.jet.util.slicedmap.WritableSlice;

public class SliceTest {
    static final ReadOnlySlice<Integer, String> DESCRIPTOR_TO_DECLARATION =
            Slices.<Integer, String>sliceBuilder().setKeyNormalizer(Slices.KeyNormalizer.DO_NOTHING).setDebugName("DESCRIPTOR_TO_DECLARATION").build();

    static WritableSlice<String, Integer> FUNCTION = Slices.<String, Integer>sliceBuilder()
            .setOpposite((WritableSlice) DESCRIPTOR_TO_DECLARATION).setDebugName("FUNCTION").build();

    static ReadOnlySlice<String, Integer> DECLARATION_TO_DESCRIPTOR = Slices.<String, Integer>sliceBuilder()
            .setFurtherLookupSlices(new ReadOnlySlice[] { FUNCTION }).build();

    public static void main(String[] args) {
        BindingTraceContext context = new BindingTraceContext();
        context.record(FUNCTION, "Hello", 12);
        // System.out.println(context.get(DESCRIPTOR_TO_DECLARATION, 12));
        System.out.println(context.get(DECLARATION_TO_DESCRIPTOR, "Hello"));
    }
}
