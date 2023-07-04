package network.radicle.tools.github.commands;

import io.quarkus.runtime.Quarkus;
import jakarta.enterprise.context.Dependent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

@Dependent
@CommandLine.Command(
        name = "issues",
        description = "Migrate issues from a GitHub repository to a Radicle project.",
        headerHeading = "@|bold,underline Usage|@:%n",
        synopsisHeading = "%n",
        descriptionHeading = "%n@|bold,underline Description|@:%n%n",
        parameterListHeading = "%n@|bold,underline Parameters|@:%n",
        optionListHeading = "%n@|bold,underline Options|@:%n")
public class IssuesCommand extends Command {
    private static final Logger logger = LoggerFactory.getLogger(IssuesCommand.class);

    @Override
    public void run() {
        super.run();

        var result = service.migrateIssues();
        var exitCode = 0;
        if (!result) {
            exitCode = 1;
            logger.error("Migration failed.");
        } else {
            logger.info("Migration finished successfully!");
        }
        Quarkus.asyncExit(exitCode);
    }

}
