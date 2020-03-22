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

package jetbrains.buildServer.deployer.agent.base;

import jetbrains.buildServer.ExtensionHolder;
import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.*;
import jetbrains.buildServer.agent.impl.artifacts.ArtifactsBuilder;
import jetbrains.buildServer.agent.impl.artifacts.ArtifactsCollection;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

/**
 * Created by Nikita.Skvortsov
 * Date: 10/1/12, 10:44 AM
 */
public abstract class BaseDeployerRunner implements AgentBuildRunner {
  protected final ExtensionHolder myExtensionHolder;

  public BaseDeployerRunner(@NotNull final ExtensionHolder extensionHolder) {
    myExtensionHolder = extensionHolder;
  }

  protected abstract BuildProcess getDeployerProcess(@NotNull final BuildRunnerContext context,
                                                     @NotNull final String username,
                                                     @NotNull final String password,
                                                     @NotNull final String target,
                                                     @NotNull final List<ArtifactsCollection> artifactsCollections) throws RunBuildException;

  @NotNull
  @Override
  public abstract AgentBuildRunnerInfo getRunnerInfo();
}
