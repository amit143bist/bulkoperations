package com.docusign.batch.item.file;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import com.docusign.batch.domain.AppConstants;
import com.docusign.batch.domain.AppParameters;
import com.docusign.proserv.application.cache.CacheManager;
import com.docusign.proserv.application.utils.PSProperties;
import com.docusign.proserv.application.webservice.ProServServiceTemplate;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.ReadContext;

public abstract class AbstractDSFieldExtractor {

	@Autowired
	Environment env;

	@Autowired
	PSProperties psProps;

	@Autowired
	CacheManager cacheManager;

	@Autowired
	AppParameters appParameters;

	@Autowired
	ProServServiceTemplate proServServiceTemplate;

	protected void createMaps(String templateOrDraftEnvelopeId,
			Map<String, List<Map<String, String>>> envelopeTemplateIdDataMap, String envelopeTemplateJSON) {

		Map<String, String> roleNameRecipientTypeMap = new HashMap<String, String>();
		Map<String, String> roleNameLabelTabTypeMap = new HashMap<String, String>();
		Map<String, String> roleNameGroupTabTypeMap = new HashMap<String, String>();
		Map<String, String> roleNameRecipientIdMap = new HashMap<String, String>();

		Configuration pathConfiguration = Configuration.builder()
				.options(Option.SUPPRESS_EXCEPTIONS, Option.DEFAULT_PATH_LEAF_TO_NULL).build();
		ReadContext ctx = JsonPath.using(pathConfiguration).parse(envelopeTemplateJSON);

		List<String> recipientTypes = Stream.of(psProps.getTemplatesRecipientTypes().split(",")).map(String::trim)
				.collect(Collectors.toList());
		List<String> labelTabTypes = Stream.of(psProps.getTemplatesLabelTabTypes().split(",")).map(String::trim)
				.collect(Collectors.toList());
		List<String> groupTabTypes = Stream.of(psProps.getTemplatesGroupTabTypes().split(",")).map(String::trim)
				.collect(Collectors.toList());

		for (String recipientType : recipientTypes) {

			List<Object> roleNames = ctx.read("$." + recipientType + "[*].roleName");

			if (null != roleNames && !roleNames.isEmpty()) {

				roleNames.forEach(roleName -> {

					roleNameRecipientTypeMap.put((String) roleName, recipientType);

					List<Object> tabCountArray = ctx.read(
							"$." + recipientType + "[?(@.roleName ==" + "'" + roleName + "'" + ")].totalTabCount");

					Integer tabCount = Integer.parseInt((String) tabCountArray.get(0));

					if (tabCount > 0) {

						if (null != labelTabTypes && !labelTabTypes.isEmpty()) {

							for (String tabType : labelTabTypes) {

								List<Object> tabLabelArray = ctx.read("$." + recipientType + "[?(@.roleName ==" + "'"
										+ roleName + "'" + ")].tabs." + tabType + "[*].tabLabel");
								tabLabelArray.forEach(tabLabel -> {

									roleNameLabelTabTypeMap.put(roleName + AppConstants.BULK_CSV_DELIMITER + tabLabel,
											tabType);
								});
							}
						}

						if (null != groupTabTypes && !groupTabTypes.isEmpty()) {

							for (String groupTabType : groupTabTypes) {

								List<Object> tabLabelArray = ctx.read("$." + recipientType + "[?(@.roleName ==" + "'"
										+ roleName + "'" + ")].tabs." + groupTabType + "[*].groupName");
								tabLabelArray.forEach(tabLabel -> {

									roleNameGroupTabTypeMap.put(roleName + AppConstants.BULK_CSV_DELIMITER + tabLabel,
											groupTabType);
								});
							}
						}
					}

					List<Object> recipientIdArray = ctx
							.read("$." + recipientType + "[?(@.roleName ==" + "'" + roleName + "'" + ")].recipientId");

					if (null != recipientIdArray && !recipientIdArray.isEmpty() && recipientIdArray.size() == 1) {

						roleNameRecipientIdMap.put((String) roleName, (String) recipientIdArray.get(0));
					}
				});
			}
		}

		List<Map<String, String>> mapList = new ArrayList<Map<String, String>>(2);
		mapList.add(roleNameRecipientTypeMap);
		mapList.add(roleNameLabelTabTypeMap);
		mapList.add(roleNameGroupTabTypeMap);
		mapList.add(roleNameRecipientIdMap);

		envelopeTemplateIdDataMap.put(templateOrDraftEnvelopeId, mapList);

	}
}