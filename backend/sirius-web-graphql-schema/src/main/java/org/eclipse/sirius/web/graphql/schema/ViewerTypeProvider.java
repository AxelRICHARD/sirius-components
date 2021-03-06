/*******************************************************************************
 * Copyright (c) 2019, 2020 Obeo.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.sirius.web.graphql.schema;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.sirius.web.graphql.utils.schema.ITypeProvider;
import org.eclipse.sirius.web.services.api.accounts.Capabilities;
import org.eclipse.sirius.web.services.api.objects.ChildCreationDescription;
import org.eclipse.sirius.web.services.api.objects.Namespace;
import org.springframework.stereotype.Service;

import graphql.Scalars;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInterfaceType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLType;
import graphql.schema.GraphQLTypeReference;

/**
 * This class is used to create the definition of the Viewer interface and its related types.
 * <p>
 * The types created will match the following GraphQL textual definition:
 * </p>
 *
 * <pre>
 * interface Viewer {
 *   id: ID!
 *   username: String!
 *   stereotypeDescriptions: [StereotypeDescription!]!
 *   projects: [Project!]!
 *   project(projectId: ID!): Project
 *   representationDescriptions(classId: ID): ViewerRepresentationDescriptionConnection!
 *   childCreationDescriptions(classId: ID!): [ChildCreationDescription!]!
 *   rootObjectCreationDescriptions(namespaceId: ID!, suggested: Boolean!): [ChildCreationDescription!]!
 *   namespaces: [Namespace!]!
 *   capabilities: Capabilities!
 * }
 *
 * type User implements Viewer {
 *   id: ID!
 *   username: String!
 *   stereotypeDescriptions: [StereotypeDescription!]!
 *   projects: [Project!]!
 *   project(projectId: ID!): Project
 *   representationDescriptions(classId: ID): ViewerRepresentationDescriptionConnection!
 *   childCreationDescriptions(classId: ID!): [ChildCreationDescription!]!
 *   rootObjectCreationDescriptions(namespaceId: ID!, suggested: Boolean!): [ChildCreationDescription!]!
 *   namespaces: [Namespace!]!
 *   capabilities: Capabilities!
 * }
 *
 * type ViewerRepresentationDescriptionConnection {
 *   edges: [UserRepresentationDescriptionEdge!]!
 *   pageInfo: PageInfo!
 * }
 *
 * type ViewerRepresentationDescriptionEdge {
 *   node: RepresentationDescription!
 * }
 * </pre>
 *
 * @author sbegaudeau
 */
@Service
public class ViewerTypeProvider implements ITypeProvider {
    public static final String TYPE = "Viewer"; //$NON-NLS-1$

    public static final String USER_TYPE = "User"; //$NON-NLS-1$

    public static final String USERNAME_FIELD = "username"; //$NON-NLS-1$

    public static final String PROJECTS_FIELD = "projects"; //$NON-NLS-1$

    public static final String PROJECT_FIELD = "project"; //$NON-NLS-1$

    public static final String STEREOTYPE_DESCRIPTIONS_FIELD = "stereotypeDescriptions"; //$NON-NLS-1$

    public static final String SCHEMA_ID_ARGUMENT = "schemaId"; //$NON-NLS-1$

    public static final String PROJECT_ID_ARGUMENT = "projectId"; //$NON-NLS-1$

    public static final String NAMESPACES_FIELD = "namespaces"; //$NON-NLS-1$

    public static final String ROOT_OBJECT_CREATION_DESCRIPTIONS_FIELD = "rootObjectCreationDescriptions"; //$NON-NLS-1$

    public static final String NAMESPACE_ID_ARGUMENT = "namespaceId"; //$NON-NLS-1$

    public static final String SUGGESTED_ARGUMENT = "suggested"; //$NON-NLS-1$

    public static final String REPRESENTATION_DESCRIPTIONS_FIELD = "representationDescriptions"; //$NON-NLS-1$

