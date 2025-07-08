package ai.hybrid.bot.service.dispatcher;

import ai.hybrid.bot.config.SshConfig;

public interface CommandInterface {
    public String getCommand();
    public void launch(SshConfig.ClusterConfig config, String job);
}
