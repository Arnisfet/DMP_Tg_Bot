package ai.hybrid.bot.service.dispatcher;

import ai.hybrid.bot.config.SshConfig;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.springframework.stereotype.Component;

@Component
public class StartCommand implements CommandInterface{
    @Override
    public String getCommand() {
        return "Start";
    }

    @Override
    public void launch(SshConfig.ClusterConfig config, String job) {
        String scriptPath = config.getScripts().get(getCommand());
        if (scriptPath == null) {
            System.err.println("No script for action: " + getCommand());
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
            channel.setCommand("cd " + scriptPath + " && nohup bash "
                    + scriptPath + job + ".sh" + " >> " + config.getLogfile() + " 2>&1 &");
            channel.connect();
//            Thread.sleep(15000);

            System.out.println("Script launched on " + config.getHost() + ": " + scriptPath);

            channel.disconnect();
            session.disconnect();
        } catch (JSchException e) {
            e.printStackTrace();
        }
    }
}