    public static final String VIEWER_REPRESENTATION_DESCRIPTIONS_CONNECTION = TYPE + RepresentationDescriptionTypeProvider.TYPE + GraphQLConstants.CONNECTION;

    public static final String VIEWER_REPRESENTATION_DESCRIPTIONS_EDGE = TYPE + RepresentationDescriptionTypeProvider.TYPE + GraphQLConstants.EDGE;

    public static final String CHILD_CREATION_DESCRIPTIONS_FIELD = "childCreationDescriptions"; //$NON-NLS-1$

    public static final String CLASS_ID_ARGUMENT = "classId"; //$NON-NLS-1$

    public static final String CAPABILITIES_FIELD = "capabilities"; //$NON-NLS-1$

    @Override
    public Set<GraphQLType> getTypes() {
        GraphQLInterfaceType viewerInterface = this.getViewerInterface();
        GraphQLObjectType userType = this.getUserType();

        GraphQLObjectType viewerRepresentationDescriptionEdge = new PaginationEdgeTypeProvider(VIEWER_REPRESENTATION_DESCRIPTIONS_EDGE, RepresentationDescriptionTypeProvider.TYPE).getType();
        GraphQLObjectType viewerRepresentationDescriptionConnection = new PaginationConnectionTypeProvider(VIEWER_REPRESENTATION_DESCRIPTIONS_CONNECTION, VIEWER_REPRESENTATION_DESCRIPTIONS_EDGE)
                .getType();

        return Set.of(viewerInterface, userType, viewerRepresentationDescriptionEdge, viewerRepresentationDescriptionConnection);
    }

    private GraphQLInterfaceType getViewerInterface() {
        // @formatter:off
        return GraphQLInterfaceType.newInterface()
                .name(TYPE)
                .fields(this.getViewerFieldDefinitions())
                .build();
        // @formatter:on
    }

    private GraphQLObjectType getUserType() {
        // @formatter:off
        return GraphQLObjectType.newObject()
                .name(USER_TYPE)
                .fields(this.getViewerFieldDefinitions())
                .withInterface(new GraphQLTypeReference(TYPE))
                .build();
        // @formatter:on
    }

    protected List<GraphQLFieldDefinition> getViewerFieldDefinitions() {
        List<GraphQLFieldDefinition> viewerFieldsDefinition = new ArrayList<>();
        viewerFieldsDefinition.add(new IdFieldProvider().getField());
        viewerFieldsDefinition.add(this.getUsernameField());
        viewerFieldsDefinition.add(this.getStereotypeDescriptionsField());
        viewerFieldsDefinition.add(this.getProjectsField());
        viewerFieldsDefinition.add(this.getProjectField());
        viewerFieldsDefinition.add(this.getNamespaceField());
        viewerFieldsDefinition.add(this.getRootObjectCreationDescriptionsField());
        viewerFieldsDefinition.add(this.getRepresentationDescriptionField());
        viewerFieldsDefinition.add(this.getChildCreationDescriptionsField());
        viewerFieldsDefinition.add(this.getCapabilitiesField());
        return viewerFieldsDefinition;
    }

    private GraphQLFieldDefinition getUsernameField() {
        // @formatter:off
        return GraphQLFieldDefinition.newFieldDefinition()
                .name(USERNAME_FIELD)
                .type(new GraphQLNonNull(Scalars.GraphQLString))
                .build();
        // @formatter:on
    }

    private GraphQLFieldDefinition getStereotypeDescriptionsField() {
        // @formatter:off
        return GraphQLFieldDefinition.newFieldDefinition()
                .name(STEREOTYPE_DESCRIPTIONS_FIELD)
                .type(new GraphQLNonNull(new GraphQLList(new GraphQLNonNull(new GraphQLTypeReference(StereotypeDescriptionTypeProvider.TYPE)))))
                .build();
        // @formatter:on
    }

    private GraphQLFieldDefinition getProjectField() {
        // @formatter:off
        return GraphQLFieldDefinition.newFieldDefinition()
                .name(PROJECT_FIELD)
                .type(new GraphQLTypeReference(ProjectTypeProvider.TYPE))
                .argument(this.getProjectIdArgument())
                .build();
        // @formatter:on
    }

