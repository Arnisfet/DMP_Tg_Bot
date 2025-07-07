package ai.hybrid.bot.service.dispatcher;

import ai.hybrid.bot.config.SshConfig;

public class DeleteCheckpointCommand implements CommandInterface{
    /**
     * @return
     */
    @Override
    public String getCommand() {
        return "Delete checkpoint";
    }

    /**
     * @param config
     * @param action
     * @param job
     */
    @Override
    public void launch(SshConfig.ClusterConfig config, String action, String job) {

    }
}
