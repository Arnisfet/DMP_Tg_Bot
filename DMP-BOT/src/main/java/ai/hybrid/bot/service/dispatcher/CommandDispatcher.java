package ai.hybrid.bot.service.dispatcher;

import ai.hybrid.bot.config.SshConfig;
import ai.hybrid.bot.data.UserContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CommandDispatcher {
    private Map<String, CommandInterface> commandMap;
    private final SshConfig sshConfig;
    CommandDispatcher(List<CommandInterface> commandList, SshConfig sshConfig) {
        commandMap = commandList.stream()
                .collect(Collectors.toMap(CommandInterface::getCommand, element-> element));
        this.sshConfig = sshConfig;
    }
    public void commandPull(UserContext context) {
        String action = context.getAction();
        String cluster = context.getCluster();
        String job = context.getJob();

        SshConfig.ClusterConfig config = sshConfig.getClusters().get(cluster);
        CommandInterface command = commandMap.get(action);
        if (config == null || command == null) {
            System.out.println("Error");
            return;
        }
        command.launch(config, action, job);
    }
}
