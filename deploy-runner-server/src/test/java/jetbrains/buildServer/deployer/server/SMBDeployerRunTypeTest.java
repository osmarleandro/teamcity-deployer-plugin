package jetbrains.buildServer.deployer.server;

import jetbrains.buildServer.deployer.common.DeployerRunnerConstants;
import jetbrains.buildServer.serverSide.InvalidProperty;
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.serverSide.RunTypeRegistry;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;

/**
 * Created by Nikita.Skvortsov
 * Date: 7/25/13, 4:55 PM
 */
public class SMBDeployerRunTypeTest extends  DeployerRunTypeTest {
    private SmbDeployerRunType myRunType;
    private PropertiesProcessor processor;

    @Override
    protected void createRunType(RunTypeRegistry registry, PluginDescriptor descriptor) {
        myRunType = new SmbDeployerRunType(registry, descriptor);
        processor = myRunType.getRunnerPropertiesProcessor();
    }

    @Test
    public void testUNCPathsPattern() throws Exception {
        assertIllegalTarget("\\\\\\\\host");
        assertIllegalTarget("host");
        assertIllegalTarget("..abracadabra");
        assertIllegalTarget("\\\\host");
        assertIllegalTarget("%variable");
        assertIllegalTarget("variable%");
        assertLegalTarget("%variable%");
        assertLegalTarget("\\\\%variable%");
        assertLegalTarget("\\\\host\\%variable%");
        assertLegalTarget("\\\\host\\%variable%\\subdir");
        assertLegalTarget("\\\\host\\%variable%\\sub.dir");
        assertLegalTarget("\\\\host\\%variable%\\subdir\\sub.dir");
        assertLegalTarget("\\\\host\\share");
        assertLegalTarget("\\\\host\\c$");
        assertLegalTarget("\\\\host\\share\\subdir");
        assertLegalTarget("\\\\sub.host.tld\\share\\subdir");
        assertLegalTarget("\\\\sub.host.tld\\a.share\\subdir");
        assertLegalTarget("\\\\sub.host.tld\\share\\sub.dir");
        assertLegalTarget("\\\\sub.host.tld\\a.share\\sub.dir");
        assertLegalTarget("\\\\host\\share\\sub.dir");
        assertLegalTarget("\\\\host\\a.share\\subdir");
        assertLegalTarget("\\\\host\\a.share\\sub.dir");
        assertLegalTarget("\\\\host\\share\\sub%some.parameter%dir");
        assertLegalTarget("\\\\::1\\share");
        assertLegalTarget("\\\\127.0.0.1\\share");
        assertLegalTarget("\\\\host\\[]&#%^~_-+`");
    }

    private void assertIllegalTarget(String value) {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put(DeployerRunnerConstants.PARAM_TARGET_URL, value);
        final Collection<InvalidProperty> invalidProperties = processor.process(properties);
        assertEquals(invalidProperties.size(), 1, "Should report 1 invalid property");
        InvalidProperty next = invalidProperties.iterator().next();
        assertEquals(next.getPropertyName(), DeployerRunnerConstants.PARAM_TARGET_URL);
    }

    private void assertLegalTarget(String value) {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put(DeployerRunnerConstants.PARAM_TARGET_URL, value);
        final Collection<InvalidProperty> invalidProperties = processor.process(properties);
        assertEquals(invalidProperties.size(), 0, "Should not report any invalid property");
    }
}
