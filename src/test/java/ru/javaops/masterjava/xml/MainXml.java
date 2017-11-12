package ru.javaops.masterjava.xml;

import com.google.common.io.Resources;
import j2html.tags.ContainerTag;
import one.util.streamex.StreamEx;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.schema.Payload;
import ru.javaops.masterjava.xml.schema.Project;
import ru.javaops.masterjava.xml.schema.User;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.Schemas;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static j2html.TagCreator.*;

public class MainXml {
    public static final Comparator<User> USER_COMPARATOR = Comparator.comparing(User::getValue).thenComparing(User::getEmail);

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Format: projectName, xmlName");
            System.exit(1);
        }
        URL payloadUrl = Resources.getResource(args[1]);
        MainXml main = new MainXml();
        String projectName = args[0];

        Set<User> users = main.parceByJaxb(projectName, payloadUrl);
        String out = toHtml(users, projectName, Paths.get("out/usersJaxb.html"));
        System.out.println(out);
    }

    private Set<User> parceByJaxb(String projectName, URL payloadUrl) throws Exception {
        JaxbParser parser = new JaxbParser(ObjectFactory.class);
        parser.setSchema(Schemas.ofClasspath("payload.xsd"));
        try (InputStream is = payloadUrl.openStream()) {
            Payload payload = parser.unmarshal(is);
            Project project = StreamEx.of(payload.getProjects().getProject())
                    .filter(p -> p.getName().equals(projectName))
                    .findAny()
                    .orElseThrow(() -> new IllegalArgumentException("Invalid project name '" + projectName + '\''));
            Set<Project.Group> groups = new HashSet<>(project.getGroup());
            return StreamEx.of(payload.getUsers().getUser())
                    .filter(u -> StreamEx.of(u.getGroupRefs())
                        .findAny(groups::contains)
                        .isPresent())
                    .collect(Collectors.toCollection(() -> new TreeSet<>(USER_COMPARATOR)));
        }
    }

    private static String toHtml(Set<User> users, String projectName, Path path) throws IOException {
        try (Writer writer = Files.newBufferedWriter(path)) {
            final ContainerTag table = table().with(
                    tr().with(th("FullName"), th("email")))
                    .attr("border", "1")
                    .attr("cellpadding", "8")
                    .attr("cellspacing", "0");

            users.forEach(u -> table.with(tr().with(td(u.getValue()), td(u.getEmail()))));

            String out = html().with(
                    head().with(title(projectName + " users")),
                    body().with(h1(projectName + " users"), table)
            ).render();
            writer.write(out);
            return out;
        }

    }
}
