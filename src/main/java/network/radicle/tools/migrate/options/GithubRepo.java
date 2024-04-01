package network.radicle.tools.migrate.options;

import picocli.CommandLine;

public class GithubRepo {
    @CommandLine.Option(
            names = {"-gr", "--github-repo"},
            order = 30,
            required = true,
            defaultValue = "${GH_REPO}",
            description = "The source GitHub repo.")
    public String gRepo;

    @CommandLine.Option(
            names = {"-go", "--github-repo-owner"},
            order = 40,
            required = true,
            defaultValue = "${GH_OWNER}",
            description = "The owner of the source GitHub repo.")
    public String gOwner;

    @CommandLine.Option(
            names = {"-gt", "--github-token"},
            order = 50,
            required = true,
            interactive = true,
            defaultValue = "${GH_TOKEN}",
            description = "Your GitHub personal access token (with `repo` scope or `read-only access` granted).")
    public String gToken;
}
