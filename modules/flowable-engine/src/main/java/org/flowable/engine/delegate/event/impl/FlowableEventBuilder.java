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
package org.flowable.engine.delegate.event.impl;

import java.util.Map;

import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.FlowNode;
import org.flowable.engine.common.api.delegate.event.FlowableEntityEvent;
import org.flowable.engine.common.api.delegate.event.FlowableEvent;
import org.flowable.engine.common.api.delegate.event.FlowableExceptionEvent;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.event.*;
import org.flowable.engine.impl.context.ExecutionContext;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.persistence.entity.IdentityLinkEntity;
import org.flowable.engine.impl.variable.VariableType;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.Job;
import org.flowable.engine.task.Task;

/**
 * Builder class used to create {@link FlowableEvent} implementations.
 *
 * @author Frederik Heremans
 */
public class FlowableEventBuilder {

    /**
     * @param type
     *            type of event
     * @return an {@link FlowableEvent} that doesn't have it's execution context-fields filled, as the event is a global event, independent of any running execution.
     */
    public static FlowableEvent createGlobalEvent(FlowableEngineEventType type) {
        FlowableEventImpl newEvent = new FlowableEventImpl(type);
        return newEvent;
    }

    public static FlowableEvent createEvent(FlowableEngineEventType type, String executionId, String processInstanceId, String processDefinitionId) {
        FlowableEventImpl newEvent = new FlowableEventImpl(type);
        newEvent.setExecutionId(executionId);
        newEvent.setProcessDefinitionId(processDefinitionId);
        newEvent.setProcessInstanceId(processInstanceId);
        return newEvent;
    }

    /**
     * @param type
     *            type of event
     * @param entity
     *            the entity this event targets
     * @return an {@link FlowableEntityEvent}. In case an {@link ExecutionContext} is active, the execution related event fields will be populated. If not, execution details will be retrieved from the
     *         {@link Object} if possible.
     */
    public static FlowableEntityEvent createEntityEvent(FlowableEngineEventType type, Object entity) {
        FlowableEntityEventImpl newEvent = new FlowableEntityEventImpl(entity, type);

        // In case an execution-context is active, populate the event fields
        // related to the execution
        populateEventWithCurrentContext(newEvent);
        return newEvent;
    }

    /**
     * @param entity
     *            the entity this event targets
     * @param variables
     *            the variables associated with this entity
     * @return an {@link FlowableEntityEvent}. In case an {@link ExecutionContext} is active, the execution related event fields will be populated. If not, execution details will be retrieved from the
     *         {@link Object} if possible.
     */
    @SuppressWarnings("rawtypes")
    public static FlowableProcessStartedEvent createProcessStartedEvent(final Object entity,
            final Map variables, final boolean localScope) {
        final FlowableProcessStartedEventImpl newEvent = new FlowableProcessStartedEventImpl(entity, variables, localScope);

        // In case an execution-context is active, populate the event fields related to the execution
        populateEventWithCurrentContext(newEvent);
        return newEvent;
    }

    /**
     * @param type
     *            type of event
     * @param entity
     *            the entity this event targets
     * @param variables
     *            the variables associated with this entity
     * @return an {@link FlowableEntityEvent}. In case an {@link ExecutionContext} is active, the execution related event fields will be populated. If not, execution details will be retrieved from the
     *         {@link Object} if possible.
     */
    @SuppressWarnings("rawtypes")
    public static FlowableEntityWithVariablesEvent createEntityWithVariablesEvent(FlowableEngineEventType type, Object entity, Map variables, boolean localScope) {
        FlowableEntityWithVariablesEventImpl newEvent = new FlowableEntityWithVariablesEventImpl(entity, variables, localScope, type);

        // In case an execution-context is active, populate the event fields
        // related to the execution
        populateEventWithCurrentContext(newEvent);
        return newEvent;
    }

    /**
     * @param type
     *            type of event
     * @param newJob
     *            the new job that was created due to the reschedule
     * @param originalJobId
     *            the job id of the original job that was rescheduled
     * @return an {@link FlowableEntityEvent}. In case an {@link ExecutionContext} is active, the execution related event fields will be populated. If not, execution details will be retrieved from the
     *         {@link Object} if possible.
     */
    public static FlowableJobRescheduledEvent createJobRescheduledEvent(FlowableEngineEventType type, Job newJob, String originalJobId) {
        FlowableJobRescheduledEventImpl newEvent = new FlowableJobRescheduledEventImpl(newJob, originalJobId, type);

        populateEventWithCurrentContext(newEvent);
        return newEvent;
    }

