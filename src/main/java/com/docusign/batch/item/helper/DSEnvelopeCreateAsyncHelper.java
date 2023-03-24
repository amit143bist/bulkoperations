package com.docusign.batch.item.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import com.docusign.batch.domain.AppConstants;
import com.docusign.batch.item.file.CustomFlatFileHeaderCallback;
import com.docusign.exception.InvalidInputException;
import com.docusign.proserv.application.utils.PSProperties;
import com.docusign.proserv.application.utils.PSUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class DSEnvelopeCreateAsyncHelper {

	final static Logger logger = LogManager.getLogger(DSEnvelopeCreateAsyncHelper.class);

	@Autowired
	private Environment env;

	@Autowired
	private PSProperties psProps;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private CustomFlatFileHeaderCallback customFlatFileHeaderCallback;

	private String headerLine;

	public String getHeaderLine() {
		return headerLine;
	}

	public void setHeaderLine(String headerLine) {
		this.headerLine = headerLine;
	}

	public String createBulkSendJSON(List<ObjectNode> createInlineTemplateNodeList, String fileName) {

		String bulkSendListJSON = null;
		ObjectNode bulkSendListNode = objectMapper.createObjectNode();
		if (null != createInlineTemplateNodeList && !createInlineTemplateNodeList.isEmpty()) {

			logger.info("createInlineTemplateNodeList size is " + createInlineTemplateNodeList.size());
			ArrayNode bulkCopiesArrayNode = objectMapper.createArrayNode();
			for (ObjectNode inlineTemplateNode : createInlineTemplateNodeList) {

				bulkCopiesArrayNode.add(inlineTemplateNode);
			}

			bulkSendListNode.putPOJO("bulkCopies", bulkCopiesArrayNode);
			bulkSendListNode.put("name", fileName);
		}

		try {

			bulkSendListJSON = objectMapper.writeValueAsString(bulkSendListNode);
		} catch (JsonProcessingException exp) {

			logger.error("JSON Processing error with exp message " + exp.getMessage());
			exp.printStackTrace();
		}

		return bulkSendListJSON;
	}

	public ObjectNode createInlineTemplateRequestNode(String[] rowAsArray, String fileName,
			List<Map<String, String>> envelopeIdDataList) {

		String[] headerAsArray = customFlatFileHeaderCallback.getHeaderArrayFromMap(fileName);

		for (String headerColumn : headerAsArray) {

			logger.debug("headerColumn in createInlineTemplateRequestNode " + headerColumn);
		}

		AtomicReference<ObjectNode> rowDataAsInlineTemplateNode = new AtomicReference<ObjectNode>();
		Map<String, String> envelopeLevelFieldMap = new HashMap<String, String>();
		Map<String, String> envelopeCustomFieldMap = new HashMap<String, String>();

		if (null != envelopeIdDataList && !envelopeIdDataList.isEmpty()) {// templateId
																			// included in
																			// the
																			// columnName

			Map<String, String> roleNameAuthMap = new HashMap<String, String>();
			Map<String, String> roleNamePhoneNumberMap = new HashMap<String, String>();

			Map<String, ObjectNode> inputRoleNameProcessedMap = new HashMap<String, ObjectNode>();

			Map<String, List<ObjectNode>> recipientTypeObjectNodeListMap = new HashMap<String, List<ObjectNode>>();
			Map<String, List<ObjectNode>> roleTabTypeObjectNodeListMap = new HashMap<String, List<ObjectNode>>();

			Map<String, ObjectNode> roleSignatureProviderNameNodeMap = new HashMap<String, ObjectNode>();

			Map<String, ObjectNode> roleEmailNotificationNodeMap = new HashMap<String, ObjectNode>();

			ArrayNode ecfArray = objectMapper.createArrayNode();

			Map<String, String> roleNameRecipientTypeMap = envelopeIdDataList.get(0);
			Map<String, String> roleNameLabelTabTypeMap = envelopeIdDataList.get(1);
			Map<String, String> roleNameGroupTabTypeMap = envelopeIdDataList.get(2);
			Map<String, String> roleNameRecipientIdMap = envelopeIdDataList.get(3);

			logger.debug("headerAsArray length >> " + headerAsArray.length);
			logger.debug("rowAsArray length >> " + rowAsArray.length);

			for (int i = 0; i < headerAsArray.length && i < rowAsArray.length; i++) {

				if (!StringUtils.isEmpty(rowAsArray[i])) {

					String columnName = headerAsArray[i].trim();

					if (i == 0) {

						logger.debug("columnName is " + columnName + " and columnValue is " + rowAsArray[i]);
					}

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

								recipientTypeNode.put("recipientId", roleNameRecipientIdMap.get(roleName));
								recipientTypeNode.put("role", roleName);
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
										roleTabTypeObjectNodeListMap, rowAsArray[i], columnName, roleName, fieldName);
							} else {

								String fieldNameLC = fieldName.toLowerCase().replaceAll(AppConstants.WHITESPACE_REGEX,
										"");

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
									signatureProviderNameNode.put("signatureProviderDisplayName", rowColumnValue);

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

									prepareEmailNotificationRoleBasedNodeMap(roleEmailNotificationNodeMap, roleName,
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

								envelopeCustomFieldMap.put(columnName, rowAsArray[i]);

							}
						}
					}
				}

			}

			if (null != envelopeCustomFieldMap && !envelopeCustomFieldMap.isEmpty()) {

				envelopeCustomFieldMap.forEach((key, value) -> {

					ObjectNode textField = objectMapper.createObjectNode();
					textField.put("name", key);
					textField.put("value", value);

					ecfArray.add(textField);
				});
			}

			if (null != ecfArray) {// Add CSV FileName as ECF

				ObjectNode textField = objectMapper.createObjectNode();
				textField.put("name", "fileName");
				textField.put("value", fileName);

				ecfArray.add(textField);
			}

			populateRoleRelatedDataInRecipientNode(inputRoleNameProcessedMap, roleNameAuthMap, roleNamePhoneNumberMap,
					roleSignatureProviderNameNodeMap, roleEmailNotificationNodeMap);// This
																					// sets
																					// all
																					// fields
																					// for
																					// each
																					// role

			ArrayNode allRecipientTypeNode = prepareAllRecipientsTypeNode(recipientTypeObjectNodeListMap,
					roleTabTypeObjectNodeListMap);// This sets "recipients" property

			rowDataAsInlineTemplateNode.set(createInlineTemplateNode(ecfArray, allRecipientTypeNode));

		} else {

			throw new RuntimeException("Some issue in reading or processing template");
		}

		return rowDataAsInlineTemplateNode.get();
	}

	private void prepareEmailNotificationRoleBasedNodeMap(Map<String, ObjectNode> roleEmailNotificationNodeMap,
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

		if ("idcheck".equalsIgnoreCase(identificationType)) {

			recipientTypeNode.put("idCheckConfigurationName", "ID Check$");
		} else {

			roleNameAuthMap.put(roleName, identificationType);
		}

	}

	private void prepareTabCollectionForEachRole(Map<String, String> roleNameLabelTabTypeMap,
			Map<String, String> roleNameGroupTabTypeMap, Map<String, List<ObjectNode>> roleTabTypeObjectNodeListMap,
			String rowColumnValue, String columnName, String roleName, String fieldName) {

		String tabType = null;
		ObjectNode tabTypeNode = objectMapper.createObjectNode();

		if (null != roleNameLabelTabTypeMap && null != roleNameLabelTabTypeMap.get(columnName)) {

			tabType = roleNameLabelTabTypeMap.get(columnName);// textTabs,numberTabs,ssnTabs,dateTabs,zipTabs,emailTabs,checkboxTabs,listTabs,formulaTabs

			if ("checkboxTabs".equalsIgnoreCase(tabType)) {

				if (!"X".equalsIgnoreCase(rowColumnValue)) {

					throw new InvalidInputException(rowColumnValue + " is invalid for " + columnName);
				}

			}

		} else if (null != roleNameGroupTabTypeMap && null != roleNameGroupTabTypeMap.get(columnName)) {

			tabType = roleNameGroupTabTypeMap.get(columnName);// radioGroupTabs

		}

		tabTypeNode.put("tabLabel", fieldName);
		if (psProps.getReplaceTabData() && null != psProps.getReplaceTabLabels()
				&& !psProps.getReplaceTabLabels().isEmpty() && psProps.getReplaceTabLabels().contains(fieldName)) {

			String tabLabelFromChar = env
					.getProperty("ds.account.bulksend.replace.tabdata.tablabels." + fieldName + ".fromchar");
			String tabLabelToChar = env
					.getProperty("ds.account.bulksend.replace.tabdata.tablabels." + fieldName + ".tochar");

			tabTypeNode.put("initialValue", rowColumnValue.replaceAll(PSUtils.escapeMetaCharacters(tabLabelFromChar),
					PSUtils.escapeMetaCharacters(tabLabelToChar)));

		} else {

			tabTypeNode.put("initialValue", rowColumnValue);
		}

		populateRoleNameObjectNodeList(roleTabTypeObjectNodeListMap, roleName, tabType, tabTypeNode);
	}

	private ArrayNode prepareAllRecipientsTypeNode(Map<String, List<ObjectNode>> recipientTypeObjectNodeListMap,
			Map<String, List<ObjectNode>> roleTabTypeObjectNodeListMap) {

		ArrayNode recipientTypeArrayNode = objectMapper.createArrayNode();
		recipientTypeObjectNodeListMap.forEach((recipientType, recipientTypeNodeList) -> {

			logger.debug("recipientType " + recipientType + " recipientTypeNodeList size "
					+ recipientTypeNodeList.size() + " recipientTypeNodeList " + recipientTypeNodeList);
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

		});

		logger.debug("recipientTypeArrayNode " + recipientTypeArrayNode);

		return recipientTypeArrayNode;
	}

	private void populateRoleRelatedDataInRecipientNode(Map<String, ObjectNode> inputRoleNameProcessedMap,
			Map<String, String> roleNameAuthMap, Map<String, String> roleNamePhoneNumberMap,
			Map<String, ObjectNode> roleSignatureProviderNameNodeMap,
			Map<String, ObjectNode> roleEmailNotificationNodeMap) {

		inputRoleNameProcessedMap.forEach((roleKey, roleValue) -> {

			populateRoleRelatedAuthData(roleNameAuthMap, roleNamePhoneNumberMap, roleKey, roleValue);

			populateRoleRelatedSignatureProviderData(roleSignatureProviderNameNodeMap, roleKey, roleValue);

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

	private void populateRoleRelatedSignatureProviderData(Map<String, ObjectNode> roleSignatureProviderNameNodeMap,
			String roleKey, ObjectNode roleValue) {

		if (null != roleSignatureProviderNameNodeMap && null != roleSignatureProviderNameNodeMap.get(roleKey)) {

			ObjectNode signatureProviderNameNode = roleSignatureProviderNameNodeMap.get(roleKey);

			if (null != signatureProviderNameNode) {

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

			roleValue.put("phonenumber", phoneNumbers);

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
					senderProvidedNumbersObjectNode.put("recipMayProvideNumber", false);
					senderProvidedNumbersObjectNode.put("validateRecipProvidedNumber", true);
					senderProvidedNumbersObjectNode.put("recordVoicePrint", false);
					roleValue.putPOJO("phoneAuthentication", senderProvidedNumbersObjectNode);
				}
				break;
			}
		}
	}

	private ObjectNode createInlineTemplateNode(ArrayNode ecfArray, ArrayNode allRecipientTypeNode) {

		ObjectNode inlineTemplateObjectNode = objectMapper.createObjectNode();
		inlineTemplateObjectNode.putPOJO("recipients", allRecipientTypeNode);

		if (null != ecfArray && !ecfArray.isEmpty()) {

			logger.debug("Adding ECF in the CompositeTemplate");

			inlineTemplateObjectNode.putPOJO("customFields", ecfArray);
		}

		return inlineTemplateObjectNode;
	}

	private void appendTabsToRecipientType(Map<String, List<ObjectNode>> roleTabTypeObjectNodeListMap,
			ArrayNode recipientTypeArrayNode, ObjectNode recipientTypeNode) {

		if (null != roleTabTypeObjectNodeListMap && !roleTabTypeObjectNodeListMap.isEmpty()) {

			String roleName = recipientTypeNode.findValue("role").asText();
			List<ObjectNode> tabTypeObjectNodeList = roleTabTypeObjectNodeListMap.get(roleName);

			if (null != tabTypeObjectNodeList && !tabTypeObjectNodeList.isEmpty()) {

				ArrayNode tabArrayNode = objectMapper.createArrayNode();
				tabTypeObjectNodeList.forEach(tab -> {

					tabArrayNode.add(tab);
				});

				recipientTypeNode.putPOJO("tabs", tabArrayNode);

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

	private void populateRoleNameObjectNodeList(Map<String, List<ObjectNode>> roleTabTypeObjectNodeListMap,
			String roleName, String tabType, ObjectNode tabTypeNode) {

		logger.debug("tabType " + tabType + " roleName " + roleName);
		if (null != roleTabTypeObjectNodeListMap.get(roleName)) {

			List<ObjectNode> tabObjectNodeList = roleTabTypeObjectNodeListMap.get(roleName);

			tabObjectNodeList.add(tabTypeNode);

		} else {

			List<ObjectNode> tabObjectNodeList = new ArrayList<ObjectNode>();
			tabObjectNodeList.add(tabTypeNode);

			roleTabTypeObjectNodeListMap.put(roleName, tabObjectNodeList);
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