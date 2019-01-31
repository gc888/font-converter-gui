/*
 * Copyright 2011 Google Inc. All Rights Reserved.
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

package com.google.typography.font.tools.subsetter;

import java.util.ArrayList;
import java.util.List;

import com.google.typography.font.sfntly.Font;
import com.google.typography.font.sfntly.Tag;
import com.google.typography.font.sfntly.table.extra.VerticalMetricsTable;

/** @author Raph Levien */
public class VerticalMetricsTableSubsetter extends TableSubsetterImpl {

  protected VerticalMetricsTableSubsetter() {
    super(Tag.vmtx, Tag.vhea);
  }

  @Override
  public boolean subset(Subsetter subsetter, Font font, Font.Builder fontBuilder) {
    List<Integer> permutationTable = subsetter.glyphMappingTable();
    if (permutationTable == null) {
      return false;
    }
    VerticalMetricsTable origMetrics = font.getTable(Tag.vmtx);
    List<VerticalMetricsTableBuilder.LongVerMetric> metrics = new ArrayList<>();
    for (int origGlyphId : permutationTable) {
      int advanceHeight = origMetrics.advanceHeight(origGlyphId);
      int tsb = origMetrics.topSideBearing(origGlyphId);
      metrics.add(new VerticalMetricsTableBuilder.LongVerMetric(advanceHeight, tsb));
    }
    new VerticalMetricsTableBuilder(fontBuilder, metrics).build();
    return true;
  }
}
