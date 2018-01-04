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
import java.nio.file.FileVisitor;
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
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.scout.rt.platform.util.CollectionUtility;

public class ArchetypeSync {

    public static void main(String[] args) throws Exception {
        ArchetypeSync t = new ArchetypeSync();
        t.init();
        t.syncModule("client");
        t.syncModule("database");
        t.syncModule("docx4j");
        t.syncModule("server");
        t.syncModule("shared");
        t.syncModule("ui.html");
        t.syncModule("ui.html.app.dev");
        t.syncModule("ui.html.app.war");
        t.syncModule("all.app.dev");
        t.syncModule("server.app.war");
        t.syncModule("server.app.dev");

    }

    private final static List<ReplaceRule> JAVA_REPLACE_RULES = Collections
            .unmodifiableList(Arrays.asList(new P_SymbolReplaceRule("$", "symbol_dollar"),
                    new P_SymbolReplaceRule("#", "symbol_pound"), new P_SymbolReplaceRule("\\", "symbol_escape"),
                    new P_PatternReplaceRule(Pattern.compile("com\\.acme\\.application"), "\\${package}")));

    private final static List<ReplaceRule> REPLACE_RULES = Collections
            .unmodifiableList(Arrays.asList(new P_SymbolReplaceRule("$", "symbol_dollar"),
                    new P_SymbolReplaceRule("#", "symbol_pound"), new P_SymbolReplaceRule("\\", "symbol_escape"),
                    new P_PatternReplaceRule(Pattern.compile("com\\.acme\\.application"), "\\${package}"),
                    new P_PatternReplaceRule(Pattern.compile("com\\.acme"), "\\${groupId}"),
                    new P_PatternReplaceRule(Pattern.compile("(?<!scout\\.)(?<!ui\\.theme=)application"),
                            "\\${rootArtifactId}"),
                    new P_PatternReplaceRule(Pattern.compile("(?<!\\$\\{)appName"), "\\${appName}"),
                    new P_PatternReplaceRule(Pattern.compile("Scout Template Application"), "\\${appName}"),
                    new P_PatternReplaceRule(Pattern.compile("1.0.0-SNAPSHOT"), "\\${version}")));

    private final static List<ReplaceRule> DIRECTORY_TRANSFORM_RULES = Collections.unmodifiableList(Arrays
            .<ReplaceRule>asList(new P_PatternReplaceRule(Pattern.compile("/java/com/acme/application/"), "/java/"),
                    new P_PatternReplaceRule(Pattern.compile("/resources/com/acme/application/"), "/resources/")

    ));

    private final static List<ReplaceRule> FILE_NAME_TRANSFORM_RULES = Collections
            .unmodifiableList(Arrays.<ReplaceRule>asList(new P_PatternReplaceRule(
                    Pattern.compile("application-(all-macro\\.((js)|(less)))"), "__parentArtifactId__-$1")));

    private static final String APPLICTION_MODULE_PREFIX = "application.";
    private static final String ARCHETYPE_MODULE_PREFIX = "__rootArtifactId__.";

    private Path workspacePath;
    private Path archetypeWorkPath;

    public void init() throws Exception {
        URL location = ArchetypeSync.class.getProtectionDomain().getCodeSource().getLocation();
        workspacePath = Paths.get(location.toURI()).resolve("../../../../application").normalize();
        archetypeWorkPath = workspacePath.resolve("../archetype/src/main/resources/archetype-resources");
    }

    // ----------------------------------------------
    // --------------------- impl -------------------
    // ----------------------------------------------

    private void syncModule(String module) throws Exception {
        syncModule(module, Collections.<String>emptyList(), Collections.<String>emptyList());
    }

    private void syncModule(String module, List<String> ignoredPaths, List<String> nonFilteredPaths) throws Exception {
        Path fromDir = workspacePath.resolve(APPLICTION_MODULE_PREFIX + module);
        Path toDir = archetypeWorkPath.resolve(ARCHETYPE_MODULE_PREFIX + module);
        Files.walkFileTree(toDir, new P_DeletingVisitor());
        List<String> allIgnoredPaths = CollectionUtility.arrayList(".classpath", ".flattened-pom.xml", ".project",
                "/target/", ".settings/org.eclipse.jdt.core.prefs", // java 1.7 - 1.8
                ".settings/org.sonarlint.eclipse.core.prefs");
        allIgnoredPaths.addAll(ignoredPaths);

        List<String> allNonFilteredPaths = CollectionUtility.arrayList("/.settings/", "META-INF/scout.xml", ".gif",
                ".png", ".ttf", ".woff", ".ico", ".json", "colors-application.less");
        allNonFilteredPaths.addAll(nonFilteredPaths);

        syncDirectory(fromDir, toDir, allIgnoredPaths, allNonFilteredPaths,
                CollectionUtility.<ReplaceRule>arrayList(new P_PatternReplaceRule(
                        Pattern.compile("^  <artifactId>\\$\\{groupId\\}\\." + module.replace(".", "\\.")),
                        "  <artifactId>\\${groupId}.\\${artifactId}")));
    }

    private void syncDirectory(final Path fromDir, final Path toDir,
            final List<String> ignoredPaths, final List<String> nonFilteredPaths,
            List<ReplaceRule> additionalReplaceRules) throws IOException {

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
                Files.createDirectories(toPath.getParent());
                Files.copy(fromPath, toPath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Throwable t) {
            System.out.println(t);
            System.out.println(fromPath);
            t.printStackTrace();
        }
    }

    private void syncFile(Path fromPath, Path toPath, List<ReplaceRule> replaceRules) {
        try {
            syncFileInternal(fromPath, transformTargetFileName(toPath), replaceRules);
        } catch (Throwable t) {
            System.out.println(t);
            System.out.println(fromPath);
            t.printStackTrace();
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
        Charset charset = StandardCharsets.UTF_8; // default workspace setting

        List<String> newLines = new ArrayList<>();

        boolean preamblesNeeded = false;

        try (BufferedReader reader = Files.newBufferedReader(fromPath, charset)) {
            String line = reader.readLine();
            while (line != null) {
                String newLine = applyReplaceRules(line, replaceRules);
                newLines.add(newLine);
                preamblesNeeded = preamblesNeeded || !newLine.equals(line);
                line = reader.readLine();
            }
        }
        if (preamblesNeeded) {
            List<String> preambles = new ArrayList<>();
            appendPreamble(preambles, replaceRules);
            preambles.addAll(newLines);
            newLines = preambles;
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

    private class P_DeletingVisitor extends SimpleFileVisitor<Path> {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            Files.delete(file);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            Files.delete(dir);
            return FileVisitResult.CONTINUE;
        }
    }
}