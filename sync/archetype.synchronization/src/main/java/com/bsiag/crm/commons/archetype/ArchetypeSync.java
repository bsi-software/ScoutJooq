/*
 * Copyright (c) BSI Business Systems Integration AG. All rights reserved.
 * http://www.bsiag.com/
 */
package com.bsiag.crm.commons.archetype;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.scout.rt.platform.util.CollectionUtility;

public class ArchetypeSyncTest {

	private final static List<ReplaceRule> JAVA_REPLACE_RULES = Collections
			.unmodifiableList(Arrays.asList(new P_SymbolReplaceRule("$", "symbol_dollar"),
					new P_SymbolReplaceRule("#", "symbol_pound"), new P_SymbolReplaceRule("\\", "symbol_escape"),
					new P_PatternReplaceRule(Pattern.compile("com\\.acme\\.application"), "\\${package}")
	// new P_PatternReplaceRule(Pattern.compile("(\\W(I)?)Bsicrm(\\w)"),
	// "$1\\${classPrefix}$3"),
	// new P_PatternReplaceRule(Pattern.compile("\"Bsicrm\""),
	// "\"\\${classPrefix}\""),
	// new P_PatternReplaceRule(Pattern.compile("\\{archetype-task\\}"),
	// "\\${task}")
	));

	private final static List<ReplaceRule> REPLACE_RULES = Collections
			.unmodifiableList(Arrays.asList(new P_SymbolReplaceRule("$", "symbol_dollar"),
					new P_SymbolReplaceRule("#", "symbol_pound"), new P_SymbolReplaceRule("\\", "symbol_escape"),
					new P_PatternReplaceRule(Pattern.compile("com\\.acme\\.application"), "\\${groupId}")
	// new P_PatternReplaceRule(Pattern.compile("15.4.0-SNAPSHOT"), "\\${version}")
	));

	private final static List<ReplaceRule> DIRECTORY_TRANSFORM_RULES = Collections.unmodifiableList(
			Arrays.<ReplaceRule>asList(new P_PatternReplaceRule(Pattern.compile("/java/com/acme/application/"), "/java/")

			));

	private final static List<ReplaceRule> FILE_NAME_TRANSFORM_RULES = Collections
			.unmodifiableList(Arrays.<ReplaceRule>asList(
					new P_PatternReplaceRule(Pattern.compile("^(I)?Bsicrm(.*\\.(java|xml))$"), "$1__classPrefix__$2")));

	private static final String APPLICTION_MODULE_PREFIX = "com.acme.application";
	private static final String ARCHETYPE_MODULE_PREFIX = "__groupId__.";
	private static final String SRC_MAIN_JAVA = "src/main/java";
	private static final String SRC_TEST_JAVA = "src/test/java";

	private static Path s_workspacePath;
	private static Path s_archetypeWorkPath;

	public static void beforeClass() throws Exception {
		URL location = ArchetypeSyncTest.class.getProtectionDomain().getCodeSource().getLocation();
		// assertTrue(String.format("not a file location (%s)", location),
		// "file".equalsIgnoreCase(location.getProtocol()));

		s_workspacePath = Paths.get(location.toURI()).getParent().getParent().getParent();
		// assertTrue(String.format("Directory (%s) does not exist.", location),
		// s_workspacePath.toFile().exists());

		s_archetypeWorkPath = s_workspacePath.resolve("archetype-crm/src/main/resources/archetype-resources");
		// assertTrue(String.format("Directory (%s) does not exist.", location),
		// s_archetypeWorkPath.toFile().exists());
	}

	// ----------------------------------------------
	// --------------------- impl -------------------
	// ----------------------------------------------

	private void syncModule(String module) throws Exception {
		syncModule(module, Collections.<String>emptyList(), Collections.<String>emptyList());
	}

	private void syncModule(String module, List<String> ignoredPaths, List<String> nonFilteredPaths) throws Exception {
		Path fromDir = s_workspacePath.resolve(APPLICTION_MODULE_PREFIX + module);
		Path toDir = s_archetypeWorkPath.resolve(ARCHETYPE_MODULE_PREFIX + module);

		List<String> allIgnoredPaths = CollectionUtility.arrayList(".classpath", ".flattened-pom.xml", ".project",
				"/target/", ".settings/org.eclipse.jdt.core.prefs", // java 1.7 - 1.8
				".settings/org.sonarlint.eclipse.core.prefs");
		allIgnoredPaths.addAll(ignoredPaths);

		List<String> allNonFilteredPaths = CollectionUtility.arrayList("/.settings/", "META-INF/scout.xml");
		allNonFilteredPaths.addAll(nonFilteredPaths);

		syncDirectory(fromDir, toDir, false, allIgnoredPaths, allNonFilteredPaths,
				CollectionUtility.<ReplaceRule>arrayList(new P_PatternReplaceRule(
						Pattern.compile("^  <artifactId>\\$\\{groupId\\}\\." + module.replace(".", "\\.")),
						"  <artifactId>\\${groupId}.\\${artifactId}")));
	}