    private GraphQLFieldDefinition getProjectsField() {
        // @formatter:off
        return GraphQLFieldDefinition.newFieldDefinition()
                .name(PROJECTS_FIELD)
                .type(new GraphQLNonNull(new GraphQLList(new GraphQLNonNull(new GraphQLTypeReference(ProjectTypeProvider.TYPE)))))
                .build();
        // @formatter:on
    }

    private GraphQLArgument getProjectIdArgument() {
        // @formatter:off
        return GraphQLArgument.newArgument()
                .name(PROJECT_ID_ARGUMENT)
                .type(new GraphQLNonNull(Scalars.GraphQLID))
                .build();
        // @formatter:on
    }

    private GraphQLFieldDefinition getNamespaceField() {
        // @formatter:off
        return GraphQLFieldDefinition.newFieldDefinition()
                .name(NAMESPACES_FIELD)
                .type(new GraphQLNonNull(new GraphQLList(new GraphQLNonNull(new GraphQLTypeReference(Namespace.class.getSimpleName())))))
                .build();
        // @formatter:on
    }

    private GraphQLFieldDefinition getRootObjectCreationDescriptionsField() {
        // @formatter:off
        return GraphQLFieldDefinition.newFieldDefinition()
                .name(ROOT_OBJECT_CREATION_DESCRIPTIONS_FIELD)
                .type(new GraphQLNonNull(new GraphQLList(new GraphQLNonNull(new GraphQLTypeReference(ChildCreationDescription.class.getSimpleName())))))
                .argument(this.getNamespaceIdArgument())
                .argument(this.getSuggestedArgument())
                .build();
        // @formatter:on
    }

    private GraphQLArgument getNamespaceIdArgument() {
        // @formatter:off
        return GraphQLArgument.newArgument()
                .name(NAMESPACE_ID_ARGUMENT)
                .type(new GraphQLNonNull(Scalars.GraphQLID))
                .build();
        // @formatter:on
    }

    private GraphQLArgument getSuggestedArgument() {
        // @formatter:off
        return GraphQLArgument.newArgument()
                .name(SUGGESTED_ARGUMENT)
                .type(new GraphQLNonNull(Scalars.GraphQLBoolean))
                .build();
        // @formatter:on
    }

    private GraphQLFieldDefinition getRepresentationDescriptionField() {
        // @formatter:off
        return GraphQLFieldDefinition.newFieldDefinition()
                .name(REPRESENTATION_DESCRIPTIONS_FIELD)
                .argument(this.getClassIdArgument())
                .type(new GraphQLNonNull(new GraphQLTypeReference(VIEWER_REPRESENTATION_DESCRIPTIONS_CONNECTION)))
                .build();
        // @formatter:on
    }

    private GraphQLFieldDefinition getChildCreationDescriptionsField() {
        // @formatter:off
        return GraphQLFieldDefinition.newFieldDefinition()
                .name(CHILD_CREATION_DESCRIPTIONS_FIELD)
                .argument(this.getClassIdArgument())
                .type(new GraphQLNonNull(new GraphQLList(new GraphQLNonNull(new GraphQLTypeReference(ChildCreationDescription.class.getSimpleName())))))
                .build();
        // @formatter:on
    }

    private GraphQLArgument getClassIdArgument() {
        // @formatter:off
        return GraphQLArgument.newArgument()
                .name(CLASS_ID_ARGUMENT)
                .type(new GraphQLNonNull(Scalars.GraphQLID))
                .build();
        // @formatter:on
    }

    private GraphQLFieldDefinition getCapabilitiesField() {
        // @formatter:off
        return GraphQLFieldDefinition.newFieldDefinition()
                .name(CAPABILITIES_FIELD)
                .type(new GraphQLNonNull(new GraphQLTypeReference(Capabilities.class.getSimpleName())))
                .build();
        // @formatter:on
    }
}
