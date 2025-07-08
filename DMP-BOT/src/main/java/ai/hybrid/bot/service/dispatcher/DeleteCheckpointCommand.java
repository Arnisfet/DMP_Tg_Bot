package ai.hybrid.bot.service.dispatcher;

import ai.hybrid.bot.config.AppButtonsConfig;
import ai.hybrid.bot.config.SshConfig;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class DeleteCheckpointCommand implements CommandInterface {
    private String BASE_PATH = "/tmp/checkpoint/";
    @Autowired
    AppButtonsConfig appButtonsConfig;
    /**
     * @return
     */
    @Override
    public String getCommand() {
        return "Delete checkpoint";
    }

    /**
     * @param config
     * @param job
     */
    @Override
    public void launch(SshConfig.ClusterConfig config, String job) {
        String directory = appButtonsConfig.getCheckpoints().get(job);
        if (directory.isEmpty())
            throw new RuntimeException("Value are not exist");
        try {
            JSch jSch = new JSch();
            if (config.getPassphrase() != null && !config.getPassphrase().isBlank())
                jSch.addIdentity(config.getPrivateKey(), config.getPassphrase());
            else
                jSch.addIdentity(config.getPrivateKey());
            Session session = jSch.getSession(config.getUser(), config.getHost(), 45292);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
            String command = "hdfs dfs -rm -r -skipTrash" + BASE_PATH + directory;
            channelExec.setCommand(command);
            channelExec.connect();

            Thread.sleep(2000);
            channelExec.disconnect();
            session.disconnect();
            log.info("Checkpoint was deleted by path {}", command);
        } catch(JSchException e){
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
