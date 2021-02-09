/*******************************************************************************
 * Copyright (c) 2019, 2021 Obeo.
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
import { httpOrigin } from 'common/URL';
import {
  boundsFeature,
  connectableFeature,
  createFeatureSet,
  deletableFeature,
  fadeFeature,
  hoverFeedbackFeature,
  layoutContainerFeature,
  popupFeature,
  selectFeature,
  viewportFeature,
} from 'sprotty';

/**
 * Convert the given diagram object to a Sprotty diagram.
 *
 * SiriusWeb diagram and Sprotty diagram does not match exactly the same API.
 * This converter will ensure the creation of a proper Sprotty diagram from a given Sirius Web diagram..
 *
 * @param diagram the diagram object to convert
 * @return a Sprotty diagram object
 */
export const convertDiagram = (diagram) => {
  const { id, descriptionId, kind, targetObjectId, label, position, size } = diagram;
  const nodes = diagram.nodes.map((node) => convertNode(node));
  const edges = diagram.edges.map((edge) => convertEdge(edge));

  return {
    id,
    descriptionId,
    kind,
    type: 'graph',
    targetObjectId,
    label,
    position,
    features: createFeatureSet([hoverFeedbackFeature, viewportFeature]),
    size,
    children: [...nodes, ...edges],
  };
};

const convertNode = (node) => {
  const {
    id,
    type,
    targetObjectId,
    targetObjectKind,
    targetObjectLabel,
    descriptionId,
    label,
    style,
    size,
    position,
  } = node;

  let borderNodes = [];
  if (node.borderNodes) {
    borderNodes = node.borderNodes.map((borderNode) => convertNode(borderNode));
  }
  let childNodes = [];
  if (node.childNodes) {
    childNodes = node.childNodes.map((childNode) => convertNode(childNode));
  }

  let convertedLabel;
  if (label?.style?.iconURL !== undefined && label?.style?.iconURL !== '') {
    convertedLabel = { ...label, style: { ...label.style, iconURL: httpOrigin + label.style.iconURL } };
  } else {
    convertedLabel = label;
  }

  let convertedStyle;
  if (style?.imageURL !== undefined && style?.imageURL !== '') {
    convertedStyle = { ...style, imageURL: httpOrigin + style.imageURL };
  } else {
    convertedStyle = style;
  }

  return {
    id,
    type,
    targetObjectId,
    targetObjectKind,
    targetObjectLabel,
    descriptionId,
    style: convertedStyle,
    size,
    position,
    features: createFeatureSet([
      connectableFeature,
      deletableFeature,
      selectFeature,
      boundsFeature,
      layoutContainerFeature,
      fadeFeature,
      hoverFeedbackFeature,
      popupFeature,
    ]),
    editableLabel: convertedLabel,
    children: [convertedLabel, ...borderNodes, ...childNodes],
  };
};

const convertEdge = (edge) => {
  const {
    id,
    type,
    targetObjectId,
    targetObjectKind,
    targetObjectLabel,
    descriptionId,
    beginLabel,
    centerLabel,
    endLabel,
    sourceId,
    targetId,
    style,
    routingPoints,
  } = edge;

  let children = [];
  if (beginLabel) {
    let convertedBeginLabel;
    if (beginLabel?.style?.iconURL !== undefined && beginLabel?.style?.iconURL !== '') {
      convertedBeginLabel = {
        ...beginLabel,
        style: { ...beginLabel.style, iconURL: httpOrigin + beginLabel.style.iconURL },
      };
    } else {
      convertedBeginLabel = beginLabel;
    }
    children.push(convertedBeginLabel);
  }
  if (centerLabel) {
    let convertedCenterLabel;
    if (centerLabel?.style?.iconURL !== undefined && centerLabel?.style?.iconURL !== '') {
      convertedCenterLabel = {
        ...centerLabel,
        style: { ...centerLabel.style, iconURL: httpOrigin + centerLabel.style.iconURL },
      };
    } else {
      convertedCenterLabel = centerLabel;
    }
    children.push(convertedCenterLabel);
  }
  if (endLabel) {
    let convertedEndLabel;
    if (endLabel?.style?.iconURL !== undefined && endLabel?.style?.iconURL !== '') {
      convertedEndLabel = {
        ...endLabel,
        style: { ...endLabel.style, iconURL: httpOrigin + endLabel.style.iconURL },
      };
    } else {
      convertedEndLabel = endLabel;
    }
    children.push(convertedEndLabel);
  }

  return {
    id,
    type,
    targetObjectId,
    targetObjectKind,
    targetObjectLabel,
    descriptionId,
    sourceId,
    targetId,
    style,
    routingPoints,
    features: createFeatureSet([deletableFeature, selectFeature, fadeFeature, hoverFeedbackFeature]),
    children: children,
  };
};
