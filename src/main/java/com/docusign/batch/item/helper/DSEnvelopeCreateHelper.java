package com.docusign.batch.item.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import com.docusign.batch.domain.AppConstants;
import com.docusign.batch.item.file.CustomFlatFileHeaderCallback;
import com.docusign.batch.item.file.DSTemplateFieldExtractor;
import com.docusign.exception.InvalidInputException;
import com.docusign.proserv.application.utils.PSProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class DSEnvelopeCreateHelper {

	final static Logger logger = LogManager.getLogger(DSEnvelopeCreateHelper.class);

	@Autowired
	Environment env;

	@Autowired
	PSProperties psProps;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	DSTemplateFieldExtractor dsTemplateFieldExtractor;

	@Autowired
	CustomFlatFileHeaderCallback customFlatFileHeaderCallback;

	public String createEnvelopeRequest(String[] rowAsArray, String envelopeStatus, String baseUri,
			String accountGuid) {

		String[] headerAsArray = customFlatFileHeaderCallback.getHeaderArray();
		Map<String, List<Map<String, String>>> templateIdDataMap = dsTemplateFieldExtractor
				.readTemplateAndPopulateMap(baseUri, accountGuid);

		String envelopeReqBodyJSON = null;
		Map<String, String> envelopeLevelFieldMap = new HashMap<String, String>();

		if (null != templateIdDataMap && !templateIdDataMap.isEmpty()) {// templateId
																		// included in
																		// the
																		// columnName

			List<ObjectNode> compositeTemplateNodeList = new ArrayList<>(templateIdDataMap.size());
			templateIdDataMap.forEach((templateId, value) -> {

				Map<String, String> roleNameRecipientTypeMap = value.get(0);
				Map<String, String> roleNameLabelTabTypeMap = value.get(1);
				Map<String, String> roleNameGroupTabTypeMap = value.get(2);

				Map<String, String> roleNameAuthMap = new HashMap<String, String>();
				Map<String, String> roleNamePhoneNumberMap = new HashMap<String, String>();

				Map<String, ObjectNode> inputRoleNameProcessedMap = new HashMap<String, ObjectNode>();

				Map<String, List<ObjectNode>> recipientTypeObjectNodeListMap = new HashMap<String, List<ObjectNode>>();
				Map<String, Map<String, List<ObjectNode>>> roleTabTypeObjectNodeListMap = new HashMap<String, Map<String, List<ObjectNode>>>();

				Map<String, ObjectNode> roleSignatureProviderOptionsNodePOJOMap = new HashMap<String, ObjectNode>();
				Map<String, ObjectNode> roleSignatureProviderNameNodeMap = new HashMap<String, ObjectNode>();

				Map<String, ObjectNode> roleEmailNotificationNodeMap = new HashMap<String, ObjectNode>();

				ArrayNode ecfArray = objectMapper.createArrayNode();

				logger.info("headerAsArray length >> " + headerAsArray.length);
				logger.info("rowAsArray length >> " + rowAsArray.length);

				int recipientId = 1;
				for (int i = 0; i < headerAsArray.length && i < rowAsArray.length; i++) {

					if (!StringUtils.isEmpty(rowAsArray[i])) {

						String columnName = headerAsArray[i].trim();

						if (columnName.contains(AppConstants.BULK_CSV_DELIMITER)) {

							String roleName = null;
							String recipientType = null;// signers,agents,editors,intermediaries,carbonCopies,certifiedDeliveries,inPersonSigners,seals,witnesses
							String fieldName = null;// NOT
													// textTabs,numberTabs,ssnTabs,dateTabs,zipTabs,emailTabs,checkboxTabs,listTabs,formulaTabs,radioGroupTabs

							List<String> columnSplit = Stream.of(columnName.split(AppConstants.BULK_CSV_DELIMITER))
									.map(String::trim).collect(Collectors.toList());

							roleName = columnSplit.get(0);
							fieldName = columnSplit.get(1);
							recipientType = roleNameRecipientTypeMap.get(roleName);

							logger.debug(" roleName " + roleName + " recipientType " + recipientType + " fieldName "
									+ fieldName);
							if (null != recipientType) {

								ObjectNode recipientTypeNode = null;
								if (null != inputRoleNameProcessedMap.get(roleName)) {

									recipientTypeNode = inputRoleNameProcessedMap.get(roleName);
								} else {

									recipientTypeNode = objectMapper.createObjectNode();

									recipientTypeNode.put("recipientId", recipientId);
									recipientTypeNode.put("roleName", roleName);
									recipientId++;
									inputRoleNameProcessedMap.put(roleName, recipientTypeNode);

									if (null != recipientTypeObjectNodeListMap.get(recipientType)) {

										List<ObjectNode> recipientTypeNodeList = recipientTypeObjectNodeListMap
												.get(recipientType);
										recipientTypeNodeList.add(recipientTypeNode);

									} else {

										List<ObjectNode> recipientTypeNodeList = new ArrayList<ObjectNode>();
										recipientTypeNodeList.add(recipientTypeNode);
										recipientTypeObjectNodeListMap.put(recipientType, recipientTypeNodeList);
									}
								}

								if ((null != roleNameLabelTabTypeMap && null != roleNameLabelTabTypeMap.get(columnName))
										|| (null != roleNameGroupTabTypeMap
												&& null != roleNameGroupTabTypeMap.get(columnName))) {

									prepareTabCollectionForEachRole(roleNameLabelTabTypeMap, roleNameGroupTabTypeMap,
											roleTabTypeObjectNodeListMap, rowAsArray[i], columnName, roleName,
											fieldName);
								} else {

									String fieldNameLC = fieldName.toLowerCase()
											.replaceAll(AppConstants.WHITESPACE_REGEX, "");

									String rowColumnValue = rowAsArray[i].trim();

									switch (fieldNameLC) {

									case "identification":

										setupIdentificationAuthData(roleNameAuthMap, rowColumnValue, roleName,
												recipientTypeNode);
										break;
									case "phonenumber":
										roleNamePhoneNumberMap.put(roleName, rowColumnValue);
										break;
									case "signatureprovider":

										ObjectNode signatureProviderNameNode = objectMapper.createObjectNode();
										signatureProviderNameNode.put("signatureProviderName",
												env.getProperty("ds.signatureprovider." + rowColumnValue.toLowerCase()
														.replaceAll(AppConstants.WHITESPACE_REGEX, "")));

										roleSignatureProviderNameNodeMap.put(roleName, signatureProviderNameNode);
										break;
									case "workflowid":

										if (!StringUtils.isEmpty(rowColumnValue)) {

											ObjectNode workflowIdNode = objectMapper.createObjectNode();
											workflowIdNode.put("workflowId", rowColumnValue);

											recipientTypeNode.putPOJO("identityVerification", workflowIdNode);
										}
										break;
									case "emailbody":
									case "emailsubject":
									case "supportedlanguage":

										prepareEmailNotifcationRoleBasedNodeMap(roleEmailNotificationNodeMap, roleName,
												fieldNameLC, rowColumnValue);
										break;
									default:
										checkAndPopulateRecipientNode(recipientTypeNode, fieldName, rowColumnValue);
									}
								}
							}

						} else {
							// set envelope level fields like emailSubject, email Body etc in
							// envelopeLevelFieldMap else they are ECF

							if (!StringUtils.isEmpty(rowAsArray[i])) {

								if (null != psProps.getEnvelopeFields() && psProps.getEnvelopeFields().stream()
										.anyMatch(s -> s.equalsIgnoreCase(columnName))) {

									envelopeLevelFieldMap.put(columnName, rowAsArray[i]);
								} else {

									ObjectNode textField = objectMapper.createObjectNode();
									textField.put("name", columnName);
									textField.put("value", rowAsArray[i]);

									ecfArray.add(textField);
								}
							}
						}
					}

				}

				populateRoleRelatedDataInRecipientNode(inputRoleNameProcessedMap, roleNameAuthMap,
						roleNamePhoneNumberMap, roleSignatureProviderOptionsNodePOJOMap,
						roleSignatureProviderNameNodeMap, roleEmailNotificationNodeMap);// This sets all fields for each
																						// role

				ObjectNode allRecipientTypeNode = prepareAllRecipientsTypeNode(recipientTypeObjectNodeListMap,
						roleTabTypeObjectNodeListMap);// This sets "recipients" property

				createCompositeTemplateNodeList(compositeTemplateNodeList, templateId, ecfArray, allRecipientTypeNode);

			});

			ArrayNode compositeTemplateArrayNode = objectMapper.createArrayNode();

			logger.debug("compositeTemplateNodeList size is " + compositeTemplateNodeList);
			compositeTemplateNodeList.forEach(compositeTemplateNode -> {

				compositeTemplateArrayNode.add(compositeTemplateNode);
			});

			ObjectNode compositeTemplatesObjectNode = objectMapper.createObjectNode();
			compositeTemplatesObjectNode.putPOJO("compositeTemplates", compositeTemplateArrayNode);
			compositeTemplatesObjectNode.put("status", envelopeStatus);

			if (null != envelopeLevelFieldMap && !envelopeLevelFieldMap.isEmpty()) {
				envelopeLevelFieldMap.forEach((field, value) -> {

					compositeTemplatesObjectNode.put(field, value);
				});
			}

			try {
				envelopeReqBodyJSON = objectMapper.writerWithDefaultPrettyPrinter()
						.writeValueAsString(compositeTemplatesObjectNode);
				logger.debug("CreateEnvelopeJSON ::: " + envelopeReqBodyJSON);
			} catch (JsonProcessingException exp) {

				logger.error("JSON Processing error with exp message " + exp.getMessage());
				exp.printStackTrace();
			}

		} else {

			throw new RuntimeException("Some issue in processing template");
		}

		return envelopeReqBodyJSON;
	}

	private void prepareEmailNotifcationRoleBasedNodeMap(Map<String, ObjectNode> roleEmailNotificationNodeMap,
			String roleName, String fieldNameLC, String rowColumnValue) {

		if (null != roleEmailNotificationNodeMap.get(roleName)) {

			ObjectNode emailNotificationRoleBasedNode = roleEmailNotificationNodeMap.get(roleName);
			emailNotificationRoleBasedNode.put(fieldNameLC, rowColumnValue);
		} else {

			ObjectNode emailNotificationRoleBasedNode = objectMapper.createObjectNode();
			emailNotificationRoleBasedNode.put(fieldNameLC, rowColumnValue);

			roleEmailNotificationNodeMap.put(roleName, emailNotificationRoleBasedNode);
		}
	}

	private void setupIdentificationAuthData(Map<String, String> roleNameAuthMap, String identificationType,
			String roleName, ObjectNode recipientTypeNode) {

		recipientTypeNode.put("requireIdLookup", true);

		if ("idcheck".equalsIgnoreCase(identificationType)) {

			recipientTypeNode.put("idCheckConfigurationName", "ID Check$");
		} else {

			roleNameAuthMap.put(roleName, identificationType);
		}

	}

	private void prepareTabCollectionForEachRole(Map<String, String> roleNameLabelTabTypeMap,
			Map<String, String> roleNameGroupTabTypeMap,
			Map<String, Map<String, List<ObjectNode>>> roleTabTypeObjectNodeListMap, String rowColumnValue,
			String columnName, String roleName, String fieldName) {

		if (null != roleNameLabelTabTypeMap && null != roleNameLabelTabTypeMap.get(columnName)) {

			String tabType = roleNameLabelTabTypeMap.get(columnName);// textTabs,numberTabs,ssnTabs,dateTabs,zipTabs,emailTabs,checkboxTabs,listTabs,formulaTabs

			ObjectNode tabTypeNode = objectMapper.createObjectNode();
			if ("checkboxTabs".equalsIgnoreCase(tabType)) {

				tabTypeNode.put("tabLabel", fieldName);

				if ("X".equalsIgnoreCase(rowColumnValue) || Boolean.getBoolean(rowColumnValue)) {

					tabTypeNode.put("selected", true);
				}
			} else {

				tabTypeNode.put("tabLabel", fieldName);
				tabTypeNode.put("value", rowColumnValue);
			}

			populateRoleNameObjectNodeList(roleTabTypeObjectNodeListMap, roleName, tabType, tabTypeNode);

		} else if (null != roleNameGroupTabTypeMap && null != roleNameGroupTabTypeMap.get(columnName)) {

			String tabType = roleNameGroupTabTypeMap.get(columnName);// radioGroupTabs

			ObjectNode tabTypeNode = objectMapper.createObjectNode();
			tabTypeNode.put("groupName", fieldName);

			if ("radioGroupTabs".equalsIgnoreCase(tabType)) {

				ObjectNode radioNode = objectMapper.createObjectNode();
				radioNode.put("value", rowColumnValue);
				radioNode.put("selected", true);

				ArrayNode radiosArray = objectMapper.createArrayNode();
				radiosArray.add(radioNode);

				tabTypeNode.putPOJO("radios", radiosArray);
			}

			populateRoleNameObjectNodeList(roleTabTypeObjectNodeListMap, roleName, tabType, tabTypeNode);
		}
	}

	private ObjectNode prepareAllRecipientsTypeNode(Map<String, List<ObjectNode>> recipientTypeObjectNodeListMap,
			Map<String, Map<String, List<ObjectNode>>> roleTabTypeObjectNodeListMap) {

		Map<String, ArrayNode> recipientTypeProperties = new HashMap<>();
		recipientTypeObjectNodeListMap.forEach((recipientType, recipientTypeNodeList) -> {

			logger.debug("recipientType " + recipientType + " recipientTypeNodeList size "
					+ recipientTypeNodeList.size() + " recipientTypeNodeList " + recipientTypeNodeList);
			ArrayNode recipientTypeArrayNode = objectMapper.createArrayNode();

			recipientTypeNodeList.forEach(recipientTypeNode -> {

				appendTabsToRecipientType(roleTabTypeObjectNodeListMap, recipientTypeArrayNode, recipientTypeNode);// This
																													// sets
																													// "tabs"
																													// property
																													// for
																													// each
																													// appropriate
																													// recipient

			});

			recipientTypeProperties.put(recipientType, recipientTypeArrayNode);
		});

		logger.debug("recipientTypeProperties " + recipientTypeProperties);

		ObjectNode allRecipientTypeNode = objectMapper.createObjectNode();
		allRecipientTypeNode.setAll(recipientTypeProperties);
		return allRecipientTypeNode;
	}

	private void populateRoleRelatedDataInRecipientNode(Map<String, ObjectNode> inputRoleNameProcessedMap,
			Map<String, String> roleNameAuthMap, Map<String, String> roleNamePhoneNumberMap,
			Map<String, ObjectNode> roleSignatureProviderOptionsNodePOJOMap,
			Map<String, ObjectNode> roleSignatureProviderNameNodeMap,
			Map<String, ObjectNode> roleEmailNotificationNodeMap) {

		inputRoleNameProcessedMap.forEach((roleKey, roleValue) -> {

			populateRoleRelatedAuthData(roleNameAuthMap, roleNamePhoneNumberMap, roleKey, roleValue);

			populateRoleRelatedSignatureProviderData(roleSignatureProviderOptionsNodePOJOMap,
					roleSignatureProviderNameNodeMap, roleKey, roleValue);

			populateRoleRelatedEmailNotificationData(roleEmailNotificationNodeMap, roleKey, roleValue);

		});
	}

	private void populateRoleRelatedEmailNotificationData(Map<String, ObjectNode> roleEmailNotificationNodeMap,
			String roleKey, ObjectNode roleValue) {

		if (null != roleEmailNotificationNodeMap && null != roleEmailNotificationNodeMap.get(roleKey)) {

			ObjectNode roleEmailNodePOJO = roleEmailNotificationNodeMap.get(roleKey);

			roleValue.putPOJO("emailNotification", roleEmailNodePOJO);
		}
	}

	private void populateRoleRelatedSignatureProviderData(
			Map<String, ObjectNode> roleSignatureProviderOptionsNodePOJOMap,
			Map<String, ObjectNode> roleSignatureProviderNameNodeMap, String roleKey, ObjectNode roleValue) {

		if (null != roleSignatureProviderOptionsNodePOJOMap
				&& null != roleSignatureProviderOptionsNodePOJOMap.get(roleKey)
				&& null != roleSignatureProviderNameNodeMap && null != roleSignatureProviderNameNodeMap.get(roleKey)) {

			ObjectNode signatureProviderOptionsPOJONode = roleSignatureProviderOptionsNodePOJOMap.get(roleKey);
			ObjectNode signatureProviderNameNode = roleSignatureProviderNameNodeMap.get(roleKey);

			if (null != signatureProviderOptionsPOJONode && null != signatureProviderNameNode) {

				signatureProviderNameNode.putPOJO("signatureProviderOptions", signatureProviderOptionsPOJONode);

				ArrayNode recipientSignatureProvidersArray = objectMapper.createArrayNode();
				recipientSignatureProvidersArray.add(signatureProviderNameNode);

				roleValue.putPOJO("recipientSignatureProviders", recipientSignatureProvidersArray);

			}
		}
	}

	private void populateRoleRelatedAuthData(Map<String, String> roleNameAuthMap,
			Map<String, String> roleNamePhoneNumberMap, String roleKey, ObjectNode roleValue) {

		if (null != roleNameAuthMap && null != roleNameAuthMap.get(roleKey)) {

			String phoneNumbers = roleNamePhoneNumberMap.get(roleKey);

			roleValue.put("idCheckConfigurationName",
					env.getProperty("ds.auth." + roleNameAuthMap.get(roleKey).trim().toLowerCase()));

			switch (roleNameAuthMap.get(roleKey).toLowerCase()) {

			case "sms":
				roleValue.putPOJO("smsAuthentication", createPhoneNumberArray(phoneNumbers, roleKey, "SMS"));
				break;
			case "phone":
				if ("usersupplied".equalsIgnoreCase(phoneNumbers)) {

					ObjectNode senderProvidedNumbersObjectNode = objectMapper.createObjectNode();
					senderProvidedNumbersObjectNode.put("recipMayProvideNumber", true);
					roleValue.putPOJO("phoneAuthentication", senderProvidedNumbersObjectNode);
				} else {

					ObjectNode senderProvidedNumbersObjectNode = createPhoneNumberArray(phoneNumbers, roleKey, "Phone");
					roleValue.putPOJO("phoneAuthentication", senderProvidedNumbersObjectNode);
				}
				break;
			}
		}
	}

	private void createCompositeTemplateNodeList(List<ObjectNode> compositeTemplateNodeList, String templateId,
			ArrayNode ecfArray, ObjectNode allRecipientTypeNode) {

		logger.info("Inside createCompositeTemplateNodeList for templateId " + templateId);

		ObjectNode inlineTemplateObjectNode = objectMapper.createObjectNode();
		inlineTemplateObjectNode.putPOJO("recipients", allRecipientTypeNode);
		inlineTemplateObjectNode.put("sequence", 2);

		if (null != ecfArray && !ecfArray.isEmpty()) {

			logger.info("Adding ECF in the CompositeTemplate");
			ObjectNode textCustomFieldsObjectNode = objectMapper.createObjectNode();
			textCustomFieldsObjectNode.putPOJO("textCustomFields", ecfArray);

			inlineTemplateObjectNode.set("customFields", textCustomFieldsObjectNode);
		}

		ArrayNode inlineTemplateArrayNode = objectMapper.createArrayNode();
		ArrayNode serverTemplateArrayNode = objectMapper.createArrayNode();

		inlineTemplateArrayNode.add(inlineTemplateObjectNode);

		ObjectNode severTemplateObjectNode = objectMapper.createObjectNode();
		severTemplateObjectNode.put("templateId", templateId);
		severTemplateObjectNode.put("sequence", 1);
		serverTemplateArrayNode.add(severTemplateObjectNode);

		ObjectNode compositeTemplateObjectNode = objectMapper.createObjectNode();
		compositeTemplateObjectNode.putPOJO("inlineTemplates", inlineTemplateArrayNode);
		compositeTemplateObjectNode.putPOJO("serverTemplates", serverTemplateArrayNode);

		logger.info("Adding one more compositeTemplate for templateId " + templateId);
		compositeTemplateNodeList.add(compositeTemplateObjectNode);
	}

	private void appendTabsToRecipientType(Map<String, Map<String, List<ObjectNode>>> roleTabTypeObjectNodeListMap,
			ArrayNode recipientTypeArrayNode, ObjectNode recipientTypeNode) {

		if (null != roleTabTypeObjectNodeListMap && !roleTabTypeObjectNodeListMap.isEmpty()) {

			String roleName = recipientTypeNode.findValue("roleName").asText();
			Map<String, List<ObjectNode>> tabTypeObjectNodeList = roleTabTypeObjectNodeListMap.get(roleName);
			Map<String, ArrayNode> properties = new HashMap<>();

			if (null != tabTypeObjectNodeList && !tabTypeObjectNodeList.isEmpty()) {

				tabTypeObjectNodeList.forEach((tabType, tabList) -> {

					logger.info("TabInfo -> tabType " + tabType + " tabList size " + tabList.size());
					logger.debug("tabType " + tabType + " tabList size " + tabList.size() + " tabList " + tabList);

					ArrayNode tabArrayNode = objectMapper.createArrayNode();
					tabList.forEach(tab -> {

						tabArrayNode.add(tab);
					});

					properties.put(tabType, tabArrayNode);
				});

				ObjectNode allTabsNode = objectMapper.createObjectNode();
				allTabsNode.setAll(properties);

				recipientTypeNode.set("tabs", allTabsNode);

			}
		}

		recipientTypeArrayNode.add(recipientTypeNode);

	}

	private void checkAndPopulateRecipientNode(ObjectNode recipientTypeNode, String nodeName, String nodeValue) {

		List<String> recipientFields = psProps.getRecipientFields();
		if (!StringUtils.isEmpty(nodeValue) && null != recipientFields
				&& recipientFields.stream().anyMatch(s -> s.equalsIgnoreCase(nodeName))) {

			recipientTypeNode.put(nodeName, nodeValue);
		}

	}

	private void populateRoleNameObjectNodeList(Map<String, Map<String, List<ObjectNode>>> roleTabTypeObjectNodeListMap,
			String roleName, String tabType, ObjectNode tabTypeNode) {

		logger.debug("tabType " + tabType + " roleName " + roleName);
		if (null != roleTabTypeObjectNodeListMap.get(roleName)) {

			Map<String, List<ObjectNode>> tabObjectNodeListMap = roleTabTypeObjectNodeListMap.get(roleName);

			if (null != tabObjectNodeListMap.get(tabType)) {

				List<ObjectNode> tabObjectNodeList = tabObjectNodeListMap.get(tabType);
				tabObjectNodeList.add(tabTypeNode);
			} else {

				List<ObjectNode> tabObjectNodeList = new ArrayList<ObjectNode>();
				tabObjectNodeList.add(tabTypeNode);

				tabObjectNodeListMap.put(tabType, tabObjectNodeList);
			}

		} else {

			List<ObjectNode> tabObjectNodeList = new ArrayList<ObjectNode>();
			tabObjectNodeList.add(tabTypeNode);

			Map<String, List<ObjectNode>> tabObjectNodeListMap = new HashMap<String, List<ObjectNode>>();
			tabObjectNodeListMap.put(tabType, tabObjectNodeList);

			roleTabTypeObjectNodeListMap.put(roleName, tabObjectNodeListMap);
		}
	}

	private ObjectNode createPhoneNumberArray(String phoneNumbers, String roleName, String authType) {

		if (!StringUtils.isEmpty(phoneNumbers)) {

			List<String> phoneNumbersList = Stream.of(phoneNumbers.split(AppConstants.SEMI_COLON_DELIMITER))
					.map(String::trim).collect(Collectors.toList());

			ArrayNode phnNumberArray = objectMapper.createArrayNode();
			phoneNumbersList.forEach(recPhoneNumber -> {

				phnNumberArray.add(recPhoneNumber);
			});

			ObjectNode senderProvidedNumbersObjectNode = objectMapper.createObjectNode();
			senderProvidedNumbersObjectNode.putPOJO("senderProvidedNumbers", phnNumberArray);
			return senderProvidedNumbersObjectNode;
		} else {
			throw new InvalidInputException(
					"PhoneNumber not passed as the input when setting up the authentication for " + roleName
							+ " authType " + authType);
		}

	}
}