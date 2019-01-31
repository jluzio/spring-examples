package org.example.work;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;

@Component
public class WorkWagesFileRenamer {

	public void run(Path inputPath, Path outputPath) throws IOException {
		List<String> months = Arrays.asList("janeiro", "fevereiro", "marco", "abril", "maio", "junho", "julho", "agosto", "setembro", "outubro", "novembro", "dezembro");
		if (!Files.isDirectory(outputPath)) {
			Files.createDirectories(outputPath);
		}
		Files.list(inputPath).forEach(path -> {
			String pattern = "(\\d+-)?0393Rec([^\\.]+)\\.pdf";
			Matcher matcher = Pattern.compile(pattern).matcher(path.getFileName().toString());
			if (matcher.find()) {
				String yearOptionalPart = matcher.group(1);
				String monthPart = matcher.group(2);
				String monthPartNormalized = Normalizer.normalize(monthPart, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
				int monthIndex = months.indexOf(monthPartNormalized.toLowerCase());
				int currentYear = LocalDate.now().getYear();
				String yearOptional = StringUtils.removeEnd(yearOptionalPart, "-");
				String filename = String.format("work_wages-%s%s.pdf",
					MoreObjects.firstNonNull(yearOptional, String.valueOf(currentYear)),
					Strings.padStart(String.valueOf(monthIndex+1), 2, '0')
				);
				try {
          Files.move(path, outputPath.resolve(filename));
        } catch (IOException e) {
          Throwables.throwIfUnchecked(e);
        }
			}
		});
	}

}
