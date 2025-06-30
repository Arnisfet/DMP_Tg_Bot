package ai.hybrid.bot.service.handler;

import ai.hybrid.bot.config.SshConfig;
import ai.hybrid.bot.data.UserContext;
import com.jcraft.jsch.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@Component
@RequiredArgsConstructor
public class CommandDispatcher {
    private final SshConfig sshConfig;
    public void launch(UserContext context) {
        String action = context.getAction();
        String cluster = context.getCluster();

        SshConfig.ClusterConfig config = sshConfig.getClusters().get(cluster);
        if (config == null) {
            System.out.println("Error");
            return;
        }
        runRemoteScript(config, action);
    }
    private void runRemoteScript(SshConfig.ClusterConfig config, String action) {
        String scriptPath = config.getScripts().get(action);
        if (scriptPath == null) {
            System.err.println("No script for action: " + action);
            return;
        }
        try {
            JSch jSch = new JSch();
            if (config.getPassphrase() != null && !config.getPassphrase().isBlank())
                jSch.addIdentity(config.getPrivateKey(), config.getPassphrase());
            else
                jSch.addIdentity(config.getPrivateKey());

            Session session = jSch.getSession(config.getUser(), config.getHost(), 45292);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand("nohup bash " + scriptPath + " > " + config.getLogfile());
            channel.connect();

            System.out.println("Script launched on " + config.getHost() + ": " + scriptPath);

            channel.disconnect();
            session.disconnect();
        } catch (JSchException e) {
            e.printStackTrace();
        }
    }
}
