package org.terrakube.executor.service.scripts;

import com.diogonunes.jcolor.AnsiFormat;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.terrakube.executor.service.mode.Command;
import org.terrakube.executor.service.mode.TerraformJob;
import org.terrakube.executor.service.scripts.bash.BashEngine;
import org.terrakube.executor.service.scripts.groovy.GroovyEngine;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static com.diogonunes.jcolor.Ansi.colorize;
import static com.diogonunes.jcolor.Attribute.*;

@Slf4j
@Service
public class ScriptEngineService {

    public static final String TOOLS_REPOSITORY = "/.terrakube/toolsRepository";
    public static final String TOOLS = "/.terrakube/tools";

    private GroovyEngine groovyEngine;
    private BashEngine bashEngine;
    private String toolsRepository;
    private String toolsBranch;

    @Autowired
    public ScriptEngineService(GroovyEngine groovyEngine, BashEngine bashEngine, @Value("${org.terrakube.tools.repository}") String toolsRepository, @Value("${org.terrakube.tools.branch}") String toolsBranch) {
        this.groovyEngine = groovyEngine;
        this.bashEngine = bashEngine;
        this.toolsRepository = toolsRepository;
        this.toolsBranch = toolsBranch;
    }

    public boolean execute(TerraformJob terraformJob, List<Command> commands, File workingDirectory, Consumer<String> output) {
        AtomicBoolean executeSuccess = new AtomicBoolean(true);
        TreeMap<Integer, Command> commandOrder = new TreeMap<>();
        if (commands != null) {
            commands.forEach(command -> {
                commandOrder.put(command.getPriority(), command);
            });

            try {
                createToolsDirectory(workingDirectory);

                commandOrder.forEach((priority, command) -> {
                    if (executeSuccess.get()) {
                        printBannerInit(command, output);
                        switch (command.getRuntime()) {
                            case GROOVY:
                                executeSuccess.set(groovyEngine.execute(terraformJob, command.getScript(), workingDirectory, output));
                                break;
                            case BASH:
                                executeSuccess.set(bashEngine.execute(terraformJob, command.getScript(), workingDirectory, output));
                                break;
                            default:
                                break;
                        }
                        printBannerEnd(output);
                    }
                });

                cleanToolsDirectory(workingDirectory);
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        return executeSuccess.get();

    }

    private void printBannerInit(Command command, Consumer<String> output) {
        AnsiFormat colorMessage = new AnsiFormat(GREEN_TEXT(), BLACK_BACK(), BOLD());
        AnsiFormat colorScript = new AnsiFormat(GREEN_TEXT(), BLACK_BACK(), BOLD());
        output.accept(colorize("***************************************", colorMessage));
        output.accept(colorize("Command Priority " + command.getPriority(), colorMessage));
        output.accept(colorize("Running " + command.getRuntime(), colorMessage));
        output.accept(colorize("Script Information:  ", colorMessage));
        output.accept(colorize(command.getScript(), colorScript));
        output.accept(colorize("\n\n\n***************************************", colorMessage));
        output.accept(colorize("Begin Execution: ", colorMessage));
    }

    private void printBannerEnd(Consumer<String> output) {
        AnsiFormat colorMessage = new AnsiFormat(GREEN_TEXT(), BLACK_BACK(), BOLD());
        output.accept(colorize("End Execution... ", colorMessage));
        output.accept(colorize("\n\n", colorMessage));
    }

    private void createToolsDirectory(File workingDirectory) throws IOException {
        FileUtils.forceMkdir(getToolsRepository(workingDirectory));

        try {
            Git.cloneRepository()
                    .setURI(toolsRepository)
                    .setDirectory(getToolsRepository(workingDirectory))
                    .setBranch(this.toolsBranch)
                    .call();
        } catch (GitAPIException e) {
            log.error(e.getMessage());
        }
    }

    @NotNull
    private File getToolsRepository(File workingDirectory) {
        return new File(workingDirectory.getAbsolutePath() + TOOLS_REPOSITORY);
    }

    private void cleanToolsDirectory(File workingDirectory) throws IOException {
        FileUtils.deleteDirectory(getToolsRepository(workingDirectory));
    }
}