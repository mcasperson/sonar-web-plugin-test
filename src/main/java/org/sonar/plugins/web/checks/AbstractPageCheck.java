/*
 * Copyright (C) 2010 Matthijs Galesloot
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

package org.sonar.plugins.web.checks;

import org.apache.commons.lang.StringUtils;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.Violation;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.visitor.AbstractNodeVisitor;

/**
 * @author Matthijs Galesloot
 */
public abstract class AbstractPageCheck extends AbstractNodeVisitor {

  private Rule rule;

  protected static final class QualifiedAttribute {

    public String attributeName;

    public String nodeName;

    private QualifiedAttribute(String nodeName, String attributeName) {
      this.nodeName = nodeName;
      this.attributeName = attributeName;
    }
  }

  protected String getAttributesAsString(QualifiedAttribute[] qualifiedAttributes) {
    StringBuilder sb = new StringBuilder();
    if (qualifiedAttributes != null) {
      for (QualifiedAttribute a : qualifiedAttributes) {
        if (sb.length() > 0) {
          sb.append(",");
        }
        sb.append(a.nodeName);
        sb.append('.');
        sb.append(a.attributeName);
      }
    }
    return sb.toString();
  }

  public QualifiedAttribute[] parseAttributes(String attributesList) {
    String[] qualifiedAttributeList = StringUtils.split(attributesList, ",");

    QualifiedAttribute[] qualifiedAttributes = new QualifiedAttribute[qualifiedAttributeList.length];
    int n = 0;
    for (String qualifiedAttribute : qualifiedAttributeList) {
      qualifiedAttribute = qualifiedAttribute.trim();
      if (qualifiedAttribute.indexOf('.') >= 0) {
        qualifiedAttributes[n++] = new QualifiedAttribute(StringUtils.substringBefore(qualifiedAttribute, "."), StringUtils.substringAfter(
            qualifiedAttribute, "."));
      } else {
        qualifiedAttributes[n++] = new QualifiedAttribute(null, qualifiedAttribute);
      }
    }
    return qualifiedAttributes;
  }

  protected final void createViolation(int linePosition) {
    createViolation(linePosition, null);
  }

  protected final void createViolation(int linePosition, String message) {
    Violation violation = new Violation(rule);
    violation.setMessage(message == null ? rule.getDescription() : message);
    violation.setLineId(linePosition);
    getWebSourceCode().addViolation(violation);
  }

  protected final void createViolation(Node node) {
    createViolation(node.getStartLinePosition());
  }

  public final Rule getRule() {
    return rule;
  }

  public final String getRuleKey() {
    return rule.getConfigKey();
  }

  public final void setRule(Rule rule) {
    this.rule = rule;
  }
}