    public static FlowableSequenceFlowTakenEvent createSequenceFlowTakenEvent(ExecutionEntity executionEntity, FlowableEngineEventType type,
            String sequenceFlowId, String sourceActivityId, String sourceActivityName, String sourceActivityType, Object sourceActivityBehavior,
            String targetActivityId, String targetActivityName, String targetActivityType, Object targetActivityBehavior) {

        FlowableSequenceFlowTakenEventImpl newEvent = new FlowableSequenceFlowTakenEventImpl(type);

        if (executionEntity != null) {
            newEvent.setExecutionId(executionEntity.getId());
            newEvent.setProcessInstanceId(executionEntity.getProcessInstanceId());
            newEvent.setProcessDefinitionId(executionEntity.getProcessDefinitionId());
        }

        newEvent.setId(sequenceFlowId);
        newEvent.setSourceActivityId(sourceActivityId);
        newEvent.setSourceActivityName(sourceActivityName);
        newEvent.setSourceActivityType(sourceActivityType);
        newEvent.setSourceActivityBehaviorClass(sourceActivityBehavior != null ? sourceActivityBehavior.getClass().getCanonicalName() : null);
        newEvent.setTargetActivityId(targetActivityId);
        newEvent.setTargetActivityName(targetActivityName);
        newEvent.setTargetActivityType(targetActivityType);
        newEvent.setTargetActivityBehaviorClass(targetActivityBehavior != null ? targetActivityBehavior.getClass().getCanonicalName() : null);

        return newEvent;
    }

    /**
     * @param type
     *            type of event
     * @param entity
     *            the entity this event targets
     * @return an {@link FlowableEntityEvent}
     */
    public static FlowableEntityEvent createEntityEvent(FlowableEngineEventType type, Object entity, String executionId, String processInstanceId, String processDefinitionId) {
        FlowableEntityEventImpl newEvent = new FlowableEntityEventImpl(entity, type);

        newEvent.setExecutionId(executionId);
        newEvent.setProcessInstanceId(processInstanceId);
        newEvent.setProcessDefinitionId(processDefinitionId);
        return newEvent;
    }

    /**
     * @param type
     *            type of event
     * @param entity
     *            the entity this event targets
     * @param cause
     *            the cause of the event
     * @return an {@link FlowableEntityEvent} that is also instance of {@link FlowableExceptionEvent}. In case an {@link ExecutionContext} is active, the execution related event fields will be
     *         populated.
     */
    public static FlowableEntityEvent createEntityExceptionEvent(FlowableEngineEventType type, Object entity, Throwable cause) {
        FlowableEntityExceptionEventImpl newEvent = new FlowableEntityExceptionEventImpl(entity, type, cause);

        // In case an execution-context is active, populate the event fields
        // related to the execution
        populateEventWithCurrentContext(newEvent);
        return newEvent;
    }

    /**
     * @param type
     *            type of event
     * @param entity
     *            the entity this event targets
     * @param cause
     *            the cause of the event
     * @return an {@link FlowableEntityEvent} that is also instance of {@link FlowableExceptionEvent}.
     */
    public static FlowableEntityEvent createEntityExceptionEvent(FlowableEngineEventType type, Object entity, Throwable cause, String executionId, String processInstanceId, String processDefinitionId) {
        FlowableEntityExceptionEventImpl newEvent = new FlowableEntityExceptionEventImpl(entity, type, cause);

        newEvent.setExecutionId(executionId);
        newEvent.setProcessInstanceId(processInstanceId);
        newEvent.setProcessDefinitionId(processDefinitionId);
        return newEvent;
    }

    public static FlowableActivityEvent createActivityEvent(FlowableEngineEventType type, String activityId, String activityName, String executionId,
            String processInstanceId, String processDefinitionId, FlowElement flowElement) {

        FlowableActivityEventImpl newEvent = new FlowableActivityEventImpl(type);
        newEvent.setActivityId(activityId);
        newEvent.setActivityName(activityName);
        newEvent.setExecutionId(executionId);
        newEvent.setProcessDefinitionId(processDefinitionId);
        newEvent.setProcessInstanceId(processInstanceId);

        if (flowElement instanceof FlowNode) {
            FlowNode flowNode = (FlowNode) flowElement;
            newEvent.setActivityType(parseActivityType(flowNode));
            Object behaviour = flowNode.getBehavior();
            if (behaviour != null) {
                newEvent.setBehaviorClass(behaviour.getClass().getCanonicalName());
            }
        }

        return newEvent;
    }

    protected static String parseActivityType(FlowNode flowNode) {
        String elementType = flowNode.getClass().getSimpleName();
        elementType = elementType.substring(0, 1).toLowerCase() + elementType.substring(1);
        return elementType;
    }

    public static FlowableActivityCancelledEvent createActivityCancelledEvent(String activityId, String activityName, String executionId,
            String processInstanceId, String processDefinitionId, String activityType, Object cause) {

        FlowableActivityCancelledEventImpl newEvent = new FlowableActivityCancelledEventImpl();
        newEvent.setActivityId(activityId);
        newEvent.setActivityName(activityName);
        newEvent.setExecutionId(executionId);
        newEvent.setProcessDefinitionId(processDefinitionId);
        newEvent.setProcessInstanceId(processInstanceId);
        newEvent.setActivityType(activityType);
        newEvent.setCause(cause);
        return newEvent;
    }

