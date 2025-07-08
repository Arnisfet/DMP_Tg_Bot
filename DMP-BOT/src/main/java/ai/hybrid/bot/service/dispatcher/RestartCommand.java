package ai.hybrid.bot.service.dispatcher;

import ai.hybrid.bot.config.SshConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RestartCommand  implements CommandInterface {
    @Autowired
    StartCommand startCommand;
    @Autowired
    StopCommand stopCommand;

    /**
     * @return
     */
    @Override
    public String getCommand() {
        return "Restart";
    }

    /**
     * @param config
     * @param job
     */
    @Override
    public void launch(SshConfig.ClusterConfig config, String job) {
        stopCommand.launch(config, job);
        startCommand.launch(config, job);
    }
}
