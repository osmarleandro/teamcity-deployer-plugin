/*
 * Copyright 2000-2020 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jetbrains.buildServer.deployer.agent.ssh;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import jetbrains.buildServer.deployer.common.DeployerRunnerConstants;
import jetbrains.buildServer.deployer.common.SSHRunnerConstants;
import jetbrains.buildServer.util.FileUtil;
import org.testng.annotations.Test;

import java.io.File;

import static org.testng.Assert.assertTrue;

/**
 * Created by Nikita.Skvortsov
 * date: 29.07.13.
 */
@Test
public class SSHSessionProviderTest extends BaseSSHTest {

  public void testUsernamePassword() throws Exception {
    assertSessionIsConnected();
  }

  public void testCustomKey() throws Exception {
    myRunnerParams.put(SSHRunnerConstants.PARAM_AUTH_METHOD, SSHRunnerConstants.AUTH_METHOD_CUSTOM_KEY);
    myRunnerParams.put(SSHRunnerConstants.PARAM_KEYFILE, FileUtil.getRelativePath(myWorkingDir, myPrivateKey));
    myRunnerParams.put(DeployerRunnerConstants.PARAM_PASSWORD, "passphrase");
    assertSessionIsConnected();
  }

  public void testCustomKeyAbsPath() throws Exception {
    myRunnerParams.put(SSHRunnerConstants.PARAM_AUTH_METHOD, SSHRunnerConstants.AUTH_METHOD_CUSTOM_KEY);
    myRunnerParams.put(SSHRunnerConstants.PARAM_KEYFILE, myPrivateKey.getAbsolutePath());
    myRunnerParams.put(DeployerRunnerConstants.PARAM_PASSWORD, "passphrase");
    assertSessionIsConnected();
  }

  public void testDefaultConfig() throws Exception {
    myRunnerParams.put(SSHRunnerConstants.PARAM_AUTH_METHOD, SSHRunnerConstants.AUTH_METHOD_DEFAULT_KEY);
    final File tempConfig = createTempFile("Host *\n" +
        "    Port " + testPort + "\n" +
        "    IdentityFile " + myPassphraselessKey.getCanonicalPath());
    myInternalProperties.put(SSHProcessAdapterOptions.TEAMCITY_DEPLOYER_SSH_CONFIG_PATH, tempConfig.getCanonicalPath());
    assertSessionIsConnected();
  }

  public void testDefaultConfigMatching() throws Exception {
    myRunnerParams.put(SSHRunnerConstants.PARAM_AUTH_METHOD, SSHRunnerConstants.AUTH_METHOD_DEFAULT_KEY);
    final File tempConfig = createTempFile(
        "Host foo\n" +
            "    Hostname 127.0.0.1\n" +
            "    Port " + testPort + "\n" +
            "    IdentityFile " + myPassphraselessKey.getCanonicalPath() + "\n" +
            "Host *\n" +
            "    Port " + 22 + "\n" +
            "    IdentityFile " + myPrivateKey.getCanonicalPath()
    );
    myRunnerParams.put(DeployerRunnerConstants.PARAM_TARGET_URL, "foo");
    myInternalProperties.put(SSHProcessAdapterOptions.TEAMCITY_DEPLOYER_SSH_CONFIG_PATH, tempConfig.getCanonicalPath());
    assertSessionIsConnected();
  }

  public void testDefaultConfigMissing() throws Exception {
    myRunnerParams.put(SSHRunnerConstants.PARAM_AUTH_METHOD, SSHRunnerConstants.AUTH_METHOD_DEFAULT_KEY);

    myInternalProperties.put(SSHProcessAdapterOptions.TEAMCITY_DEPLOYER_SSH_CONFIG_PATH, "some/not/existing/path");
    myInternalProperties.put(SSHSessionProvider.TEAMCITY_DEPLOYER_SSH_DEFAULT_KEY, myPassphraselessKey.getCanonicalPath());
    assertSessionIsConnected();
  }

  public void testUploadedKeys() throws Exception {
    myRunnerParams.put(SSHRunnerConstants.PARAM_AUTH_METHOD, SSHRunnerConstants.AUTH_METHOD_UPLOADED_KEY);

    myRunnerParams.put("teamcitySshKey", "key_id_value");
    myRunnerParams.put(DeployerRunnerConstants.PARAM_USERNAME, "username");
    myRunnerParams.put(DeployerRunnerConstants.PARAM_PASSWORD, "passphrase");

    assertSessionIsConnected();
  }

  private void assertSessionIsConnected() throws JSchException {
    Session session = null;
    try {
      final SSHSessionProvider provider = new SSHSessionProvider(myContext, myInternalPropertiesHolder, mySshKeyManager);
      session = provider.getSession();
      assertTrue(session.isConnected());
    } finally {
      if (session != null) {
        session.disconnect();
      }
    }
  }
}
