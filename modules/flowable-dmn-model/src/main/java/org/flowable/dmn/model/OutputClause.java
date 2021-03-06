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
package org.flowable.dmn.model;

/**
 * @author Yvo Swillens
 */
public class OutputClause extends DmnElement {

    protected LiteralExpression defaultOutputEntry;
    protected String name;
    protected String typeRef;

    protected int outputNumber;

    public LiteralExpression getDefaultOutputEntry() {
        return defaultOutputEntry;
    }

    public void setDefaultOutputEntry(LiteralExpression defaultOutputEntry) {
        this.defaultOutputEntry = defaultOutputEntry;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypeRef() {
        return typeRef;
    }

    public void setTypeRef(String typeRef) {
        this.typeRef = typeRef;
    }

    public int getOutputNumber() {
        return outputNumber;
    }

    public void setOutputNumber(int outputNumber) {
        this.outputNumber = outputNumber;
    }
}
