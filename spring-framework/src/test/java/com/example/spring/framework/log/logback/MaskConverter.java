package com.example.spring.framework.log.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.CompositeConverter;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@AllArgsConstructor
@Setter
public class MaskConverter extends CompositeConverter<ILoggingEvent> {

  private Map<Pattern, String> patternMap;

  record PatternEntry(Pattern pattern, String replacement) {}

  @Override
  public String transform(ILoggingEvent event, String in) {
    return patternMap.entrySet().stream().reduce(
        in,
        (message, entry) -> {
          var pattern = entry.getKey();
          var replacement = entry.getValue();
          return pattern.matcher(message).replaceAll(replacement);
        },
        (s, s2) -> s2);
  }

  public void setPatternEntries(List<PatternEntry> patternEntries) {
    this.patternMap = patternEntries.stream()
        .collect(Collectors.toMap(PatternEntry::pattern, PatternEntry::replacement));
  }
}
