package jetbrains.buildServer.deployer.agent.smb;

import jetbrains.buildServer.ExtensionHolder;
import jetbrains.buildServer.deployer.agent.base.BaseDeployerRunner;

public abstract class ExtractedClass extends BaseDeployerRunner {
    public ExtractedClass(ExtensionHolder extensionHolder) {
        super(extensionHolder);
    }
}
