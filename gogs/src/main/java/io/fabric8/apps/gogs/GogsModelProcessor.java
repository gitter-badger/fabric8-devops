/*
 * Copyright 2005-2015 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version
 * 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */

package io.fabric8.apps.gogs;

import io.fabric8.kubernetes.generator.annotation.KubernetesModelProcessor;
import io.fabric8.openshift.api.model.TemplateBuilder;
import io.fabric8.utils.Base64Encoder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@KubernetesModelProcessor
public class GogsModelProcessor {

    private static final String NAME = "gogs";

    public void onList(TemplateBuilder builder) {
        Map<String, String> secretData = new HashMap<>();

        // TODO would be nice to be able to pass these in as expressions
        // and have th base64 conversion get done automatically
        secretData.put("username", Base64Encoder.encode("gogsadmin"));
        secretData.put("password", Base64Encoder.encode("RedHat$1"));

        builder.addNewOAuthClientObject()
                .withNewMetadata()
                .withName(NAME)
                .and()
                .withRedirectURIs(Arrays.asList(
                        "http://localhost:3000",
                        "http://gogs.${DOMAIN}",
                        "https://gogs.${DOMAIN}"))
                .and()

                // lets add a default user secret for the default user
                .addNewSecretObject()
                .withNewMetadata()
                .withNamespace("user-secrets-source-${KUBERNETES_ADMIN_USER}")
                .withName("gogs-https-${KUBERNETES_ADMIN_USER}")
                .endMetadata()
                .withData(secretData)
                .endSecretObject()

                .build();
    }
}