	private void syncTestJavaFiles(String module, String... fqcns) {
		syncJavaFilesInteral(module, SRC_TEST_JAVA, fqcns);
	}

	private void syncTestJavaDirectories(String module, String... directories) throws IOException {
		syncJavaDirectoriesInteral(module, module.replace(".test", ""), SRC_TEST_JAVA, directories);
	}

	private void syncJavaFiles(String module, String... fqcns) {
		syncJavaFilesInteral(module, SRC_MAIN_JAVA, fqcns);
	}

	private void syncJavaDirectories(String module, String... directories) throws IOException {
		syncJavaDirectoriesInteral(module, module, SRC_MAIN_JAVA, directories);
	}

	private void syncJavaFilesInteral(String module, String sourceDir, String... fqcns) {
		Path fromJavaSourcePath = s_workspacePath.resolve(APPLICTION_MODULE_PREFIX + module).resolve(sourceDir);
		Path toJavaSourcePath = s_archetypeWorkPath.resolve(ARCHETYPE_MODULE_PREFIX + module).resolve(sourceDir);

		for (String fqcn : fqcns) {
			syncJavaFile(fromJavaSourcePath, toJavaSourcePath, fqcn);
		}
	}

	private void syncJavaDirectoriesInteral(String module, String modulePackage, String sourceDir,
			String... directories) throws IOException {
		Path fromJavaSourcePath = s_workspacePath.resolve(APPLICTION_MODULE_PREFIX + module).resolve(sourceDir);
		Path toJavaSourcePath = s_archetypeWorkPath.resolve(ARCHETYPE_MODULE_PREFIX + module).resolve(sourceDir);
		Path fromDirPath = fromJavaSourcePath.resolve((APPLICTION_MODULE_PREFIX + modulePackage).replace(".", "/"));
		Path toDirPath = toJavaSourcePath.resolve(modulePackage.replace(".", "/"));

		for (String directory : directories) {
			syncDirectory(fromDirPath.resolve(directory), toDirPath.resolve(directory), true);
		}
	}

	private void syncJavaFile(Path fromJavaSourcePath, Path toJavaSourcePath, String fqcn) {
		Path fromPath = fromJavaSourcePath.resolve(fqcn.replace(".", "/") + ".java");
		Path toPath = toJavaSourcePath.resolve(fqcn.replace(APPLICTION_MODULE_PREFIX, "").replace(".", "/") + ".java");

		syncFile(fromPath, toPath, JAVA_REPLACE_RULES);
	}

	private void syncDirectory(final Path fromDir, final Path toDir, boolean allowNotExisting) throws IOException {
		syncDirectory(fromDir, toDir, allowNotExisting, Collections.<String>emptyList(),
				Collections.<String>emptyList(), Collections.<ReplaceRule>emptyList());
	}

