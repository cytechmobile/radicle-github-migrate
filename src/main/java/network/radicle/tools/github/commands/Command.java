package network.radicle.tools.github.commands;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import network.radicle.tools.github.Config;
import network.radicle.tools.github.Config.Filters;
import network.radicle.tools.github.Config.GitHubConfig;
import network.radicle.tools.github.Config.RadicleConfig;
import network.radicle.tools.github.services.MigrationService;
import network.radicle.tools.github.utils.InstantConverter;
import picocli.CommandLine;

import java.net.URL;
import java.time.Instant;

@Dependent
@CommandLine.Command(sortOptions = false, sortSynopsis = false)
public class Command implements Runnable {
    public static final int PAGE_SIZE = 100;

    @CommandLine.Option(
            names = {"-gv", "--github-api-version"},
            defaultValue = "${GITHUB_API_VERSION:-2022-11-28}",
            description = "The version of the GitHub REST API (default: 2022-11-28).")
    String gVersion;

    @CommandLine.Option(
            names = {"-gu", "--github-api-url"},
            defaultValue = "${GITHUB_API_URL:-https://api.github.com}",
            description = "The base url of the GitHub REST API (default: https://api.github.com).")
    URL gUrl;

    @CommandLine.Option(
            names = {"-gr", "--github-repo"},
            required = true,
            defaultValue = "${GITHUB_REPO}",
            description = "The source GitHub repo.")
    String gRepo;

    @CommandLine.Option(
            names = {"-go", "--github-repo-owner"},
            required = true,
            defaultValue = "${GITHUB_OWNER}",
            description = "The owner of the source GitHub repo.")
    String gOwner;

    @CommandLine.Option(
            names = {"-gt", "--github-token"},
            required = true,
            interactive = true,
            defaultValue = "${GITHUB_TOKEN}",
            description = "Your GitHub personal access token.")
    String gToken;

    @CommandLine.Option(
            names = {"-rv", "--radicle-api-version"},
            defaultValue = "${RADICLE_API_VERSION:-v1}",
            description = "The version of the Radicle HTTP API (default: v1).")
    String rVersion;

    @CommandLine.Option(
            names = {"-ru", "--radicle-api-url"},
            defaultValue = "${RADICLE_API_URL:-http://localhost:8080/api}",
            description = "The base url of Radicle HTTP API (default: http://localhost:8080/api).")
    URL rUrl;

    @CommandLine.Option(
            names = {"-rp", "--radicle-project"},
            required = true,
            defaultValue = "${RADICLE_PROJECT}",
            description = "The target Radicle project.")
    String rProject;

    @CommandLine.Option(
            converter = InstantConverter.class,
            names = {"-fs", "--filter-since"},
            defaultValue = "${FILTER_SINCE}",
            description = "Migrate issues created after the given time (default: timestamp of the last run, " +
                    "example: 2023-01-01T10:15:30+01:00).")
    Instant fSince;

    @CommandLine.Option(
            names = {"-fl", "--filter-labels"},
            defaultValue = "${FILTER_LABELS}",
            description = "Migrate issues with the given labels given in a csv format (example: bug,ui,@high).")
    String fLabels;

    @CommandLine.Option(
            names = {"-ft", "--filter-state"},
            defaultValue = "${FILTER_STATE:-all}",
            description = "Migrate issues in this state (default: all, can be one of: ${COMPLETION-CANDIDATES}).")
    State fState;

    @CommandLine.Option(
            names = {"-fm", "--filter-milestone"},
            defaultValue = "${FILTER_MILESTONE}",
            description = "Migrate issues belonging to the given milestone number (example: 3).")
    Integer fMilestone;

    @CommandLine.Option(
            names = {"-fa", "--filter-assignee"},
            defaultValue = "${FILTER_ASSIGNEE}",
            description = "Migrate issues assigned to the given user name. ")
    String fAssignee;

    @CommandLine.Option(
            names = {"-fc", "--filter-creator"},
            defaultValue = "${FILTER_CREATOR}",
            description = "Migrate issues created by the given user name.")
    String fCreator;

    @CommandLine.Option(
            names = {"-dr", "--dry-run"},
            defaultValue = "${DRY_RUN:false}",
            description = "Run the whole migration process without actually creating the issues in the target Radicle project..")
    boolean dryRun;

    public enum State { open, closed, all }

    @Inject MigrationService service;
    @Inject Config config;

    public void run() {
        loadConfiguration();
    }

    private void loadConfiguration() {
        var filters = new Filters(fSince, fLabels, fState, fMilestone, fAssignee, fCreator);
        config.setGithub(new GitHubConfig(gToken, gUrl, gVersion, gOwner, gRepo, filters, PAGE_SIZE));
        config.setRadicle(new RadicleConfig(rUrl, rVersion, rProject, dryRun));
    }

}