    public static FlowableCancelledEvent createCancelledEvent(String executionId, String processInstanceId, String processDefinitionId, Object cause) {
        FlowableProcessCancelledEventImpl newEvent = new FlowableProcessCancelledEventImpl();
        newEvent.setExecutionId(executionId);
        newEvent.setProcessDefinitionId(processDefinitionId);
        newEvent.setProcessInstanceId(processInstanceId);
        newEvent.setCause(cause);
        return newEvent;
    }

    public static FlowableProcessTerminatedEvent createTerminateEvent(ExecutionEntity execution, Object cause) {
        return new FlowableProcessTerminatedEventImpl(execution, cause);
    }

    public static FlowableSignalEvent createSignalEvent(FlowableEngineEventType type, String activityId, String signalName, Object signalData, String executionId, String processInstanceId,
            String processDefinitionId) {
        FlowableSignalEventImpl newEvent = new FlowableSignalEventImpl(type);
        newEvent.setActivityId(activityId);
        newEvent.setExecutionId(executionId);
        newEvent.setProcessDefinitionId(processDefinitionId);
        newEvent.setProcessInstanceId(processInstanceId);
        newEvent.setSignalName(signalName);
        newEvent.setSignalData(signalData);
        return newEvent;
    }

    public static FlowableMessageEvent createMessageEvent(FlowableEngineEventType type, String activityId, String messageName, Object payload, String executionId, String processInstanceId,
            String processDefinitionId) {
        FlowableMessageEventImpl newEvent = new FlowableMessageEventImpl(type);
        newEvent.setActivityId(activityId);
        newEvent.setExecutionId(executionId);
        newEvent.setProcessDefinitionId(processDefinitionId);
        newEvent.setProcessInstanceId(processInstanceId);
        newEvent.setMessageName(messageName);
        newEvent.setMessageData(payload);
        return newEvent;
    }

    public static FlowableErrorEvent createErrorEvent(FlowableEngineEventType type, String activityId, String errorId, String errorCode,
            String executionId, String processInstanceId, String processDefinitionId) {
        FlowableErrorEventImpl newEvent = new FlowableErrorEventImpl(type);
        newEvent.setActivityId(activityId);
        newEvent.setExecutionId(executionId);
        newEvent.setProcessDefinitionId(processDefinitionId);
        newEvent.setProcessInstanceId(processInstanceId);
        newEvent.setErrorId(errorId);
        newEvent.setErrorCode(errorCode);
        return newEvent;
    }

    public static FlowableVariableEvent createVariableEvent(FlowableEngineEventType type, String variableName, Object variableValue, VariableType variableType, String taskId, String executionId,
            String processInstanceId, String processDefinitionId) {
        FlowableVariableEventImpl newEvent = new FlowableVariableEventImpl(type);
        newEvent.setVariableName(variableName);
        newEvent.setVariableValue(variableValue);
        newEvent.setVariableType(variableType);
        newEvent.setTaskId(taskId);
        newEvent.setExecutionId(executionId);
        newEvent.setProcessDefinitionId(processDefinitionId);
        newEvent.setProcessInstanceId(processInstanceId);
        return newEvent;
    }

    protected static void populateEventWithCurrentContext(FlowableEventImpl event) {
        if (event instanceof FlowableEntityEvent) {
            Object persistedObject = ((FlowableEntityEvent) event).getEntity();
            if (persistedObject instanceof Job) {
                event.setExecutionId(((Job) persistedObject).getExecutionId());
                event.setProcessInstanceId(((Job) persistedObject).getProcessInstanceId());
                event.setProcessDefinitionId(((Job) persistedObject).getProcessDefinitionId());
            } else if (persistedObject instanceof DelegateExecution) {
                event.setExecutionId(((DelegateExecution) persistedObject).getId());
                event.setProcessInstanceId(((DelegateExecution) persistedObject).getProcessInstanceId());
                event.setProcessDefinitionId(((DelegateExecution) persistedObject).getProcessDefinitionId());
            } else if (persistedObject instanceof IdentityLinkEntity) {
                IdentityLinkEntity idLink = (IdentityLinkEntity) persistedObject;
                if (idLink.getProcessDefinitionId() != null) {
                    event.setProcessDefinitionId(idLink.getProcessDefId());
                } else if (idLink.getProcessInstance() != null) {
                    event.setProcessDefinitionId(idLink.getProcessInstance().getProcessDefinitionId());
                    event.setProcessInstanceId(idLink.getProcessInstanceId());
                    event.setExecutionId(idLink.getProcessInstanceId());
                } else if (idLink.getTask() != null) {
                    event.setProcessDefinitionId(idLink.getTask().getProcessDefinitionId());
                    event.setProcessInstanceId(idLink.getTask().getProcessInstanceId());
                    event.setExecutionId(idLink.getTask().getExecutionId());
                }
            } else if (persistedObject instanceof Task) {
                event.setProcessInstanceId(((Task) persistedObject).getProcessInstanceId());
                event.setExecutionId(((Task) persistedObject).getExecutionId());
                event.setProcessDefinitionId(((Task) persistedObject).getProcessDefinitionId());
            } else if (persistedObject instanceof ProcessDefinition) {
                event.setProcessDefinitionId(((ProcessDefinition) persistedObject).getId());
            }
        }
    }
}