	private void syncDirectory(final Path fromDir, final Path toDir, boolean allowNotExisting,
			final List<String> ignoredPaths, final List<String> nonFilteredPaths,
			List<ReplaceRule> additionalReplaceRules) throws IOException {
		// assertTrue(String.format("Direcotry (%s) does not exist.", fromDir),
		// fromDir.toFile().exists());

		List<ReplaceRule> list = new ArrayList<>(JAVA_REPLACE_RULES);
		list.addAll(additionalReplaceRules);
		final List<ReplaceRule> javaReplaceRules = Collections.unmodifiableList(list);
		list = new ArrayList<>(REPLACE_RULES);
		list.addAll(additionalReplaceRules);
		final List<ReplaceRule> otherReplaceRules = Collections.unmodifiableList(list);

		final Set<Path> syncedFiles = new HashSet<>();

		Files.walkFileTree(fromDir, new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				if (pathMatches(dir, ignoredPaths)) {
					return FileVisitResult.SKIP_SUBTREE;
				}
				return super.preVisitDirectory(dir, attrs);
			}

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				Path toPath = toDir.resolve(transformTargetDirectory(fromDir.relativize(file)));

				if (!pathMatches(toPath, ignoredPaths)) {
					if (pathMatches(toPath, nonFilteredPaths)) {
						copyFile(file, toPath);
					} else {
						List<ReplaceRule> replaceRules = file.getFileName().toString().endsWith(".java")
								? javaReplaceRules
								: otherReplaceRules;
						syncFile(file, toPath, replaceRules);
					}
				}
				syncedFiles.add(toPath);
				return super.visitFile(file, attrs);
			}
		});

		if (!allowNotExisting) {
			for (Iterator<Path> iterator = toDir.iterator(); iterator.hasNext();) {
				Path path = iterator.next();
				if (path.toFile().isFile()) {
					// m_collector.checkThat(String.format("File (%s) should not exist.", path),
					// syncedFiles.contains(path), CoreMatchers.is(true));
				}
			}
		}
	}

	private boolean pathMatches(Path path, List<String> patterns) {
		String pathString = path.toUri().toString();
		for (String pattern : patterns) {
			if (pathString.contains(pattern)) {
				return true;
			}
		}
		return false;
	}

	private void copyFile(Path fromPath, Path toPath) {
		try {
			boolean filesEqual = toPath.toFile().exists()
					&& Arrays.equals(Files.readAllBytes(fromPath), Files.readAllBytes(toPath));
			if (!filesEqual) {
				Files.copy(fromPath, toPath, StandardCopyOption.REPLACE_EXISTING);
				// fail("File " + toPath + " is not in sync");
			}
		} catch (Throwable t) {
			// m_collector.addError(t);
		}
	}

	private void syncFile(Path fromPath, Path toPath, List<ReplaceRule> replaceRules) {
		try {
			syncFileInternal(fromPath, transformTargetFileName(toPath), replaceRules);
		} catch (Throwable t) {
			// m_collector.addError(t);
		}
	}

	private Path transformTargetDirectory(Path relativeFromPath) {
		String pathString = relativeFromPath.toString().replace(File.separatorChar, '/');

		for (ReplaceRule replaceRule : DIRECTORY_TRANSFORM_RULES) {
			pathString = replaceRule.apply(pathString);
		}

		return Paths.get(pathString);
	}

	private Path transformTargetFileName(Path toPath) {
		String fileName = toPath.getFileName().toString();

		for (ReplaceRule replaceRule : FILE_NAME_TRANSFORM_RULES) {
			fileName = replaceRule.apply(fileName);
		}

		return toPath.getParent().resolve(fileName);
	}

	private void syncFileInternal(Path fromPath, Path toPath, List<ReplaceRule> replaceRules) throws IOException {
		// assertTrue(String.format("File (%s) does not exist.", fromPath),
		// fromPath.toFile().exists());
		Charset charset = StandardCharsets.UTF_8; // default workspace setting

		List<String> newLines = new ArrayList<>();
		appendPreamble(newLines, replaceRules);
		try (BufferedReader reader = Files.newBufferedReader(fromPath, charset)) {
			String line = reader.readLine();
			while (line != null) {
				newLines.add(applyReplaceRules(line, replaceRules));
				line = reader.readLine();
			}
		}

		List<String> currentLines;
		if (toPath.toFile().exists()) {
			currentLines = Files.readAllLines(toPath, charset);
		} else {
			currentLines = new ArrayList<>();
		}

		toPath.getParent().toFile().mkdirs();
		// write lines (not using Files.write as it uses wrong line separator)
		OutputStream out = Files.newOutputStream(toPath, StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING);
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, charset))) {
			for (CharSequence line : newLines) {
				writer.append(line);
				writer.append("\n");
			}
		}

		// not using assertEquals as it prints all lines
		// assertTrue("File " + toPath + " is not in sync",
		// newLines.equals(currentLines));
	}

	private String applyReplaceRules(String line, List<ReplaceRule> replaceRules) {
		for (ReplaceRule replaceRule : replaceRules) {
			line = replaceRule.apply(line);
		}
		return line;
	}

	private void appendPreamble(List<String> collector, List<ReplaceRule> replaceRules) {
		for (ReplaceRule replaceRule : replaceRules) {
			replaceRule.appendPreamble(collector);
		}
	}

	interface ReplaceRule {

		void appendPreamble(List<String> collector);

		String apply(String input);
	}

	private static class P_SymbolReplaceRule implements ReplaceRule {
		private final String m_symbol;
		private final String m_replacement;
		private final String m_replacementInternal;

		public P_SymbolReplaceRule(String symbol, String replacement) {
			m_symbol = symbol;
			m_replacement = replacement;
			m_replacementInternal = "${" + replacement + "}";
		}

		@Override
		public void appendPreamble(List<String> collector) {
			collector.add("#set( $" + m_replacement + " = '" + m_symbol + "' )");
		}

		@Override
		public String apply(String input) {
			return input.replace(m_symbol, m_replacementInternal);
		}
	}

	private static class P_PatternReplaceRule implements ReplaceRule {
		private final Pattern m_pattern;
		private final String m_replacement;

		public P_PatternReplaceRule(Pattern pattern, String replacement) {
			m_pattern = pattern;
			m_replacement = replacement;
		}

		@Override
		public void appendPreamble(List<String> collector) {
			// nop
		}

		@Override
		public String apply(String input) {
			return m_pattern.matcher(input).replaceAll(m_replacement);
		}
	}
}