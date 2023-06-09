package network.radicle.tools.github.core.github;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class IssueTest {
    public static final ObjectMapper MAPPER = new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .registerModule(new JavaTimeModule());

    @Test
    public void testSerializationOfSingleIssue() throws Exception {
        var issue = generateGitHubIssue();
        var json = MAPPER.writeValueAsString(issue);
        var i = MAPPER.readValue(json, Issue.class);

        assertThat(i).isNotNull().usingRecursiveComparison().isEqualTo(issue);
    }

    @Test
    public void testSerializationOfManyIssues() {
        List<Issue> issues = loadGitHubIssues();
        assertThat(issues.size()).isNotZero();
        Issue issue = issues.get(0);
        assertThat(issue.title).isNotNull().isNotEmpty();
        assertThat(issue.createdAt).isNotNull();
    }

    public static Issue generateGitHubIssue() {
        try {
            var seed = System.currentTimeMillis();
            var file = new File("src/test/resources/github/issue.json");
            Issue issue = MAPPER.readValue(file, Issue.class);
            issue.id = issue.id + seed;
            issue.createdAt = Instant.now().minus(1, ChronoUnit.HOURS);
            issue.updatedAt = Instant.now();
            issue.user.id = issue.user.id + seed;
            return issue;
        } catch (Exception ex) {
            return null;
        }
    }

    public static List<Issue> loadGitHubIssues() {
        try {
            var file = new File("src/test/resources/github/issues.json");
            return MAPPER.readValue(file, new TypeReference<>() { });
        } catch (Exception ex) {
            return List.of();
        }
    }
}
