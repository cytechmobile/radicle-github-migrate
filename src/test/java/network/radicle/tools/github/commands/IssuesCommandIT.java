package network.radicle.tools.github.commands;

import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainLauncher;
import io.quarkus.test.junit.main.QuarkusMainTest;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Singleton;
import network.radicle.tools.github.clients.IGitHubClient;
import network.radicle.tools.github.clients.IRadicleClient;
import network.radicle.tools.github.core.github.Comment;
import network.radicle.tools.github.core.github.CommentTest;
import network.radicle.tools.github.core.github.Issue;
import network.radicle.tools.github.core.github.IssueTest;
import network.radicle.tools.github.core.radicle.Session;
import network.radicle.tools.github.core.radicle.actions.Action;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusMainTest
@TestProfile(IssuesCommandIT.GithubTestProfile.class)
class IssuesCommandIT {
    @Test
    public void testLaunch(QuarkusMainLauncher launcher) {
        LaunchResult result = launcher.launch("issues", "-go=testOwner", "-gr=testRepo", "-rp=testProject");

        assertThat(result.exitCode()).isEqualTo(0);
        assertThat(result.getErrorOutput()).isEmpty();
        assertThat(result.getOutput()).contains("Migration finished successfully");
    }

    public static class GithubTestProfile implements QuarkusTestProfile {
        @Override
        public Set<Class<?>> getEnabledAlternatives() {
            return Set.of(MockedGitHubClient.class, MockedRadicleClient.class);
        }
    }

    @Alternative
    @Singleton
    public static class MockedGitHubClient implements IGitHubClient {
        private static final Logger logger = LoggerFactory.getLogger(MockedGitHubClient.class);

        @Override
        public List<Issue> getIssues(int page) throws Exception {
            var issues = IssueTest.loadGitHubIssues();
            logger.info("Returning {} issues for page {}", issues.size(), page);
            return issues;
        }

        @Override
        public List<Comment> getComments(long issueId, int page) throws Exception {
            var comments = CommentTest.loadGitHubComments();
            logger.info("Returning {} comments for page {}", comments.size(), page);
            return comments;
        }
    }

    @Alternative
    @Singleton
    public static class MockedRadicleClient implements IRadicleClient {
        private static final Logger logger = LoggerFactory.getLogger(MockedRadicleClient.class);

        @Override
        public Session createSession() throws Exception {
            var session = new Session();
            session.id = UUID.randomUUID().toString();
            logger.info("Returning session id: {}", session.id);
            return session;
        }

        @Override
        public String createIssue(Session session, network.radicle.tools.github.core.radicle.Issue issue) throws Exception {
            var id = UUID.randomUUID().toString();
            logger.info("Returning issue id: {}", id);
            return id;
        }

        @Override
        public boolean updateIssue(Session session, String id, Action action) throws Exception {
            logger.info("Returning issue updated");
            return true;
        }
    }

}
