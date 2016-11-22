/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.web.samples.whiteboard.ds.extended;

import org.ops4j.pax.web.service.whiteboard.WelcomeFileMapping;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.http.HttpContext;

@Component
public class PaxWebWhiteboardWelcomeFiles implements WelcomeFileMapping {
    
    @Reference(target="(httpContext.id="+PaxWebWhiteboardHttpContextMapping.HTTP_CONTEXT_ID+")", 
            bind="bindHttpContext", 
            unbind="unbindHttpContext", cardinality=ReferenceCardinality.MANDATORY)
    private HttpContext context;
    
    @Activate
    public void start() {
        System.err.println("WelcomeFiles registered");
    }
    
    @Override
    public String getHttpContextId() {
        return PaxWebWhiteboardHttpContextMapping.HTTP_CONTEXT_ID;
    }

    @Override
    public boolean isRedirect() {
        return true;
    }

    @Override
    public String[] getWelcomeFiles() {
        return new String[] {"index.html"};
    }
    
//    public void bindHttpContext(HttpContext context) {
//        this.context = context;
//    }
//    
//    public void unbindHttpContext(HttpContext context) {
//        this.context = null;
//    }
}
