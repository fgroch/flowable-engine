/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flowable.dmn.engine.impl.hitpolicy;

import org.flowable.dmn.api.RuleExecutionAuditContainer;
import org.flowable.dmn.engine.impl.context.Context;
import org.flowable.dmn.engine.impl.mvel.MvelExecutionContext;
import org.flowable.dmn.model.HitPolicy;
import org.flowable.engine.common.api.FlowableException;

import java.util.Map;

/**
 * @author Yvo Swillens
 */
public class HitPolicyUnique extends AbstractHitPolicy {

    @Override
    public String getHitPolicyName() {
        return HitPolicy.UNIQUE.getValue();
    }

    @Override
    public void evaluateRuleValidity(int ruleNumber, MvelExecutionContext executionContext) {
        for (Map.Entry<Integer, RuleExecutionAuditContainer> entry : executionContext.getAuditContainer().getRuleExecutions().entrySet()) {
            if (entry.getKey().equals(ruleNumber) == false && entry.getValue().isValid()) {
                String hitPolicyViolatedMessage = String.format("HitPolicy UNIQUE violated: rule %d is valid but rule %d was already valid", ruleNumber, entry.getKey());

                if (Context.getDmnEngineConfiguration().isStrictMode()) {
                    executionContext.getAuditContainer().getRuleExecutions().get(ruleNumber).setExceptionMessage(hitPolicyViolatedMessage);
                    throw new FlowableException("HitPolicy UNIQUE violated");
                }
            }
        }

    }
}
